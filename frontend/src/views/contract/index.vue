<template>
  <div class="contract-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>合同管理</span>
          <el-button type="primary" @click="handleAdd" :disabled="!canEdit">
            <el-icon><Plus /></el-icon>
            新增合同
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="合同名称">
          <el-input v-model="searchForm.contractName" placeholder="请输入合同名称" clearable />
        </el-form-item>
        <el-form-item label="签署状态">
          <el-select v-model="searchForm.signStatus" placeholder="请选择签署状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已签署" value="SIGNED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="contractNo" label="合同编号" width="200" />
        <el-table-column prop="contractName" label="合同名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="caseName" label="关联案件" width="150">
          <template #default="{ row }">
            <el-tag size="small">{{ row.caseName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contractType" label="合同类型" width="120" />
        <el-table-column prop="signStatus" label="签署状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.signStatus === 'SIGNED' ? 'success' : 'info'">
              {{ row.signStatus === 'SIGNED' ? '已签署' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalFee" label="约定金额" width="120">
          <template #default="{ row }">
            {{ row.totalFee ? `¥${row.totalFee.toLocaleString()}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签订日期" width="120" />
        <el-table-column prop="version" label="版本" width="80">
          <template #default="{ row }">
            v{{ row.version || 1 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" :disabled="!canEdit">
              编辑
            </el-button>
            <el-button type="primary" link @click="handleView(row)">
              详情
            </el-button>
            <el-button type="primary" link @click="handleSign(row)" :disabled="!canEdit || row.signStatus === 'SIGNED'">
              签署
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同名称" prop="contractName">
              <el-input v-model="formData.contractName" placeholder="请输入合同名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型">
              <el-select v-model="formData.contractType" placeholder="请选择合同类型" clearable style="width: 100%;">
                <el-option label="委托代理合同" value="委托代理合同" />
                <el-option label="法律顾问合同" value="法律顾问合同" />
                <el-option label="专项服务合同" value="专项服务合同" />
                <el-option label="其他合同" value="其他合同" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联案件" prop="caseId">
              <el-select
                v-model="formData.caseId"
                placeholder="请选择关联案件"
                filterable
                style="width: 100%;"
                @change="handleCaseChange"
              >
                <el-option
                  v-for="item in caseList"
                  :key="item.id"
                  :label="item.caseName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户">
              <el-select
                v-model="formData.clientId"
                placeholder="请选择客户"
                filterable
                style="width: 100%;"
              >
                <el-option
                  v-for="item in clientList"
                  :key="item.id"
                  :label="item.clientName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="签订日期">
              <el-date-picker
                v-model="formData.signDate"
                type="date"
                placeholder="请选择签订日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期">
              <el-date-picker
                v-model="formData.expireDate"
                type="date"
                placeholder="请选择到期日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="律师费总额">
              <el-input-number
                v-model="formData.totalFee"
                :precision="2"
                :min="0"
                placeholder="请输入律师费总额"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款方式">
              <el-input v-model="formData.paymentMethod" placeholder="请输入付款方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="合同内容">
          <el-input
            v-model="formData.contractContent"
            type="textarea"
            :rows="6"
            placeholder="请输入合同内容"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="合同详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ detailData.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ detailData.contractName }}</el-descriptions-item>
        <el-descriptions-item label="关联案件">{{ detailData.caseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">{{ detailData.contractType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="签署状态">
          <el-tag :type="detailData.signStatus === 'SIGNED' ? 'success' : 'info'">
            {{ detailData.signStatus === 'SIGNED' ? '已签署' : '草稿' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="律师费总额">
          {{ detailData.totalFee ? `¥${detailData.totalFee.toLocaleString()}` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ detailData.signDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ detailData.expireDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="付款方式">{{ detailData.paymentMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="版本">v{{ detailData.version || 1 }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ detailData.remark || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="合同内容" :span="2">
          <div style="white-space: pre-wrap; max-height: 300px; overflow-y: auto;">
            {{ detailData.contractContent || '暂无' }}
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  getContracts,
  createContract,
  updateContract,
  deleteContract,
  signContract,
  getAllCases,
  getAllClients
} from '@/api'

const userStore = useUserStore()

const canEdit = computed(() => userStore.isLawyer())
const canDelete = computed(() => userStore.isPartner())

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增合同')
const formRef = ref(null)

const searchForm = reactive({
  contractName: '',
  signStatus: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const caseList = ref([])
const clientList = ref([])

const formData = reactive({
  id: null,
  contractName: '',
  contractType: '',
  caseId: null,
  clientId: null,
  signDate: '',
  expireDate: '',
  totalFee: null,
  paymentMethod: '',
  contractContent: '',
  remark: ''
})

const detailData = ref({})

const formRules = {
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  caseId: [{ required: true, message: '请选择关联案件', trigger: 'change' }]
}

const loadCases = async () => {
  try {
    const res = await getAllCases()
    if (res.code === 200) {
      caseList.value = res.data || []
    }
  } catch (error) {
    console.error('加载案件列表失败:', error)
  }
}

const loadClients = async () => {
  try {
    const res = await getAllClients()
    if (res.code === 200) {
      clientList.value = res.data || []
    }
  } catch (error) {
    console.error('加载客户列表失败:', error)
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
    
    const res = await getContracts(params)
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

const handleCaseChange = (caseId) => {
  const selectedCase = caseList.value.find(c => c.id === caseId)
  if (selectedCase && selectedCase.clientId) {
    formData.clientId = selectedCase.clientId
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.contractName = ''
  searchForm.signStatus = ''
  handleSearch()
}

const resetForm = () => {
  formData.id = null
  formData.contractName = ''
  formData.contractType = ''
  formData.caseId = null
  formData.clientId = null
  formData.signDate = ''
  formData.expireDate = ''
  formData.totalFee = null
  formData.paymentMethod = ''
  formData.contractContent = ''
  formData.remark = ''
}

const handleAdd = async () => {
  if (caseList.value.length === 0) {
    await loadCases()
  }
  if (caseList.value.length === 0) {
    ElMessage.warning('请先创建案件')
    return
  }
  if (clientList.value.length === 0) {
    await loadClients()
  }
  resetForm()
  dialogTitle.value = '新增合同'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  if (caseList.value.length === 0) {
    await loadCases()
  }
  if (clientList.value.length === 0) {
    await loadClients()
  }
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑合同'
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleSign = (row) => {
  ElMessageBox.confirm('确定要签署该合同吗？签署后将无法撤销。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await signContract(row.id)
      if (res.code === 200) {
        ElMessage.success('合同签署成功')
        loadData()
      }
    } catch (error) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该合同吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteContract(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = { ...formData }
        data.signStatus = 'DRAFT'
        data.version = 1
        
        let res
        if (data.id) {
          res = await updateContract(data.id, data)
        } else {
          res = await createContract(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(data.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error(error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(async () => {
  await Promise.all([
    loadCases(),
    loadClients()
  ])
  loadData()
})
</script>

<style lang="scss" scoped>
.contract-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }
}
</style>
