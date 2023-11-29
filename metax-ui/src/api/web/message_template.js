import request from '@/utils/request'


// 暂停消息任务
export function stopMessage(id) {
  return request({
    url: '/web/message_template/stop/' + id,
    method: 'get',
    params: id
  })
}

// 启动消息任务
export function startMessage(id) {
  return request({
    url: '/web/message_template/start/' + id,
    method: 'get',
    params: id
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/web/message_template/send',
    method: 'post',
    data: data
  })
}

// 查询所有占位符名称集合
export function listVariables(id) {
  return request({
    url: '/web/message_template/variables/' + id,
    method: 'get',
    params: id
  })
}

// 查询消息模板列表
export function listMessage_template(query) {
  return request({
    url: '/web/message_template/list',
    method: 'get',
    params: query
  })
}

// 查询消息模板列表
export function getMessage_template_for_eCharts() {
  return request({
    url: '/web/message_template/list/temp',
    method: 'get',
  })
}

// 查询消息模板详细
export function getMessage_template(id) {
  return request({
    url: '/web/message_template/' + id,
    method: 'get'
  })
}

// 获取当前用户某渠道账号配置
export function getChannel_accountBySenChannel(sendChannel) {
  return request({
    url: '/web/channel_account/ChannelAccount/' + sendChannel,
    method: 'get'
  })
}

// 新增消息模板
export function addMessage_template(data) {
  return request({
    url: '/web/message_template',
    method: 'post',
    data: data
  })
}

// 修改消息模板
export function updateMessage_template(data) {
  return request({
    url: '/web/message_template',
    method: 'put',
    data: data
  })
}

// 删除消息模板
export function delMessage_template(id) {
  return request({
    url: '/web/message_template/' + id,
    method: 'delete'
  })
}
