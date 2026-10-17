import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/Home.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'lost',
        name: 'LostList',
        component: () => import('@/views/lost/LostList.vue'),
        meta: { title: '寻物启事' },
      },
      {
        path: 'found',
        name: 'FoundList',
        component: () => import('@/views/found/FoundList.vue'),
        meta: { title: '招领信息' },
      },
      {
        path: 'item/:type/:id',
        name: 'ItemDetail',
        component: () => import('@/views/item/ItemDetail.vue'),
        meta: { title: '物品详情' },
      },
      {
        path: 'publish',
        name: 'Publish',
        component: () => import('@/views/item/Publish.vue'),
        meta: { title: '发布信息', requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
      {
        path: 'my-items',
        name: 'MyItems',
        component: () => import('@/views/user/MyItems.vue'),
        meta: { title: '我发布的物品', requiresAuth: true },
      },
      {
        path: 'my-claims',
        name: 'MyClaims',
        component: () => import('@/views/user/MyClaims.vue'),
        meta: { title: '认领管理', requiresAuth: true },
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/message/Messages.vue'),
        meta: { title: '通知中心', requiresAuth: true },
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '用户登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '用户注册' },
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '数据看板' },
      },
      {
        path: 'review',
        name: 'ReviewCenter',
        component: () => import('@/views/admin/ReviewCenter.vue'),
        meta: { title: '审核中心' },
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 校园失物招领平台`
  }

  const userStore = useUserStore()

  // 1. 登录鉴权
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再访问')
    return next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
  }

  // 2. 管理员权限鉴权
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    ElMessage.error('权限不足：仅管理员允许访问管理后台')
    return next('/')
  }

  next()
})

export default router
