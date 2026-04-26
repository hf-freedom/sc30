<template>
  <div class="audit-logs">
    <h2>审计日志</h2>
    
    <el-card>
      <el-table :data="logs" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="日志ID" width="100"></el-table-column>
        <el-table-column prop="actionType" label="操作类型" width="120">
          <template #default="scope">
            <el-tag :type="getActionType(scope.row.actionType)" size="small">{{ getActionText(scope.row.actionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="actionDescription" label="操作描述" min-width="300"></el-table-column>
        <el-table-column prop="visitorName" label="访客" width="100"></el-table-column>
        <el-table-column prop="visitorPhone" label="访客电话" width="120"></el-table-column>
        <el-table-column prop="accessArea" label="访问区域" width="100"></el-table-column>
        <el-table-column prop="actionTime" label="操作时间" width="180"></el-table-column>
        <el-table-column prop="operator" label="操作人" width="100"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AuditLogs',
  data() {
    return {
      loading: false,
      logs: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await axios.get('/api/audit-logs')
        this.logs = res.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    getActionType(actionType) {
      const map = {
        'NEW_VISITOR': 'info',
        'BLACKLIST_ADD': 'danger',
        'BLACKLIST_REMOVE': 'success',
        'APPOINTMENT_CREATE': 'primary',
        'APPOINTMENT_APPROVE': 'success',
        'APPOINTMENT_REJECT': 'danger',
        'APPOINTMENT_CANCEL': 'warning',
        'CHECK_IN': 'success',
        'CHECK_OUT': 'info',
        'SECURITY_ALERT': 'danger',
        'ALERT_HANDLED': 'info'
      }
      return map[actionType] || 'info'
    },
    getActionText(actionType) {
      const map = {
        'NEW_VISITOR': '新访客',
        'BLACKLIST_ADD': '加入黑名单',
        'BLACKLIST_REMOVE': '移出黑名单',
        'APPOINTMENT_CREATE': '创建预约',
        'APPOINTMENT_APPROVE': '审批通过',
        'APPOINTMENT_REJECT': '审批拒绝',
        'APPOINTMENT_CANCEL': '取消预约',
        'CHECK_IN': '入园',
        'CHECK_OUT': '离园',
        'SECURITY_ALERT': '安保提醒',
        'ALERT_HANDLED': '提醒处理'
      }
      return map[actionType] || actionType
    }
  }
}
</script>

<style scoped>
.audit-logs h2 {
  margin-bottom: 20px;
  color: #303133;
}
</style>
