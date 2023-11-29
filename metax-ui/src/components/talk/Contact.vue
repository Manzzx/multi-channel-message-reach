<template>
  <div class="contact">
    <div class="top">
      <div class="left">
        <img class="avatar" src="@/assets/images/profile.jpg" alt=""/>
      </div>
      <div class="right">
        {{ user }}
      </div>
    </div>
    <div v-if="friendList.length" class="bottom">
      <div v-for="(friend, i) in friendList" class="friend" :class="{activeColor: isActive(i)}" @click="setContact(i)">
        <div class="left">
          <img class="avatar" src="@/assets/images/profile.jpg" alt=""/>
        </div>
        <div class="right">
          {{ friend.nickName }}
        </div>
      </div>
    </div>
    <div v-else class="info">
      <div class="msg">
        还没有好友~~~
      </div>
    </div>
  </div>
</template>

<script>
import {getFriends} from "@/api/web/talk";

export default {
  name: 'Contact',
  data() {
    return {
      // api: api,
      active: -1,
      friendList: [],
      // params: {
      //   id: this.user.id
      // }
    }
  },
  mounted() {
    getFriends().then(res => {
      this.friendList = res
      for (let i = 0; i < this.friendList.length; i++) {
        if (this.friendList[i].userName === localStorage.getItem('user')) {
          // 根据用户名筛选出当前登录用户
          localStorage.setItem("userId", this.friendList[i].userId);
        }
      }
    })
  },
  computed: {
    user() {
      let fromName = localStorage.getItem('user');
      for (let i = 0; i < this.friendList.length; i++) {
        if (this.friendList[i].userName === fromName){
          //筛选出发送方昵称
          localStorage.setItem("userNickName",this.friendList[i].nickName)
        }
      }
      return localStorage.getItem('userNickName')
    }
  },
  methods: {
    // 将聊天对象传到Dialog组件中
    setContact(index) {
      this.active = index
      delete this.friendList[index].password
      localStorage.setItem("toUsername",this.friendList[index].nickName)

      let contact = {
        from: localStorage.getItem('userId'),
        to: this.friendList[index].userId
      }
      this.$emit('set-contact', contact)
    },
    isActive(index) {
      return this.active === index
    }
  }
}
</script>

<style scoped>
.contact {
  width: 360px;
  height: 100%;
  float: left;
  border-right: #d0d0d0 1px solid;
}

.top {
  width: 100%;
  height: 80px;
  display: flex;
  align-items: center;
  border-bottom: #e0dfdf 1px solid;
}

.activeColor {
  background-color: #c9cbcb;
}

.top .left {
  flex: 1;
  text-align: center;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 4px;
}

.top .right {
  flex: 3;
}

.friend {
  width: 360px;
  height: 60px;
  line-height: 60px;
  display: flex;
  align-items: center;
  border-bottom: #faf7f7 1px solid;
}

.friend .left {
  flex: 1;
  margin-top: 24px;
  text-align: center;
}

.friend .right {
  flex: 3;
  color: #575454;
  font-size: 14px;
}

.friend .avatar {
  width: 36px;
  height: 36px;
}

.info {
  margin-top: 230px;
}

.info .msg {
  text-align: center;
}
</style>
