import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getCurUser } from '@/api/userController'

export const userLoginStore = defineStore(
  'loginUser',
  () => {
    const loginUser = ref<API.LoginUserVO>({
      userName: '未登录',
    })
    //   const doubleCount = computed(() => count.value * 2)
    async function handleLogin() {
      const response = await getCurUser()
      if (response.data.code === 200 && response.data.data) {
        loginUser.value = response.data.data
      }
    }

    return { loginUser, handleLogin }
  },
  {
    persist: true, // ✅ 开启持久化，自动同步 localStorage
  },
)
