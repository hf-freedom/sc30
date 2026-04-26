<template>
  <div class="parking">
    <h2>停车位管理</h2>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ parkingStatus.total }}</div>
            <div class="stat-label">总停车位</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card success">
          <div class="stat-content">
            <div class="stat-number">{{ parkingStatus.available }}</div>
            <div class="stat-label">可用</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-number">{{ parkingStatus.occupied }}</div>
            <div class="stat-label">已占用</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-table :data="parkingSpots" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="spotNumber" label="车位号" width="120"></el-table-column>
        <el-table-column prop="location" label="位置" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentAppointmentId" label="关联预约" width="120">
          <template #default="scope">
            <span v-if="scope.row.currentAppointmentId">#{{ scope.row.currentAppointmentId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="expectedReleaseTime" label="预计释放时间" width="180"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Parking',
  data() {
    return {
      loading: false,
      parkingSpots: [],
      parkingStatus: {
        total: 0,
        available: 0,
        occupied: 0
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
        const [spotsRes, statusRes] = await Promise.all([
          axios.get('/api/parking/spots'),
          axios.get('/api/parking/status')
        ])
        
        this.parkingSpots = spotsRes.data.data
        this.parkingStatus = statusRes.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    getStatusType(status) {
      const map = {
        'AVAILABLE': 'success',
        'OCCUPIED': 'danger',
        'RESERVED': 'warning',
        'MAINTENANCE': 'info'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'AVAILABLE': '可用',
        'OCCUPIED': '已占用',
        'RESERVED': '已预约',
        'MAINTENANCE': '维护中'
      }
      return map[status] || status
    }
  }
}
</script>

<style scoped>
.parking h2 {
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

.stat-card.success .stat-number {
  color: #67C23A;
}

.stat-card.warning .stat-number {
  color: #E6A23C;
}
</style>
