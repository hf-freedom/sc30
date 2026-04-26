<template>
  <div class="app-container">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>园区访客系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/dashboard">
            <i class="el-icon-s-data"></i>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/appointments">
            <i class="el-icon-s-order"></i>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/visitors">
            <i class="el-icon-user"></i>
            <span>访客管理</span>
          </el-menu-item>
          <el-menu-item index="/employees">
            <i class="el-icon-s-custom"></i>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="/meeting-rooms">
            <i class="el-icon-s-home"></i>
            <span>会议室管理</span>
          </el-menu-item>
          <el-menu-item index="/parking">
            <i class="el-icon-s-promotion"></i>
            <span>停车位管理</span>
          </el-menu-item>
          <el-menu-item index="/security">
            <i class="el-icon-warning"></i>
            <span>安保管理</span>
          </el-menu-item>
          <el-menu-item index="/audit-logs">
            <i class="el-icon-document"></i>
            <span>审计日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    activeMenu() {
      return this.$route.path
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
}

.app-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
}

.main-content {
  margin-left: 200px;
  background-color: #f0f2f5;
  min-height: 100vh;
  padding: 20px;
}

.el-menu {
  border-right: none;
}
</style>
