<template>
  <div class="login">
    <div class="login-logo">
      <span> Metax | 多渠道消息推送系统</span>
    </div>
    <h2 class="main-title"><span>Metax</span></h2>
    <h3 class="sub-title">统一、可靠、简单的多渠道消息推送系统</h3>
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">登录</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon"/>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon"/>
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon"/>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <div class="button-container">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            style="width: 40%"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </div>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" style="color: #ffffff" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--    &lt;!&ndash;  底部  &ndash;&gt;-->
    <!--    <div class="el-login-footer">-->
    <!--      <span>Copyright © 2018-2023 ruoyi.vip All Rights Reserved.</span>-->
    <!--    </div>-->
  </div>
</template>

<script>
import {getCodeImg} from "@/api/login";
import Cookies from "js-cookie";
import {encrypt, decrypt} from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          {required: true, trigger: "blur", message: "请输入您的账号"}
        ],
        password: [
          {required: true, trigger: "blur", message: "请输入您的密码"}
        ],
        code: [{required: true, trigger: "change", message: "请输入验证码"}]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: true,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          let storage = window.localStorage;
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, {expires: 30});
            Cookies.set("password", encrypt(this.loginForm.password), {expires: 30});
            Cookies.set('rememberMe', this.loginForm.rememberMe, {expires: 30});
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          storage.setItem("user", this.loginForm.username);
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({path: this.redirect || "/"}).catch(() => {
            });
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">

.button-container {
  text-align: center;
}

.login-input,
.login-button {
  margin: 6px 0;
  height: 36px;
  width: 100%;
  border: none;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  padding: 0 14px;
  color: #3d5245;
}

.login-logo {
  width: 500px;
  height: 24px;
  margin-right: 10px;
  padding-right: 10px;
  background-image: url("../assets/logo/logo.png");
  background-repeat: no-repeat;
  background-size: auto 24px;
  display: inline-block;
  vertical-align: middle;
  zoom: 1;
  position: absolute;
  left: 200px;
  top: 50px;

  span {
    line-height: 24px;
    font-size: 20px;
    height: 24px;
    padding-left: 30px;
    color: #ffffff;
    vertical-align: middle;
    font-weight: 600;
  }
}


.main-title {
  margin: 100px 0 0 0;
  height: 34px;
  position: relative;
  text-align: center;
  color: #ffffff;
  background-image: url("../assets/logo/logo.png");
  background-repeat: no-repeat;
  background-size: auto 34px;
  display: inline-block;
  vertical-align: middle;
  zoom: 1;

  span {
    line-height: 34px;
    font-size: 34px;
    height: 34px;
    padding-left: 40px;
    color: #ffffff;
    vertical-align: middle;
    font-weight: 600;
  }
}

.sub-title {
  margin: 20px 0 60px 0;
  text-align: center;
  color: #ffffff;
}

.login {
  display: flex;
  //justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

//.login-form {
//  border-radius: 6px;
//  background: #ffffff;
//  width: 400px;
//  padding: 25px 25px 5px 25px;
//  .el-input {
//    height: 38px;
//    input {
//      height: 38px;
//    }
//  }
//  .input-icon {
//    height: 39px;
//    width: 14px;
//    margin-left: 2px;
//  }
//}

.login-form {
  width: 400px;
  height: 400px;
  display: flex;
  flex-direction: column;
  padding: 40px;
  top: -50px;
  //text-align: center;
  position: relative;
  z-index: 100;
  background: inherit;
  border-radius: 6px;
  overflow: hidden; /* 隐藏多余的模糊效果*/
  .el-input {
    height: 38px;

    input {
      height: 38px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 38px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 38px;
}
</style>
