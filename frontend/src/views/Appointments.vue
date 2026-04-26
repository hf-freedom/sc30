<template>
  <div class="appointments">
    <div class="page-header">
      <h2>预约管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <i class="el-icon-plus"></i> 新建预约
      </el-button>
    </div>

    <el-card>
      <el-table :data="appointments" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="预约号" width="80"></el-table-column>
        <el-table-column prop="visitorName" label="访客" width="100"></el-table-column>
        <el-table-column prop="visitorPhone" label="访客电话" width="120"></el-table-column>
        <el-table-column prop="employeeName" label="被访人" width="100"></el-table-column>
        <el-table-column prop="accessArea" label="访问区域" width="100"></el-table-column>
        <el-table-column prop="isDriving" label="是否开车" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.isDriving" type="warning">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="scope">
            <el-button-group>
              <el-button 
                v-if="scope.row.status === 'PENDING_APPROVAL'" 
                type="success" 
                size="small" 
                @click="approveAppointment(scope.row)"
              >批准</el-button>
              <el-button 
                v-if="scope.row.status === 'PENDING_APPROVAL'" 
                type="danger" 
                size="small" 
                @click="rejectAppointment(scope.row)"
              >拒绝</el-button>
              <el-button 
                v-if="scope.row.status === 'APPROVED'" 
                type="primary" 
                size="small" 
                @click="checkIn(scope.row)"
              >签到</el-button>
              <el-button 
                v-if="scope.row.status === 'IN_PROGRESS'" 
                type="success" 
                size="small" 
                @click="checkOut(scope.row)"
              >签退</el-button>
              <el-button 
                v-if="scope.row.status === 'PENDING_APPROVAL' || scope.row.status === 'APPROVED'" 
                type="warning" 
                size="small" 
                @click="cancelAppointment(scope.row)"
              >取消</el-button>
              <el-button 
                type="info" 
                size="small" 
                @click="viewDetails(scope.row)"
              >详情</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog 
      title="新建预约" 
      v-model="showCreateDialog" 
      width="600px"
      @close="resetCreateForm"
    >
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="访客姓名" prop="visitorName">
          <el-input v-model="createForm.visitorName" placeholder="请输入访客姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="visitorPhone">
          <el-input v-model="createForm.visitorPhone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="证件号" prop="visitorIdCard">
          <el-input v-model="createForm.visitorIdCard" placeholder="请输入证件号"></el-input>
        </el-form-item>
        <el-form-item label="车辆信息" prop="vehicleInfo">
          <el-input v-model="createForm.vehicleInfo" placeholder="车牌号（可选）"></el-input>
        </el-form-item>
        <el-form-item label="被访人" prop="employeeId">
          <el-select v-model="createForm.employeeId" placeholder="请选择被访人" style="width: 100%">
            <el-option 
              v-for="emp in employees" 
              :key="emp.id" 
              :label="emp.name + ' - ' + emp.department" 
              :value="emp.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="访问时间" prop="startTime">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="访问区域" prop="accessArea">
          <el-select v-model="createForm.accessArea" placeholder="请选择访问区域" style="width: 100%">
            <el-option label="办公区A" value="办公区A"></el-option>
            <el-option label="办公区B" value="办公区B"></el-option>
            <el-option label="生产区" value="生产区"></el-option>
            <el-option label="研发中心" value="研发中心"></el-option>
            <el-option label="会议中心" value="会议中心"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否开车" prop="isDriving">
          <el-switch v-model="createForm.isDriving" active-text="是" inactive-text="否"></el-switch>
        </el-form-item>
        <el-form-item label="会议室" prop="meetingRoomId">
          <el-select v-model="createForm.meetingRoomId" placeholder="请选择会议室（可选）" clearable style="width: 100%">
            <el-option 
              v-for="room in availableMeetingRooms" 
              :key="room.id" 
              :label="room.name + ' (' + room.location + ')' " 
              :value="room.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="访问目的" prop="purpose">
          <el-input type="textarea" v-model="createForm.purpose" placeholder="请输入访问目的"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitCreateForm">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="预约详情" v-model="showDetailDialog" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约号">{{ currentAppointment.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)">{{ getStatusText(currentAppointment.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="访客">{{ currentAppointment.visitorName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentAppointment.visitorPhone }}</el-descriptions-item>
        <el-descriptions-item label="被访人">{{ currentAppointment.employeeName }}</el-descriptions-item>
        <el-descriptions-item label="访问区域">{{ currentAppointment.accessArea }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentAppointment.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentAppointment.endTime }}</el-descriptions-item>
        <el-descriptions-item label="是否开车">
          <el-tag v-if="currentAppointment.isDriving" type="warning">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="访问目的">{{ currentAppointment.purpose }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Appointments',
  data() {
    return {
      loading: false,
      appointments: [],
      employees: [],
      availableMeetingRooms: [],
      showCreateDialog: false,
      showDetailDialog: false,
      currentAppointment: {},
      timeRange: [],
      createForm: {
        visitorName: '',
        visitorPhone: '',
        visitorIdCard: '',
        vehicleInfo: '',
        employeeId: null,
        accessArea: '',
        isDriving: false,
        meetingRoomId: null,
        purpose: ''
      },
      createRules: {
        visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
        visitorPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        visitorIdCard: [{ required: true, message: '请输入证件号', trigger: 'blur' }],
        employeeId: [{ required: true, message: '请选择被访人', trigger: 'change' }],
        accessArea: [{ required: true, message: '请选择访问区域', trigger: 'change' }]
      },
      timeRangeRules: {
        timeRange: [{ required: true, message: '请选择访问时间', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    formatDateTime(date) {
      if (!date) return null
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      const seconds = String(d.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    async loadData() {
      this.loading = true
      try {
        const [appointmentsRes, employeesRes, meetingRoomsRes] = await Promise.all([
          axios.get('/api/appointments'),
          axios.get('/api/employees'),
          axios.get('/api/meeting-rooms/available')
        ])
        
        const appointments = appointmentsRes.data.data
        const employees = employeesRes.data.data
        
        this.appointments = appointments.map(a => {
          const emp = employees.find(e => e.id === a.employeeId)
          return {
            ...a,
            employeeName: emp ? emp.name : '未知'
          }
        })
        
        this.employees = employees.filter(e => e.status === 'ACTIVE')
        this.availableMeetingRooms = meetingRoomsRes.data.data
      } catch (error) {
        this.$message.error('加载数据失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    async submitCreateForm() {
      if (!this.timeRange || !this.timeRange[0] || !this.timeRange[1]) {
        this.$message.error('请选择访问时间')
        return
      }
      
      this.$refs.createFormRef.validate(async valid => {
        if (valid) {
          try {
            const formData = {
              ...this.createForm,
              startTime: this.timeRange[0],
              endTime: this.timeRange[1]
            }
            
            console.log('提交的数据:', formData)
            
            const res = await axios.post('/api/appointments', formData)
            
            if (res.data.success) {
              this.$message.success('预约创建成功')
              this.showCreateDialog = false
              this.loadData()
            } else {
              this.$message.error(res.data.message || '创建失败')
            }
          } catch (error) {
            this.$message.error(error.response?.data?.message || '创建失败')
            console.error(error)
          }
        }
      })
    },
    resetCreateForm() {
      this.$refs.createFormRef.resetFields()
      this.timeRange = []
    },
    async approveAppointment(row) {
      try {
        const res = await axios.post(`/api/appointments/${row.id}/approve`, {
          approverId: row.employeeId,
          comment: '审批通过'
        })
        
        if (res.data.success) {
          this.$message.success('审批通过')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    async rejectAppointment(row) {
      try {
        const res = await axios.post(`/api/appointments/${row.id}/reject`, {
          approverId: row.employeeId,
          reason: '审批拒绝'
        })
        
        if (res.data.success) {
          this.$message.success('已拒绝')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    async cancelAppointment(row) {
      try {
        const res = await axios.post(`/api/appointments/${row.id}/cancel`, {
          reason: '用户取消'
        })
        
        if (res.data.success) {
          this.$message.success('已取消')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    async checkIn(row) {
      try {
        const res = await axios.post(`/api/appointments/${row.id}/checkin`)
        
        if (res.data.success) {
          this.$message.success('签到成功')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    async checkOut(row) {
      try {
        const res = await axios.post(`/api/appointments/${row.id}/checkout`)
        
        if (res.data.success) {
          this.$message.success('签退成功')
          this.loadData()
        } else {
          this.$message.error(res.data.message || '操作失败')
        }
      } catch (error) {
        this.$message.error(error.response?.data?.message || '操作失败')
        console.error(error)
      }
    },
    viewDetails(row) {
      this.currentAppointment = row
      this.showDetailDialog = true
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
.appointments .page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.appointments h2 {
  margin: 0;
  color: #303133;
}
</style>
