<template>
  <div class="client-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户档案</span>
          <el-button type="primary" @click="handleAdd" :disabled="!canEdit">
            <el-icon><Plus /></el-icon>
            新增客户
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.clientName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="searchForm.clientType" placeholder="请选择客户类型" clearable>
            <el-option label="个人" value="PERSON" />
            <el-option label="企业" value="COMPANY" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="searchForm.phone" placeholder="请输入联系电话" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="clientName" label="客户名称" />
        <el-table-column prop="clientType" label="客户类型">
          <template #default="{ row }">
            <el-tag :type="row.clientType === 'PERSON' ? '' : 'primary'">
              {{ row.clientType === 'PERSON' ? '个人' : '企业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="电子邮箱" />
        <el-table-column prop="address" label="联系地址" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
      width="700px"
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
            <el-form-item label="客户名称" prop="clientName">
              <el-input v-model="formData.clientName" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="clientType">
              <el-select v-model="formData.clientType" placeholder="请选择客户类型" style="width: 100%;">
                <el-option label="个人" value="PERSON" />
                <el-option label="企业" value="COMPANY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号/信用代码" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电子邮箱">
              <el-input v-model="formData.email" placeholder="请输入电子邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业" v-if="formData.clientType === 'COMPANY'">
              <el-input v-model="formData.industry" placeholder="请输入所属行业" />
            </el-form-item>
            <el-form-item label="法定代表人" v-if="formData.clientType === 'COMPANY'">
              <el-input v-model="formData.legalPerson" placeholder="请输入法定代表人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="联系地址">
          <el-input v-model="formData.address" type="textarea" :rows="2" placeholder="请输入联系地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="客户详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="客户名称">{{ detailData.clientName }}</el-descriptions-item>
        <el-descriptions-item label="客户类型">
          {{ detailData.clientType === 'PERSON' ? '个人' : '企业' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="电子邮箱">{{ detailData.email }}</el-descriptions-item>
        <el-descriptions-item label="身份证号/信用代码" :span="2">
          {{ detailData.idCard }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.clientType === 'COMPANY'" label="所属行业">
          {{ detailData.industry }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.clientType === 'COMPANY'" label="法定代表人">
          {{ detailData.legalPerson }}
        </el-descriptions-item>
        <el-descriptions-item label="联系地址" :span="2">
          {{ detailData.address }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ detailData.remark || '暂无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getClients, createClient, updateClient, deleteClient } from '@/api'

const userStore = useUserStore()

const canEdit = computed(() => userStore.isLawyer())
const canDelete = computed(() => userStore.isPartner())

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增客户')
const formRef = ref(null)

const searchForm = reactive({
  clientName: '',
  clientType: '',
  phone: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const formData = reactive({
  id: null,
  clientName: '',
  clientType: 'PERSON',
  idCard: '',
  phone: '',
  email: '',
  address: '',
  industry: '',
  legalPerson: '',
  remark: ''
})

const detailData = ref({})

const formRules = {
  clientName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  clientType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
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
    
    const res = await getClients(params)
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
  searchForm.clientName = ''
  searchForm.clientType = ''
  searchForm.phone = ''
  handleSearch()
}

const resetForm = () => {
  formData.id = null
  formData.clientName = ''
  formData.clientType = 'PERSON'
  formData.idCard = ''
  formData.phone = ''
  formData.email = ''
  formData.address = ''
  formData.industry = ''
  formData.legalPerson = ''
  formData.remark = ''
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增客户'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(formData, row)
  dialogTitle.value = '编辑客户'
  dialogVisible.value = true
}

const handleView = (row) => {
  detailData.value = { ...row }
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteClient(row.id)
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
        if (data.clientType !== 'COMPANY') {
          delete data.industry
          delete data.legalPerson
        }
        
        let res
        if (data.id) {
          res = await updateClient(data.id, data)
        } else {
          res = await createClient(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(data.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.client-page {
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
