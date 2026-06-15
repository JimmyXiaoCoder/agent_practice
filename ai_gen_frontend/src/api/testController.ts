import request from '@/utils/request'

export async function sseTest( options?: { [key: string]: any }) {
    
    return request<API.ServerSentEventString[]>('/test/sse',{
        method: 'GET',
        ...(options || {}),
    })
}