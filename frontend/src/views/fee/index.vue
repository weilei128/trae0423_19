<template>
  <div class="fee-page">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>总收入</span>
          </template>
          <div class="stat-value" style="color: #67C23A;">
            {{ stats.totalIncome ? `¥${stats.totalIncome.toLocaleString()}` : '¥0' }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>总支出</span>
          </template>
          <div class="stat-value" style="color: #F56C6C;">
            {{ stats.totalExpense ? `¥${stats.totalExpense.toLocaleString()}` : '¥0' }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>净利润</span>
          </template>
          <div class="stat-value" style="color: #409EFF;">
            {{ stats.netProfit ? `¥${stats.netProfit.toLocaleString()}` : '¥0' }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <span>回款率</span>
          </template>
          <div class="stat-value" style="color: #E6A23C;">
            {{ stats.totalIncome > 0 ? Math.round((stats.totalIncome / (stats.totalIncome + stats.totalExpense)) * 100) : 0 }}%
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>费用记录</span>
          <el-button type="primary" @click="handleAdd" :disabled="!canEdit">
            <el-icon><Plus /></el-icon>
            新增费用
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="费用类型">
          <el-select v-model="searchForm.feeType" placeholder="请选择费用类型" clearable>
            <el-option label="收入" value="INCOME" />
            <el-option label="支出" value="EXPENSE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="feeNo" label="费用编号" width="180" />
        <el-table-column prop="feeName" label="费用名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="feeType" label="费用类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.feeType === 'INCOME' ? 'success' : 'danger'">
              {{ row.feeType === 'INCOME' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.feeType === 'INCOME' ? 'income-amount' : 'expense-amount'">
              {{ row.feeType === 'INCOME' ? '+' : '-' }}¥{{ row.amount?.toLocaleString() || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="feeCategory" label="费用分类" width="120" />
        <el-table-column prop="payerPayee" label="收付方" width="120" />
        <el-table-column prop="paymentDate" label="收付日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" :disabled="!canEdit">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)" :disabled="!canDelete">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-alert
      title="功能说明"
      type="info"
      :closable="false"
      style="margin-top: 20px;"
    >
      <template #default>
        <ul>
          <li>费用管理：律师费收取、支出记录、欠款催收</li>
          <li>依赖合同：费用记录关联案件和合同</li>
          <li>权限控制：合伙人可删除，律师可编辑，助理仅可查看</li>
        </ul>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getFees, createFee, updateFee, deleteFee, getFeeSummary } from '@/api'

const userStore = useUserStore()

const canEdit = computed(() => userStore.isLawyer())
const canDelete = computed(() => userStore.isPartner())

const loading = ref(false)
const tableData = ref([])

const stats = reactive({
  totalIncome: 0,
  totalExpense: 0,
  netProfit: 0
})

const searchForm = reactive({
  feeType: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loadStats = async () => {
  try {
    const res = await getFeeSummary()
    if (res.code === 200) {
      stats.totalIncome = res.data.totalIncome || 0
      stats.totalExpense = res.data.totalExpense || 0
      stats.netProfit = res.data.netProfit || 0
    }
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null) {
        delete params[key]
      }
    })
    
    const res = await getFees(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.feeType = ''
  handleSearch()
}

const handleAdd = () => {
  ElMessage.info('费用新增功能，请先创建合同后再进行费用登记')
}

const handleEdit = (row) => {
  ElMessage.info('费用编辑功能正在完善中')
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该费用记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteFee(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
        loadStats()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  loadStats()
  loadData()
})
</script>

<style lang="scss" scoped>
.fee-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .stat-value {
    font-size: 28px;
    font-weight: bold;
  }

  .income-amount {
    color: #67C23A;
    font-weight: bold;
  }

  .expense-amount {
    color: #F56C6C;
    font-weight: bold;
  }
}
</style>
