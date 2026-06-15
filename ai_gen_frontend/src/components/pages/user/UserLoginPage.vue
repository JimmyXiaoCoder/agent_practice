<template>
  <div class="login-page">
    <h1 class="login-page__title">代码生成平台</h1>
    <h3>登录</h3>
    <a-form
      class="login-page__form"
      :model="formState"
      name="basic"
      layout="vertical"
      autocomplete="off"
      @finish="handleSubmit"
      @finishFailed="onFinishFailed"
    >
      <a-form-item
        label="账号"
        name="userAccount"
        :rules="[{ required: true, message: '请输入账号' }]"
      >
        <a-input v-model:value="formState.userAccount" />
      </a-form-item>

      <a-form-item
        label="密码"
        name="userPassword"
        :rules="[{ required: true, message: '请输入密码' }]"
      >
        <a-input-password v-model:value="formState.userPassword" />
      </a-form-item>

      <!-- 
    <a-form-item name="remember" :wrapper-col="{ offset: 8, span: 16 }">
      <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
    </a-form-item> -->

      <a-form-item class="login-page__submit">
        <a-button type="primary" block html-type="submit">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { login } from '@/api/userController'
import { message } from 'ant-design-vue'
import router from '@/router'
import { userLoginStore } from '@/stores/user'

interface FormState {
  userAccount: string
  userPassword: string
}

const formState = reactive<FormState>({
  userAccount: '',
  userPassword: '',
})
const onFinish = (values: any) => {
  console.log('Success:', values)
}

const onFinishFailed = (errorInfo: any) => {
  console.log('Failed:', errorInfo)
}

const userStore = userLoginStore()

const handleSubmit = async () => {
  const response = await login(formState)
  if (response.data.code === 200) {
    message.success('登录成功!')
    userStore.handleLogin()
    router.push('/')
  } else {
    message.error('登录失败: ' + response.data.message)
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 72px 16px 48px;
  box-sizing: border-box;
}

.login-page__title {
  margin: 0 0 40px;
  font-size: 2rem;
  font-weight: 600;
  line-height: 1.2;
  text-align: center;
  color: rgba(0, 0, 0, 0.88);
}

.login-page__form {
  width: 100%;
  max-width: 360px;
}

.login-page__submit {
  margin-bottom: 0;
}
</style>
