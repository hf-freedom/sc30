<template>
  <div class="dashboard">
    <h2>系统仪表盘</h2>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalAppointments }}</div>
            <div class="stat-label">总预约数</div>
          </div>
          <i class="el-icon-s-order stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.pendingApprovals }}</div>
            <div class="stat-label">待审批</div>
          </div>
          <i class="el-icon-time stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.inProgress }}</div>
            <div class="stat-label">进行中</div>
          </div>
          <i class="el-icon-s-flag stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-number">{{ stats.pendingAlerts }}</div>
            <div class="stat-label">待处理警报</div>
          </div>
          <i class="el-icon-warning stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="info-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>资源状态</span>
          </template>
          <div class="resource-status">
            <div class="resource-item">
              <span class="resource-name">停车位</span>
              <el-progress 
                :percentage="Math.round((parkingStatus.total - parkingStatus.available) / parkingStatus.total * 100)" 
                :status="parkingStatus.available === 0 ? 'exception' : ''"
              >
              </el-progress>
              <span class="resource-info">{{ parkingStatus.available }} / {{ parkingStatus.total }} 可用</span>
            </div>
            <div class="resource-item">
              <span class="resource-name">会议室</span>
              <el-progress 
                :percentage="Math.round((meetingRoomStatus.total - meetingRoomStatus.available) / meetingRoomStatus.total * 100)"
                :status="meetingRoomStatus.available === 0 ? 'exception' : ''"
              >
              </el-progress>
              <span class="resource-info">{{ meetingRoomStatus.available }} / {{ meetingRoomStatus.total }} 可用</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近预约</span>
          </template>
          <el-table :data="recentAppointments" style="width: 100%" size="small">
            <el-table-column prop="visitorName" label="访客" width="100"></el-table-column>
            <el-table-column prop="employeeName" label="被访人" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        totalAppointments: 0,
        pendingApprovals: 0,
        inProgress: 0,
        pendingAlerts: 0
      },
      parkingStatus: {
        total: 0,
        available: 0
      },
      meetingRoomStatus: {
        total: 0,
        available: 0
      },
      recentAppointments: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [appointmentsRes, parkingRes, meetingRes, securityRes] = await Promise.all([
          axios.get('/api/appointments'),
          axios.get('/api/parking/status'),
          axios.get('/api/meeting-rooms/status'),
          axios.get('/api/security/dashboard')
        ])

        const appointments = appointmentsRes.data.data
        this.stats.totalAppointments = appointments.length
        this.stats.pendingApprovals = appointments.filter(a => a.status === 'PENDING_APPROVAL').length
        this.stats.inProgress = appointments.filter(a => a.status === 'IN_PROGRESS').length
        this.stats.pendingAlerts = securityRes.data.data.pendingAlerts

        this.parkingStatus = parkingRes.data.data
        this.meetingRoomStatus = meetingRes.data.data

        this.recentAppointments = appointments.slice(0, 5)
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    getStatusType(status) {
      const map = {
        'PENDING_APPROVAL': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'CANCELLED': 'info',
        'IN_PROGRESS': 'primary',
        'COMPLETED': 'success',
        'EXPIRED': 'danger'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'PENDING_APPROVAL': '待审批',
        'APPROVED': '已批准',
        'REJECTED': '已拒绝',
        'CANCELLED': '已取消',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'EXPIRED': '已过期'
      }
      return map[status] || status
    }
  }
}
</script>

<style scoped>
.dashboard h2 {
  margin-bottom: 20px;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
}

.stat-card .stat-content {
  flex: 1;
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

.stat-icon {
  font-size: 48px;
  color: #dcdfe6;
}

.stat-card.warning .stat-number {
  color: #E6A23C;
}

.stat-card.warning .stat-icon {
  color: #FDF6EC;
}

.info-row {
  margin-bottom: 20px;
}

.resource-status {
  padding: 10px 0;
}

.resource-item {
  margin-bottom: 20px;
}

.resource-item:last-child {
  margin-bottom: 0;
}

.resource-name {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #303133;
}

.resource-info {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
