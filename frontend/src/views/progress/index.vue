<template>
  <div class="progress-page">
    <el-card>
      <template #header>
        <span>案件跟进</span>
      </template>
      <el-empty description="案件跟进功能正在开发中">
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
          <li>案件进度节点：开庭记录、证据材料、进度节点</li>
          <li>支持提醒设置：重要节点自动提醒</li>
          <li>权限控制：律师/助理可添加跟进记录，合伙人可查看所有</li>
        </ul>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.progress-page {
}
</style>
