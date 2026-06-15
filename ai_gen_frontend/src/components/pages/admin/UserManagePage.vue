<template>
  <a-table :columns="columns" :data-source="data">
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'id'">
        <a>
          {{ record.id }}
        </a>
      </template>
      <template v-else-if="column.key === 'userAvatar'">
        <img :src="record.userAvatar" alt="Avatar" style="width: 40px; height: 55px;">
      </template>
      <template v-else-if="column.key === 'action'">
        <span>
          <a-divider type="vertical" />
          <a @click="handleDelete(record.id)">Delete</a>
          <a-divider type="vertical" />
          <a class="ant-dropdown-link">
            More actions
            <down-outlined />
          </a>
        </span>
      </template>
    </template>
  </a-table>
</template>
<script lang="ts" setup>
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import { onMounted, ref } from 'vue'
import { list } from '@/api/userController'
import { remove } from '@/api/userController'
import { message } from 'ant-design-vue';
import type { RefSymbol } from '@vue/reactivity';

const data = ref<API.UserVO[]>([])  // 改为响应式

const fetchData = async () => {
  try {
    const response = await list()
    data.value = response.data.data  // 更新响应式数据
  } catch (error) {
    console.error('Error fetching user data:', error)
  }
}

const handleDelete = async (id: string) => {
  try {
    const response = await remove({ id })
    response.data.data === true ? message.success('操作成功') : message.error('用户删除失败')
    fetchData()  // 刷新数据
  } catch (error) {
    console.error('Error deleting user:', error)
  }
}

onMounted(() => {
  fetchData()
})

console.log(data)  // 输出用户数据到控制台

const columns = [
  {
    title: 'userName',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: 'userAvatar',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
  },
  {
    title: 'userRole',
    dataIndex: 'userRole',
    key: 'userRole',
  },
  {
    title: 'Action',
    key: 'action',
  },
];

</script>

