<template>
  <div class="employees">
    <h2>员工管理</h2>
    
    <el-card>
      <el-table :data="employees" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="department" label="部门" width="120"></el-table-column>
        <el-table-column prop="canApproveVisitor" label="审批权限" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.canApproveVisitor" type="success">可审批</el-tag>
            <el-tag v-else type="info">不可审批</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Employees',
  data() {
    return {
      loading: false,
      employees: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await axios.get('/api/employees')
        this.employees = res.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    getStatusType(status) {
      const map = {
        'ACTIVE': 'success',
        'ON_LEAVE': 'warning',
        'RESIGNED': 'danger'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'ACTIVE': '在职',
        'ON_LEAVE': '请假',
        'RESIGNED': '离职'
      }
      return map[status] || status
    }
  }
}
</script>

<style scoped>
.employees h2 {
  margin-bottom: 20px;
  color: #303133;
}
</style>
