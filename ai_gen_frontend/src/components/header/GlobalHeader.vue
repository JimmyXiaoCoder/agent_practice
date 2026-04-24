<template>
  <a-row :wrap="false">
    <a-col flex="200px">
      <div class="title-bar">
        <img class="logo" src="../../assets/chiikawa.png" alt="logo" />
        <div class="title">My App</div>
      </div>
    </a-col>
    <a-col flex="auto" class="nav">
      <a-menu
        v-model:selectedKeys="current"
        mode="horizontal"
        :items="items"
        @click="doMenuClick"
      />
    </a-col>
    <template v-if="loginStore.loginUser.id">
      <a-col flex="120px" class="user-info">
        <div class="user-login-status">
          <a-dropdown>
            <a class="ant-dropdown-link" @click.prevent>
              <span>{{ loginStore.loginUser.userName }}</span>
              <DownOutlined />
            </a>
            <template #overlay>
              <a-menu @click="onClick">
                <a-menu-item>
                  <a>个人中心</a>
                </a-menu-item>
                <a-menu-item key="logout">
                  <a>退出登录</a>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-col>
    </template>
    <template v-else>
      <a-col flex="70px" class="user-info">
        <div class="user-login-status">
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </a-col>
      <a-col flex="70px" class="user-info">
        <div class="user-login-status">
          <a-button href="/user/register">注册</a-button>
        </div>
      </a-col>
    </template>
  </a-row>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { MailOutlined, AppstoreOutlined, SettingOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { userLoginStore } from '@/stores/user'
import { logout } from '@/api/userController'
const router = useRouter()
const loginStore = userLoginStore()

const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}

const onClick = ({ key }: { key: string }) => {
  if (key === 'logout') {
    handleLogout()
  }
}

const handleLogout = async () => {
  // 这里可以调用后端的登出接口
  const resp = await logout()
  // await apiLogout()
  loginStore.loginUser = { userName: '未登录' }
  router.push('/')
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
