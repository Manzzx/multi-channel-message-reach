import request from '@/utils/request'

// 查询定时模板链路追踪列表
export function listCron_task_cords(query) {
  return request({
    url: '/web/data/cronTaskCords/list',
    method: 'get',
    params: query
  })
}



