import {message} from 'ant-design-vue'
import { userLoginStore } from '@/stores/user'
import router from './router'

// 首次访问
let firstVisit = true

// 监听路由变化
router.beforeEach(async (to, from, next) => {

    const loginStore = userLoginStore()

    // 如果是首次访问，获取登录用户（公开页面跳过，避免 40100 导致跳转）
    const publicPages = ['/user/login', '/user/register']
    if (firstVisit) {
        if (!publicPages.includes(to.path)) {
            await loginStore.handleLogin()
        }
        firstVisit = false
    }

    const user = loginStore.loginUser

    const toUrl = to.fullPath
    console.log(toUrl)
    console.log(to.path)

    if (user && toUrl.startsWith('/admin') && user.userRole !== 'admin') {
        message.error('您没有访问权限，请先登录管理员账号')
        next('/user/login?redirect=' + encodeURIComponent(toUrl))
        return
    } else {
        next()
    }


})
