import axios from 'axios'
import cache from 'lscache'

axios.interceptors.request.use(config => {
  cache.setBucket('auth')
  config.headers['Authorization'] = cache.get('hc-apm-token')
  return config
}, err => Promise.reject(err))