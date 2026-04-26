import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('./views/Dashboard.vue')
  },
  {
    path: '/appointments',
    name: 'Appointments',
    component: () => import('./views/Appointments.vue')
  },
  {
    path: '/visitors',
    name: 'Visitors',
    component: () => import('./views/Visitors.vue')
  },
  {
    path: '/employees',
    name: 'Employees',
    component: () => import('./views/Employees.vue')
  },
  {
    path: '/meeting-rooms',
    name: 'MeetingRooms',
    component: () => import('./views/MeetingRooms.vue')
  },
  {
    path: '/parking',
    name: 'Parking',
    component: () => import('./views/Parking.vue')
  },
  {
    path: '/security',
    name: 'Security',
    component: () => import('./views/Security.vue')
  },
  {
    path: '/audit-logs',
    name: 'AuditLogs',
    component: () => import('./views/AuditLogs.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
