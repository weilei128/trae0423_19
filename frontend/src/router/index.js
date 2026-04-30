import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'clients',
        name: 'Clients',
        component: () => import('@/views/client/index.vue'),
        meta: { title: '客户档案', icon: 'User' }
      },
      {
        path: 'cases',
        name: 'Cases',
        component: () => import('@/views/case/index.vue'),
        meta: { title: '案件管理', icon: 'Document' }
      },
      {
        path: 'contracts',
        name: 'Contracts',
        component: () => import('@/views/contract/index.vue'),
        meta: { title: '合同管理', icon: 'Stamp' }
      },
      {
        path: 'progress',
        name: 'CaseProgress',
        component: () => import('@/views/progress/index.vue'),
        meta: { title: '案件跟进', icon: 'Timer' }
      },
      {
        path: 'documents',
        name: 'Documents',
        component: () => import('@/views/document/index.vue'),
        meta: { title: '文书管理', icon: 'Files' }
      },
      {
        path: 'fees',
        name: 'Fees',
        component: () => import('@/views/fee/index.vue'),
        meta: { title: '费用管理', icon: 'Money' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', roles: ['PARTNER'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path === '/login') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    if (token) {
      if (!userStore.userInfo.username) {
        try {
          await userStore.getUserInfo()
        } catch (error) {
          next('/login')
          return
        }
      }
      
      const roles = to.meta.roles
      if (roles && roles.length > 0) {
        if (!roles.includes(userStore.userInfo.role)) {
          next('/dashboard')
          return
        }
      }
      
      next()
    } else {
      next('/login')
    }
  }
})

export default router
