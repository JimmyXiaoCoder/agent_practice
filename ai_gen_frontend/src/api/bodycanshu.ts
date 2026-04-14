// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 POST /body/test */
export async function test(options?: { [key: string]: any }) {
  return request<number>('/body/test', {
    method: 'POST',
    ...(options || {}),
  })
}
