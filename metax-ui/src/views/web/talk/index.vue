<template>
  <div id="app">
    <div class="main">
<!--      @set-contact 表示监听了一个名为 "set-contact" 的自定义事件，并将其绑定到 set 方法上。当该事件触发时，set 方法将被调用。-->
      <Contact @set-contact="set"/>
      <Dialog :contact="contact" :msgList="msgList" :to="to"/>
    </div>
  </div>
</template>

<script>
import Contact from '@/components/talk/Contact'
import Dialog from '@/components/talk/Dialog'
import {pullMsg} from "@/api/web/talk";

export default {
  name: "talk",
  components: {
    Dialog,
    Contact
  },
  data() {
    return {
      contact: null,
      msgList: [],
      to:null,
    }
  },
  methods: {
    set(user) {
      this.contact = user
      this.to = localStorage.getItem("toUsername")
      pullMsg(this.contact).then(res =>{
        this.msgList = res
      })
      // request({
      //   method: 'post',
      //   url: '/pullMsg',
      //   params: {
      //     from: JSON.parse(localStorage.getItem('user')).id,
      //     to: this.contact.id
      //   }
      // }).then(res => {
      //   this.msgList = res.data.data
      // }).catch(err => {
      //   console.log(err)
      // })
    }
  }
}
</script>

<style scoped>
#app {
  width: 100%;
  height: 100%;
  background-size: cover;
  /*background-image: url("src/assets/images/login-background.jpg");*/
}

.main {
  width: 1100px;
  height: 600px;
  margin-top: 15px;
  margin-left: auto;
  margin-right: auto;
  border-radius: 5px;
  background-color: #F5F5F5;
  border: #f8f3f3 1px solid;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04)
}
</style>
