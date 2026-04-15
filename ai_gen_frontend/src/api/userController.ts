// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /api/user/getCurUser */
export async function getCurUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>('/api/api/user/getCurUser', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /api/user/getInfo/${param0} */
export async function getInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfoParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.User>(`/api/user/getInfo/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /api/user/list */
export async function list(options?: { [key: string]: any }) {
  return request<API.User[]>('/api/user/list', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /api/user/login */
export async function login(body: API.UserLoginRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>('/api/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /api/user/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>('/api/user/logout', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /api/user/page */
export async function page(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pageParams,
  options?: { [key: string]: any }
) {
  return request<API.PageUser>('/api/user/page', {
    method: 'GET',
    params: {
      ...params,
      page: undefined,
      ...params['page'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /api/user/register */
export async function register(body: API.UserRegisterRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/api/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /api/user/remove/${param0} */
export async function remove(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<boolean>(`/api/user/remove/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /api/user/update */
export async function update(body: API.User, options?: { [key: string]: any }) {
  return request<boolean>('/api/user/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
