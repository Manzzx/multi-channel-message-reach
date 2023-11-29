import request from '@/utils/request'

// 拉取左侧好友列表
export function getFriends() {
  return request({
    url: 'web/im/getFriends',
    method: 'get',
    // params: query
  })
}

// 拉取信息
export function pullMsg(contact) {
  return request({
    url: 'web/im/pullMsg/' + contact.from + "/" + contact.to,
    method: 'get',
  })
}
