<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建者" prop="creator">
        <el-input
          v-model="queryParams.creator"
          placeholder="请输入创建者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable
          v-model="queryParams.createTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>


    <el-table v-loading="loading" :data="message_templateList" >
      <el-table-column label="ID" align="center" prop="id"/>
      <el-table-column label="标题" align="center" prop="name"/>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_audit_status"
            :value="scope.row.auditStatus"
          >
          </dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="消息类型" align="center" prop="msgType" min-width="80%">
        <template slot-scope="scope">
          <div v-if="scope.row.msgType === 10">
            <p>通知类</p>
          </div>
          <div v-if="scope.row.msgType === 20">
            <p>营销类</p>
          </div>
          <div v-if="scope.row.msgType === 30">
            <p>验证码类</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="expectPushTime">
        <template slot-scope="scope">
          <div v-if="scope.row.expectPushTime === null || scope.row.expectPushTime === '0'">
            <p>立即发送</p>
          </div>
          <div v-if="scope.row.expectPushTime != null && scope.row.expectPushTime != '0'">
            <p>{{ scope.row.expectPushTime }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发送渠道" align="center" prop="sendChannel">
        <template slot-scope="scope">
          <div v-if="scope.row.sendChannel === 'email'">
            <p>邮箱</p>
          </div>
          <div v-if="scope.row.sendChannel === 'sms'">
            <p>短信</p>
          </div>
          <div v-if="scope.row.sendChannel === 'dingDingRobot'">
            <p>钉钉群机器人</p>
          </div>
          <div v-if="scope.row.sendChannel === 'weChatServiceAccount'">
            <p>微信公众号</p>
          </div>
          <div v-if="scope.row.sendChannel === 'push'">
            <p>APP通知栏</p>
          </div>
          <div v-if="scope.row.sendChannel === 'feiShuRobot'">
            <p>飞书机器人</p>
          </div>
          <div v-if="scope.row.sendChannel === 'enterpriseWeChatRobot'">
            <p>企业微信机器人</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发送账号 " align="center" prop="sendAccount"/>
      <el-table-column label="创建者" align="center" prop="creator"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="110%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">

        <template slot-scope="scope">
          <el-button size="small"  style="margin-left: 0" type="info" icon="el-icon-view"
                     @click="handleShow(scope.row)">查看
          </el-button>
          <el-button size="small" style="margin-left: 0" type="success" icon="el-icon-check"
                     @click="handleAuditPass(scope.row.id)">通过
          </el-button>
          <el-button size="small" style="margin-left: 0" type="danger" icon="el-icon-close"
                     @click="handleAuditRejected(scope.row.id)">拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改消息模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1100px" append-to-body>

      <el-form ref="form" :model="form" label-width="80px" :readonly="true">
        <el-form-item label="标题" prop="name">
          <el-input v-model="form.name" placeholder="请输入标题"/>
        </el-form-item>
        <el-form-item label="推送类型" prop="pushType">
          <el-radio v-model="form.pushType" label="10">实时</el-radio>
          <el-radio v-model="form.pushType" label="20">定时</el-radio>
        </el-form-item>
        <el-form-item label="消息类型" prop="msgType">
          <el-radio v-model="form.msgType" label="10">通知类消息</el-radio>
          <el-radio v-model="form.msgType" label="20">营销类消息</el-radio>
          <el-radio v-model="form.msgType" label="30">验证码类消息</el-radio>
        </el-form-item>
        <div v-if="form.pushType === '20'">
          <el-form-item label="发送时间" prop="expectPushTime">
            <el-input v-model="form.expectPushTime" placeholder="期望发送时间 0为立即发送且只执行一次"/>
          </el-form-item>
        </div>
        <div v-if="form.pushType === '20'">
          <el-form-item label="人群文件" prop="cronCrowdPath">
            <file-upload v-model="form.cronCrowdPath"/>
          </el-form-item>
        </div>
        <el-form-item label="发送渠道" prop="sendChannel">
          <el-radio v-model="form.sendChannel" label="50" @change="getChannel_accounts(form.sendChannel)">APP通知栏</el-radio>
          <el-radio v-model="form.sendChannel" label="10" @change="getChannel_accounts(form.sendChannel)">邮箱</el-radio>
          <el-radio v-model="form.sendChannel" label="20" @change="getChannel_accounts(form.sendChannel)">短信</el-radio>
          <el-radio v-model="form.sendChannel" label="40" @change="getChannel_accounts(form.sendChannel)">微信公众号</el-radio>
          <el-radio v-model="form.sendChannel" label="30" @change="getChannel_accounts(form.sendChannel)">钉钉群机器人</el-radio>
          <el-radio v-model="form.sendChannel" label="60" @change="getChannel_accounts(form.sendChannel)">飞书机器人</el-radio>
          <el-radio v-model="form.sendChannel" label="70" @change="getChannel_accounts(form.sendChannel)">企业微信机器人</el-radio>
        </el-form-item>
        <div v-if="form.sendChannel !== '40'">
          <el-form-item label="账号配置">
            <el-select v-model="form.sendAccount" placeholder="请选择">
              <el-option
                v-for="item in sendAccounts"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </div>
        <div v-if="form.sendChannel === '40'">
          <el-form-item label="服务配置">
            <el-select v-model="form.sendAccount" placeholder="请选择">
              <el-option
                v-for="item in sendAccounts"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="消息模板">
            <el-select v-model="weChatServiceAccount.templateId" placeholder="请选择">
              <el-option
                v-for="item in weChatTemplates"
                :key="item.templateId"
                :label="item.title"
                :value="item.templateId">
              </el-option>
            </el-select>
          </el-form-item>
        </div>

        <!--        邮箱-->
        <div v-if="form.sendChannel === '10'">
          <el-form-item label="消息标题">
            <el-input
              type="textarea"
              autosize
              placeholder="占位符用${var}表示 留空将发送默认信息"
              v-model="email.title">
            </el-input>
          </el-form-item>
        </div>
        <div style="margin: 20px 0;"></div>
        <el-form-item label="消息内容" v-if="form.sendChannel === '10'">
          <el-input
            type="textarea"
            autosize
            placeholder="占位符用${var}表示 留空将发送默认信息"
            v-model="email.content">
          </el-input>
        </el-form-item>
        <div v-if="form.sendChannel === '10'">
          <el-form-item label="附件url">
            <el-input v-model="email.url" placeholder="邮箱附件地址 占位符用${var}表示"/>
          </el-form-item>
        </div>

        <!--        短信-->
        <div v-if="form.sendChannel === '20'">
          <el-form-item label="短信内容">
            <el-input v-model="sms.content" placeholder="短信内容 占位符用${var}表示 注意要与配置账号的短信模板占位符变量名一致 并且只发送变量数据"/>
          </el-form-item>
          <el-form-item label="链接">
            <el-input v-model="sms.url" placeholder="可选 短信链接 占位符用${url}表示 注意要与配置账号的短信模板占位符变量名一致 主要用于长链接转短链"/>
          </el-form-item>
        </div>

        <!--        钉钉群机器人-->
        <div v-if="form.sendChannel === '30'">
          <el-form-item label="发送类型">
            <el-radio v-model="dingDingRobot.sendType" label="10">文本类型(text)</el-radio>
            <el-radio v-model="dingDingRobot.sendType" label="20">链接类型(link)</el-radio>
            <el-radio v-model="dingDingRobot.sendType" label="30">markdown类型(markdown)</el-radio>
            <el-radio v-model="dingDingRobot.sendType" label="40">独立跳转actionCard类型(actionCard)</el-radio>
            <el-radio v-model="dingDingRobot.sendType" label="50">feedCard类型(feedCard)</el-radio>
          </el-form-item>

          <div v-if="dingDingRobot.sendType === '10'">
            <el-form-item label="文本内容">
              <el-input type="textarea" v-model="text.content" placeholder="消息内容 占位符用${var}表示"/>
            </el-form-item>
          </div>

          <div v-if="dingDingRobot.sendType === '20'">
            <el-form-item label="标题">
              <el-input v-model="link.title" placeholder="占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="内容">
              <el-input type="textarea" v-model="link.text" placeholder="消息内容 如果太长只会部分展示 占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="跳转URL">
              <el-input v-model="link.messageUrl" placeholder="点击消息跳转的URL 占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="图片URL">
              <el-input v-model="link.picUrl" placeholder="图片URL 占位符用${var}表示"/>
            </el-form-item>
          </div>

          <div v-if="dingDingRobot.sendType === '30'">
            <el-form-item label="标题">
              <el-input v-model="markdown.title" placeholder="首屏会话透出的展示内容 占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="内容">
              <el-input type="textarea" v-model="markdown.text"
                        placeholder="markdown格式的消息 目前只支持markdown语法的子集 占位符用${var}表示"/>
            </el-form-item>
          </div>

          <div v-if="dingDingRobot.sendType === '40'">
            <el-form-item label="标题">
              <el-input v-model="actionCard.title" placeholder="占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="内容">
              <el-input type="textarea" v-model="actionCard.text"
                        placeholder="markdown格式的消息 目前只支持markdown语法的子集 占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="按钮布局">
              <el-radio v-model="actionCard.btnOrientation" label="0">按钮竖直排列</el-radio>
              <el-radio v-model="actionCard.btnOrientation" label="1">按钮横向排列</el-radio>
            </el-form-item>
            <p></p>
            <el-form-item label="按钮数组">
              <el-input type="textarea" v-model="actionCard.btns" placeholder="请先点击添加按钮数组数据" :readonly="true"/>
            </el-form-item>

          </div>

          <div v-if="dingDingRobot.sendType === '50'">
            <p></p>
            <el-form-item label="图文数组">
              <el-input type="textarea" v-model="feedCard.links" placeholder="请先点击添加图文数组数据" :readonly="true"/>
            </el-form-item>

          </div>
        </div>

        <!--        微信服务号-->
        <div v-if="form.sendChannel === '40'">
          <el-form-item label="链接类型">
            <el-radio v-model="weChatServiceAccount.linkType" label="10">外部链接</el-radio>
            <!--            <el-radio v-model="weChatServiceAccount.linkType" label="20">小程序</el-radio>-->
          </el-form-item>
          <el-form-item label="跳转链接">
            <el-input v-model="weChatServiceAccount.url" placeholder="详情链接 需要占位符请填${跳转链接}"/>
          </el-form-item>
        </div>

        <!--        push通知栏-->
        <div v-if="form.sendChannel === '50'">
          <el-form-item label="发送标题">
            <el-input
              type="textarea"
              autosize
              placeholder="占位符用${var}表示"
              v-model="push.title">
            </el-input>
          </el-form-item>
          <el-form-item label="发送内容">
            <el-input
              type="textarea"
              autosize
              placeholder="占位符用${var}表示"
              v-model="push.body">
            </el-input>
          </el-form-item>
          <el-form-item label="后续动作">
            <el-radio v-model="push.clickType" label="url" @change="clear2('intent','payload')">打开网页地址</el-radio>
            <el-radio v-model="push.clickType" label="intent" @change="clear2('url','payload')">打开应用内特定页面</el-radio>
            <el-radio v-model="push.clickType" label="payload" @change="clear2('intent','url')">自定义消息内容启动应用</el-radio>
            <el-radio v-model="push.clickType" label="payload_custom" @change="clear2('intent','url')">自定义消息内容不启动应用</el-radio>
            <el-radio v-model="push.clickType" label="startapp" @change="clear3()">打开应用首页</el-radio>
            <el-radio v-model="push.clickType" label="none" @change="clear3()">纯通知，无后续动作</el-radio>
          </el-form-item>
          <el-form-item label="重要性">
            <el-radio v-model="push.channelLevel" label="0">无声音，无振动，不显示</el-radio>
            <el-radio v-model="push.channelLevel" label="1">无声音，无振动，锁屏不显示，通知栏中被折叠显示，导航栏无logo</el-radio>
            <el-radio v-model="push.channelLevel" label="2">无声音，无振动，锁屏和通知栏中都显示，通知不唤醒屏幕</el-radio>
            <el-radio v-model="push.channelLevel" label="3">有声音，无振动，锁屏和通知栏中都显示，通知唤醒屏幕</el-radio>
            <el-radio v-model="push.channelLevel" label="4">有声音，有振动，亮屏下通知悬浮展示，锁屏通知以默认形式展示且唤醒屏幕</el-radio>
          </el-form-item>
          <el-form-item label="网页地址" v-if="push.clickType === 'url'">
            <el-input v-model="push.url" placeholder="占位符用${var}表示"/>
          </el-form-item>
          <el-form-item label="应用页面" v-if="push.clickType === 'intent'">
            <el-input v-model="push.intent" placeholder="占位符用${var}表示"/>
          </el-form-item>
          <el-form-item label="透传消息" v-if="push.clickType === 'payload'">
            <el-input v-model="push.payload" placeholder="占位符用${var}表示 点击通知时 附加自定义透传消息 消息体格式可以自己定义，如纯文本、json串等"/>
          </el-form-item>
          <el-form-item label="透传消息" v-if="push.clickType === 'payload_custom'">
            <el-input v-model="push.payload" placeholder="占位符用${var}表示 点击通知时 附加自定义透传消息 消息体格式可以自己定义，如纯文本、json串等"/>
          </el-form-item>
        </div>

        <!--        飞书机器人-->
        <div v-if="form.sendChannel === '60'">
          <el-form-item label="发送类型">
            <el-radio v-model="feiShuRobot.msgType" label="10">文本类型(text)</el-radio>
          </el-form-item>

          <div v-if="feiShuRobot.msgType ==='10'">
            <el-form-item label="飞书内容">
              <el-input v-model="feiShuRobot.text.content" placeholder="飞书内容 占位符用${var}表示"/>
            </el-form-item>
          </div>

        </div>

        <!--        企业微信机器人-->
        <div v-if="form.sendChannel === '70'">
          <el-form-item label="发送类型">
            <el-radio v-model="enterpriseWeChatRobot.sendType" label="10">文本类型</el-radio>
            <el-radio v-model="enterpriseWeChatRobot.sendType" label="20">markdown类型</el-radio>
            <el-radio v-model="enterpriseWeChatRobot.sendType" label="30">图片类型</el-radio>
            <el-radio v-model="enterpriseWeChatRobot.sendType" label="40">图文类型</el-radio>
            <el-radio v-model="enterpriseWeChatRobot.sendType" label="50">文件类型</el-radio>
            <el-radio v-model="enterpriseWeChatRobot.sendType" label="60">语音类型</el-radio>
          </el-form-item>

          <div v-if="enterpriseWeChatRobot.sendType === '10'">
            <el-form-item label="文本内容">
              <el-input type="textarea" v-model="eWCRText.content" placeholder="文本内容 最长不超过2048个字节 支持使用<@userid>扩展语法来@群成员 占位符用${var}表示"/>
            </el-form-item>
          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '20'">
            <el-form-item label="内容">
              <el-input type="textarea" v-model="eWCRMarkdown.content"
                        placeholder="markdown内容 最长不超过4096个字节 目前只支持markdown语法的子集 支持使用<@userid>扩展语法来@群成员 占位符用${var}表示"/>
            </el-form-item>
          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '30'">
            <el-form-item label="base64">
              <el-input type="textarea" v-model="eWCRImage.base64" placeholder="图片内容的base64编码 占位符用${var}表示"/>
            </el-form-item>
            <el-form-item label="md5">
              <el-input type="textarea" v-model="eWCRImage.md5" placeholder="图片内容（base64编码前）的md5值 占位符用${var}表示"/>
            </el-form-item>
          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '40'">
            <p></p>
            <el-form-item label="图文数组">
              <el-input type="textarea" v-model="eWCRNews.articles" placeholder="请先点击添加图文数组数据" :readonly="true"/>
            </el-form-item>

          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '50'">

            <el-form-item label="文件id">
              <el-input v-model="eWCRFile.media_id" placeholder="文件id 文件大小不超过20M 通过文件上传接口获取 上传成功自动返回文件id id仅三天内有效"/>
            </el-form-item>
          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '60'">

            <el-form-item label="语音id">
              <el-input v-model="eWCRVoice.media_id"
                        placeholder="语音文件id 文件大小不超过2M 播放长度不超过60s 仅支持AMR格式 上传成功自动返回语音文件id id仅三天内有效"/>
            </el-form-item>
          </div>
        </div>
      </el-form>

    </el-dialog>
  </div>
</template>

<script>
import { listMessage_template_audit, getMessage_template_audit, delMessage_template_audit, addMessage_template_audit, updateMessage_template_audit } from "@/api/web/message_template_audit";
import {getChannel_accountBySenChannel, getMessage_template} from "@/api/web/message_template";

export default {
  name: "Message_template_audit",
  dicts: ['sys_push_type','sys_audit_status','sys_msg_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 消息模板表格数据
      message_templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: null,
        name: null,
        creator: null,
        createTime: null,
      },

      // 账号配置数组
      sendAccounts: [],
      email: {
        title: "",
        content: "",
        url: ""
      },
      sms: {
        content: "",
        url: ""
      },
      //钉钉机器人最终发送表单
      dingDingRobot: {
        sendType: "",
        content: ""
      },
      //钉钉机器人各数据类型
      text: {
        content: ""
      },
      link: {
        text: "",
        title: "",
        picUrl: "",
        messageUrl: ""
      },
      markdown: {
        title: "",
        text: ""
      },
      actionCard: {
        title: "",
        text: "",
        btnOrientation: "0",
        btns: ""
      },
      feedCard: {
        links: ""
      },
      //微信服务号数据类型
      weChatServiceAccount: {
        templateId: "",
        linkType: "10",
        url: ""
      },
      //通知栏数据类型
      push: {
        title: "",
        body: "",
        channelLevel: "",
        clickType: "",
        url: "",
        intent: "",
        payload: ""
      },
      feiShuRobot: {
        msgType: "",
        //文本类型
        text: {
          content: ""
        },

      },
      //企业微信机器人数据类型
      enterpriseWeChatRobot: {
        sendType: "",
        content: ""
      },
      eWCRText: {
        content: ""
      },
      eWCRMarkdown: {
        content: ""
      },
      eWCRImage: {
        base64: "",
        md5: ""
      },
      eWCRNews: {
        articles: ""
      },
      eWCRFile: {
        media_id: ""
      },
      eWCRVoice: {
        media_id: ""
      },
      weChatTemplates: [],
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {

    handleAuditPass(id){
      updateMessage_template_audit(id,20).then(response =>{
        this.$modal.msgSuccess("模板修改状态为：'审核通过'");
        this.getList();
      })
    },

    handleAuditRejected(id){
      updateMessage_template_audit(id,30).then(response =>{
        this.$modal.msgSuccess("模板修改状态为：'审核不通过'");
        this.getList();
      })
    },
    getChannel_accounts(sendChannel) {
      this.form.sendAccount = null
      getChannel_accountBySenChannel(sendChannel).then(response => {
        this.sendAccounts = response.data
      });
    },
    /** 查询模板审核列表 */
    getList() {
      this.loading = true;
      listMessage_template_audit(this.queryParams).then(response => {
        this.message_templateList= response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        msgStatus: null,
        pushType: null,
        cronTaskId: null,
        cronCrowdPath: null,
        expectPushTime: null,
        sendChannel: null,
        msgContent: null,
        sendAccount: null,
        creator: null,
        updator: null,
        isDeleted: null,
        createTime: null,
        updateTime: null
      };
      // this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

    handleShow(row) {
      this.isShowTem = false;
      this.reset();
      const id = row.id || this.ids
      getMessage_template(id).then(response => {
        this.form = response.data;
        let pushType = this.form.pushType.toString()
        let sendChannel = this.form.sendChannel.toString()
        let msgType = this.form.msgType.toString()
        this.form.pushType = pushType
        this.form.sendChannel = sendChannel
        this.form.msgType = msgType
        if (this.form.sendChannel === '10') {
          this.email = JSON.parse(this.form.msgContent)
        } else if (this.form.sendChannel === '20') {
          this.sms = JSON.parse(this.form.msgContent)
        } else if (this.form.sendChannel === '30') {
          this.dingDingRobot = JSON.parse(this.form.msgContent)
          this.dingDingRobotDataForShow()
        } else if (this.form.sendChannel === '40') {
          this.weChatServiceAccount = JSON.parse(this.form.msgContent)
        } else if (this.form.sendChannel === '50') {
          this.push = JSON.parse(this.form.msgContent)
        } else if (this.form.sendChannel === '60') {
          this.feiShuRobot = JSON.parse(this.form.msgContent)
        } else if (this.form.sendChannel === '70') {
          this.enterpriseWeChatRobot = JSON.parse(this.form.msgContent)
          this.enterpriseWeChatRobotDataForShow()
        }
        this.open = true;
        this.title = "查看消息模板";
      });
    },

    /**
     * 用于显示数据
     */

    enterpriseWeChatRobotDataForShow() {
      if (this.enterpriseWeChatRobot.sendType === '10') {
        this.eWCRText = this.enterpriseWeChatRobot.content
      }
      if (this.enterpriseWeChatRobot.sendType === '20') {
        this.eWCRMarkdown = this.enterpriseWeChatRobot.content
      }
      if (this.enterpriseWeChatRobot.sendType === '30') {
        this.eWCRImage = this.enterpriseWeChatRobot.content
      }
      if (this.enterpriseWeChatRobot.sendType === '40') {
        //图文类型
        this.eWCRNews = this.enterpriseWeChatRobot.content
      }
      if (this.enterpriseWeChatRobot.sendType === '50') {
        this.eWCRFile = this.enterpriseWeChatRobot.content
      }
      if (this.enterpriseWeChatRobot.sendType === '60') {
        this.eWCRVoice = this.enterpriseWeChatRobot.content
      }
      //赋值上传文件/语音额外参数
      this.uploadObjs = {
        sendAccount: this.form.sendAccount
      };
    },

    dingDingRobotDataForShow() {
      if (this.dingDingRobot.sendType === '10') {
        //文本类型
        this.text = this.dingDingRobot.content
      }
      if (this.dingDingRobot.sendType === '20') {
        //link类型
        this.link = this.dingDingRobot.content
      }
      if (this.dingDingRobot.sendType === '30') {
        //markdown类型
        this.markdown = this.dingDingRobot.content
      }
      if (this.dingDingRobot.sendType === '40') {
        //actionCard类型
        this.actionCard = this.dingDingRobot.content
      }
      if (this.dingDingRobot.sendType === '50') {
        //feedCard类型
        this.feedCard = this.dingDingRobot.content
      }
    },

  }
};
</script>
