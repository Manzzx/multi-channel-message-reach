<template>
  <div v-if="contact" class="dialog">
    <div class="top">
      <div class="name">
        {{ to }}
      </div>
    </div>
    <div class="middle" @mouseover="over" @mouseout="out">
      <div v-if="msgList.length>0">
        <div v-for="msg in msgList">
          <div style="text-align: center;color: #999999;font-size: 13px;">
            {{ parseTime(msg.time, '{y}-{m}-{d} {h}:{i}:{s}') }}
          </div>
          <div class="msg" :style="msg.from === contact.to ? 'flex-direction: row;' : 'flex-direction: row-reverse;'">
            <div class="avatar">
              <img alt="" src="@/assets/images/profile.jpg"/>
            </div>
            <div v-if="msg.from === contact.to" style="flex: 13;">
              <div class="bubble-msg-left" style="margin-right: 75px;">
                {{ msg.message }}
              </div>
            </div>
            <div v-else style="flex: 13;">
              <div class="bubble-msg-right" style="margin-left: 75px;">
                {{ msg.message }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="line"></div>
    <div class="bottom">
      <label>
        <textarea
          class="messageText"
          maxlength="256"
          v-model="msg"
          :placeholder="hint"
          @keydown.enter="sendMsg($event)"
        ></textarea>
      </label>
      <button class="send" :class="{emptyText: isEmptyText}" title="按下 ENTER 发送" @click="sendMsg()">发送</button>
    </div>
  </div>
  <div v-else class="info">
    <div class="msg">
      找个好友聊天吧~~~
    </div>
  </div>
</template>

<script>
import {pullMsg} from "@/api/web/talk";

export default {
  name: "Dialog",
  props: {
    contact: {
      type: Object
    },
    msgList: {
      type: Array
    },
    to: null,
  },
  mounted() {
    this.socket = new WebSocket(`ws://localhost:9203/websocket/` + localStorage.getItem("userId"))
    this.socket.onmessage = event => {
      this.msgList.push(JSON.parse(event.data))
    }
    this.interval = setInterval(() => {
      if (this.contact && this.contact.to != null) {
        pullMsg(this.contact).then(res => {
          this.msgList = res
        })
      }
    }, 15000)
  },
  beforeDestroy() {
    !this.interval && clearInterval(this.interval)
  },
  data() {
    return {
      msg: '',
      hint: '',
      socket: null,
      bubbleMsg: '',
      interval: null,
      isEmptyText: true,
    }
  },
  watch: {
    msgList() {
      const mid = document.querySelector('.middle')
      this.$nextTick(() => {
        mid && (mid.scrollTop = mid.scrollHeight)
        document.querySelector('.messageText').focus()
      })
    },
    msg() {
      this.isEmptyText = !this.msg
    }
  },
  methods: {
    over() {
      this.setColor('#c9c7c7')
    },
    out() {
      this.setColor('#0000')
    },
    setColor(color) {
      document.documentElement.style.setProperty('--scroll-color', `${color}`)
    },
    sendMsg(e) {
      if (e) {
        e.preventDefault()
      }
      if (!this.msg) {
        this.hint = '信息不可为空！'
        return
      }
      let entity = {
        from: this.contact.from,
        to: this.contact.to,
        message: this.msg,
        time: new Date()
      }
      this.socket.send(JSON.stringify(entity))
      this.msgList.push(entity)
      this.msg = ''
      this.hint = ''
    }
  }
}
</script>

<style scoped>
:root {
  --scroll-color: #0000;
}

.dialog {
  position: relative;
  width: 719px;
  height: 100%;
  float: right;
  /*margin-left: 0px;*/

}

.name {
  position: relative;
  top: 22px;
  left: 25px;
}

.info {
  width: 719px;
  height: 100%;
  display: flex;
  align-items: center;
}

.info .msg {
  flex: 1;
  text-align: center;
}

.top {
  width: 100%;
  height: 60px;
  border-bottom: #d0d0d0 1px solid;
}

.top::after {
  content: " ";
  float: right;
  position: relative;
  top: 40px;
  border: 4px solid #0000;
  border-top-color: #8e9292;
}

.middle {
  height: 432px;
  overflow: auto;
  padding: 10px;
  margin: 6px 0 11px 0;
}

.middle::-webkit-scrollbar {
  width: 8px;
  height: 1px;
}

.middle::-webkit-scrollbar-thumb {
  border-radius: 8px;
  background-color: var(--scroll-color);
}

.middle::-webkit-scrollbar-track {
  background: #efeded;
  border-radius: 4px;
}

.middle .msg {
  display: flex;
}

.avatar {
  margin: 8px;
  flex: 1;
}

.avatar img {
  width: 36px;
  height: 36px;
  border-radius: 4px;
}

.bubble-msg-left, .bubble-msg-right {
  padding: 10px;
  font-size: 14px;
  margin-top: 10px;
  line-height: 24px;
  border-radius: 5px;
  width: fit-content;
  line-break: anywhere;
}

.bubble-msg-left {
  float: left;
  color: black;
  margin-left: -12px;
  text-indent: -0.5em;
  background-color: white;
}

.bubble-msg-right {
  float: right;
  color: white;
  background-color: #1e6ee1;
}

.bubble-msg-right::before {
  content: " ";
  float: right;
  position: relative;
  left: 18px;
  border: 4px solid #0000;
  border-left-color: #1e6ee1;
}

.bubble-msg-left::before {
  content: " ";
  float: left;
  position: relative;
  left: -18px;
  border: 4px solid #0000;
  border-right-color: white;
}

.line {
  width: 100%;
  height: 0;
  position: relative;
  top: -6px;
  border-top: #d0d0d0 1px solid;
}

.dialog .bottom {
  padding-left: 10px;
  padding-right: 25px;
}

.messageText {
  position: relative;
  margin-right: 2px;
  font: 14px/1.5 Helvetica, Arial, Tahoma, 微软雅黑;
  width: 100%;
  height: 88px;
  /*top: -30px;*/
  outline: none;
  background: #F5F5F5;
  border: 0 none;
  overflow-y: auto;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  resize: none;
  vertical-align: middle;
  display: inline-block;
}

.dialog .bottom::after {
  content: " ";
  float: right;
  position: relative;
  top: -121px;
  left: 75px;
  border: 4px solid #0000;
  border-bottom-color: #8e9292;
}

.send {
  float: right;
  position: relative;
  top: -30px;
  left: 10px;
  background-color: #11ec1b;
  border: #87ceeb;
  color: #fff;
  font-size: 12px;
  width: 50px;
  height: 26px;
  border-radius: 3px;
}

.send:focus {
  outline: none;
}

.emptyText {
  background-color: #d0d0d0;
}
</style>
