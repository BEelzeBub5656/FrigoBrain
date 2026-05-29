import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import Inventory from '@/views/Inventory.vue'
import NutritionDashboard from '@/views/NutritionDashboard.vue'
import RecipeLibrary from '@/views/RecipeLibrary.vue'
import WasteReport from '@/views/WasteReport.vue'
import DeviceManage from '@/views/DeviceManage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: 'Login' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: 'Dashboard', icon: 'Odometer' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: Inventory,
        meta: { title: 'Inventory', icon: 'Goods' }
      },
      {
        path: 'nutrition',
        name: 'Nutrition',
        component: NutritionDashboard,
        meta: { title: 'Nutrition', icon: 'DataLine' }
      },
      {
        path: 'recipes',
        name: 'Recipes',
        component: RecipeLibrary,
        meta: { title: 'Recipes', icon: 'Notebook' }
      },
      {
        path: 'waste',
        name: 'Waste',
        component: WasteReport,
        meta: { title: 'Waste Report', icon: 'TrendCharts' }
      },
      {
        path: 'devices',
        name: 'Devices',
        component: DeviceManage,
        meta: { title: 'Devices', icon: 'Monitor' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `FrigoBrain - ${to.meta.title}` : 'FrigoBrain'
  if (to.path !== '/login' && !localStorage.getItem('fb_token')) {
    next('/login')
  } else if (to.path === '/login' && localStorage.getItem('fb_token')) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
