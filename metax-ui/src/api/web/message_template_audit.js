import request from '@/utils/request'

// 查询模板审核列表
export function listMessage_template_audit(query) {
  return request({
    url: '/web/message_template/audit/list',
    method: 'get',
    params: query
  })
}

// // 查询模板审核详细
// export function getMessage_template_audit(id) {
//   return request({
//     url: '/web/message_template_audit/' + id,
//     method: 'get'
//   })
// }

// // 新增模板审核
// export function addMessage_template_audit(data) {
//   return request({
//     url: '/web/message_template_audit',
//     method: 'post',
//     data: data
//   })
// }

// 修改模板审核
export function updateMessage_template_audit(id,status) {
  return request({
    url: '/web/message_template/audit/'+id+'/'+status,
    method: 'get',
  })
}

// 删除模板审核
export function delMessage_template_audit(id) {
  return request({
    url: '/web/message_template_audit/' + id,
    method: 'delete'
  })
}
