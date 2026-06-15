<template>
  <div class="home-page">
    <h1 class="home-page__title">零代码生成应用平台</h1>
    <div class="home-page__panel">
      <a-textarea
        v-model:value="prompt"
        class="home-page__input"
        :rows="6"
        placeholder="请描述你想生成的应用，例如：一个待办事项管理页面……"
        :maxlength="8000"
        show-count
      />
      <a-button
        class="home-page__send"
        type="primary"
        block
        size="large"
        :loading="submitting"
        @click="handleSend"
      >
        发送
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { addApp } from '@/api/appController'
import router from '@/router'

const CHAT_MESSAGE_KEY = '__appChatInitMessage'

const prompt = ref('')
const submitting = ref(false)

const defaultAppName = () => {
  const t = prompt.value.trim()
  if (!t) return '未命名应用'
  return t.length > 40 ? `${t.slice(0, 40)}…` : t
}

const handleSend = async () => {
  const text = prompt.value.trim()
  if (!text) {
    message.warning('请输入内容后再发送')
    return
  }
  submitting.value = true
  try {
    const response = await addApp({
      appName: defaultAppName(),
      initPrompt: text,
    })
    if (response.data.code !== 200 || response.data.data == null) {
      message.error(response.data.message || '创建应用失败')
      return
    }
    const appId = String(response.data.data)
    sessionStorage.setItem(CHAT_MESSAGE_KEY, text)
    await router.push({
      name: 'app-chat',
      query: { appId },
    })
  } catch (e) {
    console.error(e)
    message.error('网络异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 16px 32px;
  box-sizing: border-box;
}

.home-page__title {
  margin: 0 0 32px;
  font-size: 1.75rem;
  font-weight: 600;
  line-height: 1.25;
  text-align: center;
  color: rgba(0, 0, 0, 0.88);
}

.home-page__panel {
  width: 100%;
  max-width: 640px;
}

.home-page__input {
  margin-bottom: 16px;
}

.home-page__send {
  height: 44px;
}
</style>
