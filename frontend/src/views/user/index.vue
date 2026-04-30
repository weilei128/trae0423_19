<template>
  <div class="user-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <el-table :data="userList" stripe>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link>编辑</el-button>
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-alert
      title="功能说明"
      type="info"
      :closable="false"
      style="margin-top: 20px;"
    >
      <template #default>
        <ul>
          <li>用户管理：仅合伙人可访问</li>
          <li>三种角色：PARTNER-合伙人、LAWYER-律师、ASSISTANT-助理</li>
          <li>权限说明：
            <ul style="margin-top: 8px;">
              <li>合伙人：所有权限，包括用户管理、删除数据</li>
              <li>律师：可编辑/创建客户、案件、合同、费用等</li>
              <li>助理：仅可查看数据，不可编辑</li>
            </ul>
          </li>
        </ul>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const userList = ref([
  {
    username: 'admin',
    realName: '系统管理员',
    phone: '13800138000',
    email: 'admin@lawfirm.com',
    role: 'PARTNER',
    status: 1,
    createTime: '2024-01-01 10:00:00'
  },
  {
    username: 'partner1',
    realName: '张合伙人',
    phone: '13800138001',
    email: 'partner1@lawfirm.com',
    role: 'PARTNER',
    status: 1,
    createTime: '2024-01-02 10:00:00'
  },
  {
    username: 'lawyer1',
    realName: '李律师',
    phone: '13800138002',
    email: 'lawyer1@lawfirm.com',
    role: 'LAWYER',
    status: 1,
    createTime: '2024-01-03 10:00:00'
  },
  {
    username: 'assistant1',
    realName: '王助理',
    phone: '13800138003',
    email: 'assistant1@lawfirm.com',
    role: 'ASSISTANT',
    status: 1,
    createTime: '2024-01-04 10:00:00'
  }
])

const getRoleTagType = (role) => {
  const typeMap = {
    'PARTNER': 'danger',
    'LAWYER': 'primary',
    'ASSISTANT': 'success'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    'PARTNER': '合伙人',
    'LAWYER': '律师',
    'ASSISTANT': '助理'
  }
  return textMap[role] || role
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style lang="scss" scoped>
.user-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
