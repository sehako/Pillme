import axios from 'axios'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL.replace(/\/$/, ''),
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
})

export default apiClient
