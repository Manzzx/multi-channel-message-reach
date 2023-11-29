import request from '@/utils/request'

// 查询渠道账号列表
export function listChannel_account(query) {
  return request({
    url: '/web/channel_account/list',
    method: 'get',
    params: query
  })
}

// 查询渠道账号详细
export function getChannel_account(id) {
  return request({
    url: '/web/channel_account/' + id,
    method: 'get'
  })
}

// 新增渠道账号
export function addChannel_account(data) {
  return request({
    url: '/web/channel_account',
    method: 'post',
    data: data
  })
}

// 修改渠道账号
export function updateChannel_account(data) {
  return request({
    url: '/web/channel_account',
    method: 'put',
    data: data
  })
}

// 删除渠道账号
export function delChannel_account(id) {
  return request({
    url: '/web/channel_account/' + id,
    method: 'delete'
  })
}

// 查询微信服务号账号列表
export function listWxTemplate_account(id) {
  return request({
    url: '/web/weChatServiceAccount/template/list/'+id,
    method: 'get',
  })
}
