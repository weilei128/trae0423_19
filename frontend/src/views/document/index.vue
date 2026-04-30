<template>
  <div class="document-page">
    <el-card>
      <template #header>
        <span>文书管理</span>
      </template>
      <el-empty description="文书管理功能正在开发中">
        <el-button type="primary">返回首页</el-button>
      </el-empty>
    </el-card>

    <el-alert
      title="功能说明"
      type="info"
      :closable="false"
      style="margin-top: 20px;"
    >
      <template #default>
        <ul>
          <li>法律文书：模板填充、版本管理</li>
          <li>依赖案件跟进：基于案件跟进记录生成文书</li>
          <li>权限控制：律师可创建/编辑文书，合伙人可查看所有</li>
        </ul>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.document-page {
}
</style>
