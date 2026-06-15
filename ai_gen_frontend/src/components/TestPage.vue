<template>
  <a-layout class="page-layout">
    <a-layout-sider id="side" :width="140">
      <a-menu
        :openKeys="openKeys"
        :selectedKeys="selectedKeys"
        mode="vertical"
        :items="items"
      />
    </a-layout-sider>

    <a-layout class="chat-column">
      <a-layout-content class="chat-content">
        <div ref="chatScrollRef" class="chat-scroll">
          <a-list :split="false" :bordered="false" :data-source="conversations">
            <template #renderItem="{ item }">
              <a-list-item :class="['chat-item', `chat-item--${item.role}`]">
                <div :class="['chat-bubble', `chat-bubble--${item.role}`]">
                  {{ item.content }}
                </div>
              </a-list-item>
            </template>
          </a-list>
        </div>
      </a-layout-content>

      <a-layout-footer class="chat-footer">输入框</a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { listMyAppByPage, addApp } from '@/api/appController'
import { ref, onMounted, nextTick } from 'vue'

type ChatRole = 'user' | 'model'

interface ChatMessage {
  content: string
  role: ChatRole
}

const items = ref<{ key?: number; label?: string; title?: string }[]>([])
const apps = ref<API.AppVO[]>([])
const fullContent = ref('')
const chatScrollRef = ref<HTMLElement | null>(null)

const openKeys = ref([])
const selectedKeys = ref([])
const formData = {
  pageNum: 1,
  pageSize: 10,
}

const initPrompt = '生成一个简单页面，20行代码以内'
const conversations = ref<ChatMessage[]>([
  {
    content: initPrompt,
    role: 'user',
  },
])

const scrollToBottom = () => {
  nextTick(() => {
    const el = chatScrollRef.value
    if (el) {
      el.scrollTop = el.scrollHeight
    }
  })
}

const fetchData = async () => {
  const resp = await listMyAppByPage(formData)

  apps.value = resp.data.data?.records ?? []
  items.value = apps.value.map((item) => ({
    key: item.id,
    label: item.initPrompt,
    title: item.initPrompt,
  }))
}

fetchData()

const chatToGenCode = async () => {
  fullContent.value = ''

  const addResp = await addApp({
    initPrompt: initPrompt,
  })
  const appId = String(addResp.data.data)
  const chatContent = new URLSearchParams({
    appId: appId,
    message: initPrompt,
  })

  conversations.value.push({
    content: '',
    role: 'model',
  })
  scrollToBottom()

  const url = `http://localhost:8123/api/app/chatToGenCode?${chatContent.toString()}`
  const source = new EventSource(url, {
    withCredentials: true,
  })
  source.onmessage = (event) => {
    const parseData = JSON.parse(event.data)
    const content = parseData.d
    if (content !== undefined && content !== null) {
      fullContent.value += content
      const last = conversations.value[conversations.value.length - 1]
      if (last) {
        last.content = fullContent.value
      }
      scrollToBottom()
    }
  }

  source.onerror = (err) => {
    console.error('SSE error', err)
    source.close()
  }

  source.addEventListener('done', () => {
    source.close()
    scrollToBottom()
  })
}

onMounted(() => {
  chatToGenCode()
  scrollToBottom()
})
</script>

<style scoped>
.page-layout {
  height: 100vh;
  overflow: hidden;
}

:deep(.page-layout > .ant-layout) {
  flex: 1;
  min-height: 0;
}

#side {
  background-color: #fff;
}

.chat-column {
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.chat-content {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  padding: 16px 24px;
  background: #fafafa;
}

.chat-scroll {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.chat-footer {
  min-height: 30vh;
  flex-shrink: 0;
  padding: 12px 24px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
}

:deep(.chat-item.ant-list-item) {
  display: flex;
  padding: 8px 0;
  border: none !important;
}

:deep(.chat-item--model) {
  justify-content: flex-start;
}

:deep(.chat-item--user) {
  justify-content: flex-end;
}

.chat-bubble {
  max-width: 75%;
  padding: 10px 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-bubble--model {
  background: rgba(0, 0, 0, 0.06);
  color: rgba(0, 0, 0, 0.88);
  border-radius: 12px 12px 12px 4px;
}

.chat-bubble--user {
  background: #1677ff;
  color: #fff;
  border-radius: 12px 12px 4px 12px;
}

:deep(.ant-list .ant-list-items) {
  padding: 0;
}
</style>
