<template>
  <div class="meeting-rooms">
    <h2>会议室管理</h2>
    
    <el-card>
      <el-table :data="meetingRooms" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="名称" width="150"></el-table-column>
        <el-table-column prop="location" label="位置" width="150"></el-table-column>
        <el-table-column prop="capacity" label="容量" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'AVAILABLE' ? 'success' : 'danger'">
              {{ scope.row.status === 'AVAILABLE' ? '可用' : '占用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'MeetingRooms',
  data() {
    return {
      loading: false,
      meetingRooms: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await axios.get('/api/meeting-rooms')
        this.meetingRooms = res.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.meeting-rooms h2 {
  margin-bottom: 20px;
  color: #303133;
}
</style>
