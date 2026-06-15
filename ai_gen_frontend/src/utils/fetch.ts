type FetchOptions = Omit<RequestInit, 'body'> & {
  params?: Record<string, any>
  body?: any
}

/**
 * 纯 fetch 封装（无 axios / request）
 */
export async function fetchClient<T>(
  url: string,
  options: FetchOptions = {}
): Promise<T> {
  const {
    params,
    body,
    method = 'GET',
    headers = {},
    ...rest
  } = options

  // 1️⃣ 拼接 URL 参数
  const query = params ? new URLSearchParams(params).toString() : ''
  const fullUrl = query ? `${url}?${query}` : url

  // 2️⃣ 处理 body
  let finalBody: BodyInit | undefined
  const finalHeaders: HeadersInit = { ...headers }

  if (body) {
    if (body instanceof URLSearchParams) {
      finalBody = body
      finalHeaders['Content-Type'] =
        'application/x-www-form-urlencoded'
    } else {
      finalBody = JSON.stringify(body)
      finalHeaders['Content-Type'] = 'application/json'
    }
  }

  // 3️⃣ 发起 fetch
  const response = await fetch(fullUrl, {
    method,
    headers: finalHeaders,
    body: finalBody,
    credentials: 'include', // ✅ 带 cookie
    ...rest,
  })

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }

  // 4️⃣ 默认返回 JSON
  return response.json()
}