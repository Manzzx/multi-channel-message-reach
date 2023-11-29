import request from '@/utils/request'

// 查询消息查询列表
export function list_receiver_records(query) {
  return request({
    url: '/web/data/receiverRecords',
    method: 'get',
    params: query
  })
}
