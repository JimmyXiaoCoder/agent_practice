<template>
  <div class="app-chat">
    <div class="app-chat__toolbar">
      <a-button type="link" @click="goHome">返回首页</a-button>
      <span v-if="appId" class="app-chat__meta">应用 ID：{{ appId }}</span>
    </div>

    <a-spin :spinning="connecting && !assistantText && !streamError">
      <div class="app-chat__body">
        <div v-if="userMessage" class="app-chat__bubble app-chat__bubble--user">
          {{ userMessage }}
        </div>
        <div class="app-chat__bubble app-chat__bubble--assistant">
          <template v-if="streamError">{{ streamError }}</template>
          <template v-else>{{ assistantText }}</template>
          <span v-if="streaming" class="app-chat__cursor" aria-hidden="true">▍</span>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import request from '@/utils/request'

const CHAT_MESSAGE_KEY = '__appChatInitMessage'

const route = useRoute()
const router = useRouter()

const appId = ref('')
const userMessage = ref('')
const assistantText = ref('')
const streaming = ref(false)
const connecting = ref(false)
const streamError = ref('')

let abortController: AbortController | null = null

const goHome = () => {
  abortController?.abort()
  router.push('/')
}

function stripSseDataLine(line: string): string {
  const trimmed = line.trim()
  if (trimmed.startsWith('data:')) {
    return trimmed.slice(5).trim()
  }
  return trimmed
}

function appendFromJsonPayload(payload: string): void {
  const raw = stripSseDataLine(payload)
  if (!raw || raw === '[DONE]') return
  try {
    const obj = JSON.parse(raw) as { d?: unknown }
    if (typeof obj.d === 'string') {
      assistantText.value += obj.d
    }
  } catch {
    // 半包或杂讯行，忽略
  }
}

async function consumeStream(reader: ReadableStreamDefaultReader<Uint8Array>): Promise<void> {
  const decoder = new TextDecoder()
  let buffer = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    const parts = buffer.split('\n')
    buffer = parts.pop() ?? ''
    for (const part of parts) {
      appendFromJsonPayload(part)
    }
  }
  if (buffer.trim()) {
    appendFromJsonPayload(buffer)
  }
}

async function startChatStream(): Promise<void> {
  const id = typeof route.query.appId === 'string' ? route.query.appId : ''
  const msg = sessionStorage.getItem(CHAT_MESSAGE_KEY) ?? ''

  appId.value = id
  userMessage.value = msg
  sessionStorage.removeItem(CHAT_MESSAGE_KEY)

  if (!id || !msg) {
    streamError.value = '缺少应用或对话内容，请从首页重新发起。'
    message.warning(streamError.value)
    return
  }

  const base = (request.defaults.baseURL as string | undefined) ?? ''
  const params = new URLSearchParams({
    appId: id,
    message: msg,
  })
  const url = `${base.replace(/\/$/, '')}/app/chatToGenCode?${params.toString()}`

  abortController = new AbortController()
  connecting.value = true
  streaming.value = true
  assistantText.value = ''
  streamError.value = ''

  try {
    const res = await fetch(url, {
      method: 'GET',
      credentials: 'include',
      headers: {
        Accept: 'text/event-stream, application/json, text/plain, */*',
      },
      signal: abortController.signal,
    })

    if (res.status === 401) {
      message.warning('未登录或登录已过期')
      streamError.value = '未登录，请先登录后再试。'
      setTimeout(() => {
        window.location.href = `/user/login?redirect=${encodeURIComponent(route.fullPath)}`
      }, 800)
      return
    }

    if (!res.ok) {
      streamError.value = `请求失败（${res.status}）`
      message.error(streamError.value)
      return
    }

    const body = res.body
    if (!body) {
      const text = await res.text()
      if (text) {
        for (const line of text.split('\n')) {
          appendFromJsonPayload(line)
        }
      }
      return
    }

    await consumeStream(body.getReader())
  } catch (e: unknown) {
    if ((e as Error)?.name === 'AbortError') return
    console.error(e)
    streamError.value = '读取生成结果时出错，请稍后重试。'
    message.error(streamError.value)
  } finally {
    connecting.value = false
    streaming.value = false
  }
}

onMounted(() => {
  startChatStream()
})

onBeforeUnmount(() => {
  abortController?.abort()
})
</script>

<style scoped>
.app-chat {
  max-width: 880px;
  margin: 0 auto;
}

.app-chat__toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.app-chat__meta {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.45);
}

.app-chat__body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.app-chat__bubble {
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.app-chat__bubble--user {
  align-self: flex-end;
  max-width: 85%;
  background: #1677ff;
  color: #fff;
}

.app-chat__bubble--assistant {
  align-self: stretch;
  background: rgba(0, 0, 0, 0.04);
  color: rgba(0, 0, 0, 0.88);
  min-height: 48px;
}

.app-chat__cursor {
  display: inline-block;
  margin-left: 2px;
  animation: app-chat-blink 1s step-end infinite;
}

@keyframes app-chat-blink {
  50% {
    opacity: 0;
  }
}
</style>
