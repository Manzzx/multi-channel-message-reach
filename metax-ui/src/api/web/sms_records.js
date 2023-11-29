import request from '@/utils/request'

// 拉取短信回执
export function push_sms_records(query) {
  return request({
    url: '/web/sms/pushRecord',
    method: 'get',
    params: query
  })
}

// 拉取近期短信回执
export function push_recent_sms_records(query) {
  return request({
    url: '/web/sms/recent',
    method: 'get',
    params: query
  })
}

// 删除消息查询
export function clearRecent() {
  return request({
    url: '/web/sms/clear',
    method: 'delete'
  })
}



