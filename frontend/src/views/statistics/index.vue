<template>
  <div class="statistics-page">
    <el-card>
      <template #header>
        <span>统计分析</span>
      </template>
      <el-empty description="统计分析功能正在开发中">
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
          <li>律师办案量统计：按律师统计案件数量</li>
          <li>案件类型分布：饼图展示各类型案件占比</li>
          <li>回款率统计：按时间段统计费用回收情况</li>
          <li>权限控制：合伙人可查看所有统计，律师/助理可查看个人相关</li>
        </ul>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.statistics-page {
}
</style>
