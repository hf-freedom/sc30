<template>
  <div class="visitors">
    <h2>访客管理</h2>
    
    <el-card>
      <el-table :data="visitors" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="phoneNumber" label="手机号" width="120"></el-table-column>
        <el-table-column prop="idCardNumber" label="证件号" width="180"></el-table-column>
        <el-table-column prop="vehicleInfo" label="车辆信息" width="120">
          <template #default="scope">
            <span v-if="scope.row.vehicleInfo">{{ scope.row.vehicleInfo }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isBlacklisted" label="黑名单状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.isBlacklisted" type="danger">黑名单</el-tag>
            <el-tag v-else type="success">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
            <el-button 
              v-if="!scope.row.isBlacklisted" 
              type="danger" 
              size="small" 
              @click="addToBlacklist(scope.row)"
            >加入黑名单</el-button>
            <el-button 
              v-else 
              type="success" 
              size="small" 
              @click="removeFromBlacklist(scope.row)"
            >移出黑名单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Visitors',
  data() {
    return {
      loading: false,
      visitors: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await axios.get('/api/visitors')
        this.visitors = res.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async addToBlacklist(visitor) {
      try {
        const res = await axios.post(`/api/visitors/${visitor.id}/blacklist`, '违规行为')
        if (res.data.success) {
          this.$message.success('已加入黑名单')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    async removeFromBlacklist(visitor) {
      try {
        const res = await axios.delete(`/api/visitors/${visitor.id}/blacklist`)
        if (res.data.success) {
          this.$message.success('已移出黑名单')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    }
  }
}
</script>

<style scoped>
.visitors h2 {
  margin-bottom: 20px;
  color: #303133;
}
</style>
