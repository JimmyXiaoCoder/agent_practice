import axios from 'axios'
import message from 'ant-design-vue/es/message';

const instance = axios.create({
  baseURL: 'http://localhost:8123/api',
  timeout: 1000,
  headers: {'X-Custom-Header': 'foobar'},
  withCredentials: true, // 允许携带cookie
});

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
    
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  const path = window.location.pathname  
  const isLoginPage = path === '/user/login'

    if (!isLoginPage && response.data.code === 40100) {
        console.log('未登录，跳转到登录页')
        message.warning(response.data.message || '未登录');
        setTimeout(() => {
          window.location.href = `/user/login`;
        },1000)
    }

    return response;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });

 export default instance