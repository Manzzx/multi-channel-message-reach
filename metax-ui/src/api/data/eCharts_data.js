import request from '@/utils/request'

// 查询首页列表
export function get_index_eCharts_data(day) {
  return request({
    url: '/web/eCharts/index/'+day,
    method: 'get',
  })
}

// 查询模板分析列表
export function get_template_eCharts_data(day,id) {
  return request({
    url: '/web/eCharts/analyse/'+day+'/'+id,
    method: 'get',
  })
}

// 查询用户首页列表
export function get_user_index_eCharts_data(day,user) {
  return request({
    url: '/web/eCharts/user/index/'+day+'/'+user,
    method: 'get',
  })
}

// 查询首页列表
export function get_users() {
  return request({
    url: '/web/eCharts/users',
    method: 'get',
  })
}

