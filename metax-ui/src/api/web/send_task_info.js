import request from '@/utils/request'

// 查询消息查询列表
export function listSend_task_info(query) {
  return request({
    url: '/web/data/sendTaskInfo',
    method: 'get',
    params: query
  })
}

// 查询消息查询列表
export function list_user_Send_task_info(query) {
  return request({
    url: '/web/data/sendTaskInfo/user',
    method: 'get',
    params: query
  })
}


// 删除消息查询
export function delSend_task_info(date, user) {
  return request({
    url: '/web/data/clear/' + date + '/' + user,
    method: 'delete'
  })
}
