<template>
  <a-row :wrap="false">
    <a-col flex="200px">
      <div class="title-bar">
        <img class="logo" src="../../assets/chiikawa.png" alt="logo" />
        <div class="title">My App</div>
      </div>
    </a-col>
    <a-col flex="auto" class="nav">
      <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" @click="doMenuClick"/>
    </a-col>
    <a-col flex="120px" class="user-info">
      <div class="user-login-status">
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { MailOutlined, AppstoreOutlined, SettingOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { userLoginStore } from '@/stores/user'
const router = useRouter()
const userStore = userLoginStore()

const handleLogin = () => {
  userStore.handleLogin()
}

const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}

const current = ref<string[]>(['/'])

router.afterEach((to) => {
  current.value = [to.path]
})
const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(MailOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/about',
    icon: () => h(AppstoreOutlined),
    label: '关于',
    title: '关于',
  },
  {
    key: 'alipay',
    label: h('a', { href: 'https://antdv.com', target: '_blank' }, 'Navigation Four - Link'),
    title: 'Navigation Four - Link',
  },
])
</script>
<style scoped>
#user-login-status {
  margin-right: 10px;
}

.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
