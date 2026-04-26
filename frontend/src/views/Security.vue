<template>
  <div class="security">
    <h2>安保管理</h2>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalAlerts }}</div>
            <div class="stat-label">总提醒数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-number">{{ stats.pendingAlerts }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-number">{{ stats.handledAlerts }}</div>
            <div class="stat-label">已处理</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>待处理提醒</span>
      </template>
      <el-table :data="pendingAlerts" style="width: 100%" v-loading="loading" empty-text="暂无待处理提醒">
        <el-table-column prop="id" label="提醒ID" width="100"></el-table-column>
        <el-table-column prop="alertType" label="类型" width="100">
          <template #default="scope">
            <el-tag type="danger">{{ scope.row.alertType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertMessage" label="提醒内容" min-width="300"></el-table-column>
        <el-table-column prop="alertTime" label="提醒时间" width="180"></el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button-group>
              <el-button type="success" size="small" @click="handleAlert(scope.row, 'HANDLED')">处理</el-button>
              <el-button type="warning" size="small" @click="handleAlert(scope.row, 'DISMISSED')">忽略</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>所有提醒记录</span>
      </template>
      <el-table :data="allAlerts" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="提醒ID" width="100"></el-table-column>
        <el-table-column prop="alertType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PENDING' ? 'danger' : 'info'">{{ scope.row.alertType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertMessage" label="提醒内容" min-width="250"></el-table-column>
        <el-table-column prop="alertTime" label="提醒时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getAlertStatusType(scope.row.status)">{{ getAlertStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handledBy" label="处理人" width="100"></el-table-column>
        <el-table-column prop="handledTime" label="处理时间" width="180"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Security',
  data() {
    return {
      loading: false,
      pendingAlerts: [],
      allAlerts: [],
      stats: {
        totalAlerts: 0,
        pendingAlerts: 0,
        handledAlerts: 0
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const [allRes, pendingRes, dashboardRes] = await Promise.all([
          axios.get('/api/security/alerts'),
          axios.get('/api/security/alerts/pending'),
          axios.get('/api/security/dashboard')
        ])
        
        this.allAlerts = allRes.data.data
        this.pendingAlerts = pendingRes.data.data
        this.stats = dashboardRes.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async handleAlert(alert, action) {
      try {
        const res = await axios.post(`/api/security/alerts/${alert.id}/handle`, {
          handlerName: '安保人员',
          action: action
        })
        
        if (res.data.success) {
          this.$message.success(action === 'HANDLED' ? '已处理' : '已忽略')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    getAlertStatusType(status) {
      const map = {
        'PENDING': 'danger',
        'HANDLED': 'success',
        'DISMISSED': 'info'
      }
      return map[status] || 'info'
    },
    getAlertStatusText(status) {
      const map = {
        'PENDING': '待处理',
        'HANDLED': '已处理',
        'DISMISSED': '已忽略'
      }
      return map[status] || status
    }
  }
}
</script>

<style scoped>
.security h2 {
  margin-bottom: 20px;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-card.warning .stat-number {
  color: #E6A23C;
}

.stat-card.success .stat-number {
  color: #67C23A;
}
</style>
