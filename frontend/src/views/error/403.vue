<!-- 403错误页面 -->
<template>
  <div class="error-page">
    <div class="error-code">403</div>
    <div class="error-desc">抱歉，您没有访问该页面的权限</div>
    <el-button type="primary" @click="goBack">返回上一页</el-button>
    <el-button @click="goHome">返回首页</el-button>
  </div>
</template>

<script>
export default {
  name: 'Error403',
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 20px;
}

.error-desc {
  font-size: 24px;
  color: #606266;
  margin-bottom: 30px;
}

.el-button {
  margin: 0 10px;
}
</style> 