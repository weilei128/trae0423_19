<template>
  <div class="case-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>案件管理</span>
          <el-button type="primary" @click="handleAdd" :disabled="!canEdit">
            <el-icon><Plus /></el-icon>
            新增案件
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="案件名称">
          <el-input v-model="searchForm.caseName" placeholder="请输入案件名称" clearable />
        </el-form-item>
        <el-form-item label="案件状态">
          <el-select v-model="searchForm.caseStatus" placeholder="请选择案件状态" clearable>
            <el-option label="新建" value="NEW" />
            <el-option label="审理中" value="PROCESSING" />
            <el-option label="已结案" value="CLOSED" />
            <el-option label="中止" value="SUSPENDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="caseNo" label="案件编号" width="180" />
        <el-table-column prop="caseName" label="案件名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="clientName" label="客户名称" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.clientName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="caseStatus" label="案件状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.caseStatus)">
              {{ getStatusText(row.caseStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="caseLevel" label="案件级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.caseLevel)" size="small">
              {{ getLevelText(row.caseLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="court" label="受理法院" show-overflow-tooltip />
        <el-table-column prop="amount" label="涉案金额" width="120">
          <template #default="{ row }">
            {{ row.amount ? `¥${row.amount.toLocaleString()}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
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
            <el-form-item label="案件名称" prop="caseName">
              <el-input v-model="formData.caseName" placeholder="请输入案件名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案件类型" prop="caseTypeId">
              <el-select v-model="formData.caseTypeId" placeholder="请选择案件类型" style="width: 100%;">
                <el-option
                  v-for="item in caseTypeList"
                  :key="item.id"
                  :label="item.typeName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户" prop="clientId">
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
          <el-col :span="12">
            <el-form-item label="案件级别" prop="caseLevel">
              <el-select v-model="formData.caseLevel" placeholder="请选择案件级别" style="width: 100%;">
                <el-option label="普通" value="NORMAL" />
                <el-option label="重要" value="IMPORTANT" />
                <el-option label="紧急" value="EMERGENCY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="对方当事人">
              <el-input v-model="formData.oppositeParty" placeholder="请输入对方当事人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="对方代理人">
              <el-input v-model="formData.oppositeAgent" placeholder="请输入对方代理人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="对方律所">
              <el-input v-model="formData.oppositeLawFirm" placeholder="请输入对方律所" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="受理法院">
              <el-input v-model="formData.court" placeholder="请输入受理法院" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="立案日期">
              <el-date-picker
                v-model="formData.caseDate"
                type="date"
                placeholder="请选择立案日期"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="承办法官">
              <el-input v-model="formData.judge" placeholder="请输入承办法官" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="涉案金额">
              <el-input-number
                v-model="formData.amount"
                :precision="2"
                :min="0"
                placeholder="请输入涉案金额"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案件阶段">
              <el-select v-model="formData.caseStage" placeholder="请选择案件阶段" clearable style="width: 100%;">
                <el-option label="一审" value="一审" />
                <el-option label="二审" value="二审" />
                <el-option label="再审" value="再审" />
                <el-option label="执行" value="执行" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="案件描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入案件描述"
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

    <el-dialog v-model="detailVisible" title="案件详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="案件编号">{{ detailData.caseNo }}</el-descriptions-item>
        <el-descriptions-item label="案件名称">{{ detailData.caseName }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ detailData.clientName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="案件级别">
          {{ getLevelText(detailData.caseLevel) }}
        </el-descriptions-item>
        <el-descriptions-item label="案件状态">
          <el-tag :type="getStatusType(detailData.caseStatus)">
            {{ getStatusText(detailData.caseStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="涉案金额">
          {{ detailData.amount ? `¥${detailData.amount.toLocaleString()}` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="对方当事人">{{ detailData.oppositeParty || '-' }}</el-descriptions-item>
        <el-descriptions-item label="对方代理人">{{ detailData.oppositeAgent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="对方律所">{{ detailData.oppositeLawFirm || '-' }}</el-descriptions-item>
        <el-descriptions-item label="受理法院">{{ detailData.court || '-' }}</el-descriptions-item>
        <el-descriptions-item label="立案日期">{{ detailData.caseDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="承办法官">{{ detailData.judge || '-' }}</el-descriptions-item>
        <el-descriptions-item label="案件描述" :span="2">
          {{ detailData.description || '暂无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  getCases,
  createCase,
  updateCase,
  deleteCase,
  getCaseTypes,
  getAllClients,
  getClient
} from '@/api'

const userStore = useUserStore()

const canEdit = computed(() => userStore.isLawyer())
const canDelete = computed(() => userStore.isPartner())

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增案件')
const formRef = ref(null)

const searchForm = reactive({
  caseName: '',
  caseStatus: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const caseTypeList = ref([])
const clientList = ref([])

const formData = reactive({
  id: null,
  caseName: '',
  caseTypeId: null,
  caseLevel: 'NORMAL',
  clientId: null,
  oppositeParty: '',
  oppositeAgent: '',
  oppositeLawFirm: '',
  court: '',
  caseDate: '',
  judge: '',
  amount: null,
  caseStage: '',
  description: ''
})

const detailData = ref({})

const formRules = {
  caseName: [{ required: true, message: '请输入案件名称', trigger: 'blur' }],
  clientId: [{ required: true, message: '请选择客户', trigger: 'change' }]
}

const getStatusType = (status) => {
  const typeMap = {
    'NEW': 'info',
    'PROCESSING': 'primary',
    'CLOSED': 'success',
    'SUSPENDED': 'warning'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'NEW': '新建',
    'PROCESSING': '审理中',
    'CLOSED': '已结案',
    'SUSPENDED': '中止'
  }
  return textMap[status] || status
}

const getLevelType = (level) => {
  const typeMap = {
    'NORMAL': 'info',
    'IMPORTANT': 'warning',
    'EMERGENCY': 'danger'
  }
  return typeMap[level] || 'info'
}

const getLevelText = (level) => {
  const textMap = {
    'NORMAL': '普通',
    'IMPORTANT': '重要',
    'EMERGENCY': '紧急'
  }
  return textMap[level] || '普通'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const loadCaseTypes = async () => {
  try {
    const res = await getCaseTypes()
    if (res.code === 200) {
      caseTypeList.value = res.data || []
    }
  } catch (error) {
    console.error('加载案件类型失败:', error)
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
    
    const res = await getCases(params)
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
  searchForm.caseName = ''
  searchForm.caseStatus = ''
  handleSearch()
}

const resetForm = () => {
  formData.id = null
  formData.caseName = ''
  formData.caseTypeId = null
  formData.caseLevel = 'NORMAL'
  formData.clientId = null
  formData.oppositeParty = ''
  formData.oppositeAgent = ''
  formData.oppositeLawFirm = ''
  formData.court = ''
  formData.caseDate = ''
  formData.judge = ''
  formData.amount = null
  formData.caseStage = ''
  formData.description = ''
}

const handleAdd = async () => {
  if (clientList.value.length === 0) {
    await loadClients()
  }
  if (clientList.value.length === 0) {
    ElMessage.warning('请先创建客户档案')
    return
  }
  if (caseTypeList.value.length === 0) {
    await loadCaseTypes()
  }
  resetForm()
  dialogTitle.value = '新增案件'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  if (clientList.value.length === 0) {
    await loadClients()
  }
  if (caseTypeList.value.length === 0) {
    await loadCaseTypes()
  }
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑案件'
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该案件吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteCase(row.id)
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
        
        let res
        if (data.id) {
          res = await updateCase(data.id, data)
        } else {
          res = await createCase(data)
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
    loadCaseTypes(),
    loadClients()
  ])
  loadData()
})
</script>

<style lang="scss" scoped>
.case-page {
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
