import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('fb_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        localStorage.removeItem('fb_token')
        window.location.href = '/login'
      } else if (status >= 500) {
        ElMessage.error('Server error. Using demo data.')
      }
    } else if (error.code === 'ECONNABORTED') {
      console.warn('Request timeout - falling back to demo data')
    } else {
      console.warn('API unavailable - falling back to demo data')
    }
    return Promise.reject(error)
  }
)

export default request
