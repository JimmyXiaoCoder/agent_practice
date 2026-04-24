<template>
  <div>
    <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      autocomplete="off"
      @finish="handleSubmit"
      @finishFailed="onFinishFailed"
    >
      <a-form-item
        label="UserAccount"
        name="userAccount"
        :rules="[{ required: true, message: 'Please input your UserAccount!' }]"
      >
        <a-input v-model:value="formState.userAccount" />
      </a-form-item>

      <a-form-item
        label="Password"
        name="userPassword"
        :rules="[{ required: true, message: 'Please input your password!' }]"
      >
        <a-input-password v-model:value="formState.userPassword" />
      </a-form-item>

      <a-form-item
        label="checkPassword"
        name="checkPassword"
        :rules="[{ required: true, message: 'Please input your checkPassword!' }]"
      >
        <a-input-password v-model:value="formState.checkPassword" />
      </a-form-item>
      <!-- 
    <a-form-item name="remember" :wrapper-col="{ offset: 8, span: 16 }">
      <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
    </a-form-item> -->

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { register } from '@/api/userController'
import { message } from 'ant-design-vue'
import router from '@/router'

interface FormState {
  userAccount: string
  userPassword: string
  checkPassword: string
}

const formState = reactive<FormState>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})
const onFinish = (values: any) => {
  console.log('Success:', values)
}

const onFinishFailed = (errorInfo: any) => {
  console.log('Failed:', errorInfo)
}

const handleSubmit = async () => {
  const response = await register(formState)
  if (response.data.code === 200) {
    message.success('注册成功!')
    router.push('/user/login')
  } else {
    message.error('注册失败: ' + response.data.message)
  }
}
</script>

<style>
.home {
}
</style>
