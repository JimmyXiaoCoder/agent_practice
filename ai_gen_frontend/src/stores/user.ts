import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const userLoginStore = defineStore('userLoginStore', () => {
  const userName = ref('')
//   const doubleCount = computed(() => count.value * 2)
  function handleLogin() {
    console.log('登录成功')
  }

  return { userName, handleLogin }
})
