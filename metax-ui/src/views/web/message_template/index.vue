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
      <el-form-item label="渠道" prop="sendChannel">
        <el-input
          v-model="queryParams.sendChannel"
          placeholder="请输入消息渠道类型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable
                        v-model="queryParams.createTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择创建时间"
                        @keyup.enter.native="handleQuery"
        >
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['web:message_template:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['web:message_template:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['web:message_template:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['web:message_template:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="message_templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="id"/>
      <el-table-column label="标题" align="center" prop="name" width="110%"/>
      <el-table-column label="推送类型" align="center" prop="pushType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_push_type"
            :value="scope.row.pushType"
          >
          </dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_msg_status"
            :value="scope.row.msgStatus"
          >
          </dict-tag>
        </template>
      </el-table-column>
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

      <el-table-column label="发送渠道" align="center" prop="sendChannel" width="120%">
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
          <el-button size="small" type="primary" icon="el-icon-position"
                     v-if="scope.row.pushType == '实时'"
                     @click="openSendDialog(scope.row)">发送
          </el-button>
          <el-button size="small" style="margin-left: 0" type="success" icon="el-icon-caret-right"
                     v-if="scope.row.pushType == '定时' && scope.row.msgStatus == '已停用'"
                     @click="openStartDialog(scope.row)">启动
          </el-button>
          <el-button size="small" style="margin-left: 0" type="danger" icon="el-icon-caret-right"
                     v-if="scope.row.pushType == '定时' && scope.row.msgStatus == '已启用'"
                     @click="openStopDialog(scope.row)">暂停
          </el-button>
          <el-button size="small" style="margin-left: 0" type="warning" icon="el-icon-edit"
                     @click="handleUpdate(scope.row)">修改
          </el-button>
          <el-button size="small" style="margin-left: 0" type="info" icon="el-icon-view"
                     @click="handleShow(scope.row)">查看
          </el-button>
          <el-button size="small" style="margin-left: 0" type="danger" icon="el-icon-delete"
                     @click="handleDelete(scope.row)">删除
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
    <el-dialog :title="title" :visible.sync="open" :before-close="beforeCancel" width="1100px" append-to-body>

      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
            cron表达式生成网址:
            <el-link type="primary" @click="get_jecoco_report()" :underline="false">http://cron.ciding.cc/</el-link>
          </el-form-item>
        </div>
        <div v-if="form.pushType === '20'">
          <el-form-item label="人群文件" prop="cronCrowdPath">
            <file-upload v-model="form.cronCrowdPath"/>
          </el-form-item>
        </div>
        <el-form-item label="发送渠道" prop="sendChannel">
          <el-radio v-model="form.sendChannel" label="50" @change="getChannel_accounts(form.sendChannel)">APP通知栏
          </el-radio>
          <el-radio v-model="form.sendChannel" label="10" @change="getChannel_accounts(form.sendChannel)">邮箱</el-radio>
          <el-radio v-model="form.sendChannel" label="20" @change="getChannel_accounts(form.sendChannel)">短信</el-radio>
          <el-radio v-model="form.sendChannel" label="40" @change="getChannel_accounts(form.sendChannel)">微信公众号
          </el-radio>
          <el-radio v-model="form.sendChannel" label="30" @change="getChannel_accounts(form.sendChannel)">钉钉群机器人
          </el-radio>
          <el-radio v-model="form.sendChannel" label="60" @change="getChannel_accounts(form.sendChannel)">飞书机器人
          </el-radio>
          <el-radio v-model="form.sendChannel" label="70" @change="getChannel_accounts(form.sendChannel)">企业微信机器人
          </el-radio>
        </el-form-item>
        <div v-if="form.sendChannel !== '40'">
          <el-form-item label="账号配置">
            <el-select v-model="form.sendAccount" placeholder="请选择" @change="setUploadObjs(form.sendAccount)">
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
            <el-select v-model="form.sendAccount" placeholder="请选择" @change="getWxTemplateList(form.sendAccount)">
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
            placeholder="占位符用${var}表示 可插入HTML片段 留空将发送默认信息"
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
            <el-input v-model="sms.content" type="textarea"
                      placeholder="短信内容 占位符用${var}表示 注意要与配置账号的短信模板占位符变量名一致 并且只发送变量数据"/>
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
            <div>
              <el-button icon="el-icon-circle-plus-outline" type="primary" @click="btnsshowPopup">添加/修改按钮数组</el-button>
              <el-button icon="el-icon-circle-close" type="danger" @click="btnsresetForm('btnsaddJsonForm')">清除按钮数组
              </el-button>
            </div>
            <p></p>
            <el-form-item label="按钮数组">
              <el-input type="textarea" v-model="actionCard.btns" placeholder="请先点击添加按钮数组数据" :readonly="true"/>
            </el-form-item>

            <!-- actionCard按钮数组赋值-->
            <template>
              <div>
                <el-dialog
                  class="comn_dialog"
                  title="添加数据"
                  :visible.sync="btnsaddJsonVisible"
                  width="800px"
                  top="23vh"
                  append-to-body
                >
                  <el-button type="primary" @click="btnsaddTableItem">添加</el-button>
                  <el-button type="danger" @click="btnsdelTableItem">删除</el-button>

                  <el-form
                    :model="btnsaddJsonForm"
                    ref="btnsaddJsonForm"
                    :rules="btnsaddJsonForm.addJsonRules"
                    :inline="true"
                    label-width="108px"
                  >
                    <el-table
                      :data="btnsaddJsonForm.params"
                      style="width: 100%"
                      border
                      @selection-change="btnsaddJsonSelectionChange"
                    >
                      <el-table-column type="selection" width="55" align="center">
                      </el-table-column>

                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">标题</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.title'"
                            :rules="btnsaddJsonForm.addJsonRules.title"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.title"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>
                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">跳转链接</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.actionURL'"
                            :rules="btnsaddJsonForm.addJsonRules.actionURL"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.actionURL"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-form>
                  <span slot="footer" class="dialog-footer">
                    <el-button @click="btnsresetAddJsonPopup">取 消</el-button>
                    <el-button type="primary" @click="btnssubmitAddJsonPopup">确定</el-button>
                  </span>
                </el-dialog>
              </div>
            </template>
          </div>

          <div v-if="dingDingRobot.sendType === '50'">
            <div>
              <el-button icon="el-icon-circle-plus-outline" type="primary" @click="cardsshowPopup">添加/修改图文数组</el-button>
              <el-button icon="el-icon-circle-close" type="danger" @click="cardsresetForm('cardsaddJsonForm')">清除图文数组
              </el-button>
            </div>
            <p></p>
            <el-form-item label="图文数组">
              <el-input type="textarea" v-model="feedCard.links" placeholder="请先点击添加图文数组数据" :readonly="true"/>
            </el-form-item>

            <!-- feedCard卡片数组赋值-->
            <template>
              <div>
                <el-dialog
                  class="comn_dialog"
                  title="添加数据"
                  :visible.sync="cardsaddJsonVisible"
                  width="800px"
                  top="23vh"
                  append-to-body
                >
                  <el-button type="primary" @click="cardsaddTableItem">添加</el-button>
                  <el-button type="danger" @click="cardsdelTableItem">删除</el-button>

                  <el-form
                    :model="cardsaddJsonForm"
                    ref="cardsaddJsonForm"
                    :rules="cardsaddJsonForm.addJsonRules"
                    :inline="true"
                    label-width="108px"
                  >
                    <el-table
                      :data="cardsaddJsonForm.params"
                      style="width: 100%"
                      border
                      @selection-change="cardsaddJsonSelectionChange"
                    >
                      <el-table-column type="selection" width="55" align="center">
                      </el-table-column>

                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">标题</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.title'"
                            :rules="cardsaddJsonForm.addJsonRules.title"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.title"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>
                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">跳转链接</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.messageURL'"
                            :rules="cardsaddJsonForm.addJsonRules.messageURL"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.messageURL"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>

                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">图片链接</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.picURL'"
                            :rules="cardsaddJsonForm.addJsonRules.picURL"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.picURL"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>

                    </el-table>
                  </el-form>
                  <span slot="footer" class="dialog-footer">
                      <el-button @click="cardsresetAddJsonPopup">取 消</el-button>
                      <el-button type="primary" @click="cardssubmitAddJsonPopup">确定</el-button>
                    </span>
                </el-dialog>
              </div>
            </template>

          </div>
        </div>

        <!--        微信服务号-->
        <div v-if="form.sendChannel === '40'">
          <el-form-item label="链接类型">
            <el-radio v-model="weChatServiceAccount.linkType" label="10">外部链接</el-radio>
            <!--            <el-radio v-model="weChatServiceAccount.linkType" label="20">小程序</el-radio>-->
          </el-form-item>
          <el-form-item label="跳转链接">
            <el-input v-model="weChatServiceAccount.url" placeholder="详情链接 需要占位符请填${weChat_url} 注意只能填一个链接"/>
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
            <el-radio v-model="push.clickType" label="payload_custom" @change="clear2('intent','url')">自定义消息内容不启动应用
            </el-radio>
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
              <el-input v-model="feiShuRobot.text.content" type="textarea" placeholder="飞书内容 占位符用${var}表示"/>
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
            <p>图片转 BASE64 编码网址：https://www.jyshare.com/front-end/59/</p>
            <p>文件md5加密网址：http://www.metools.info/other/o21.html</p>
          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '40'">
            <div>
              <el-button icon="el-icon-circle-plus-outline" type="primary" @click="eWCRCardsshowPopup">添加/修改图文数组
              </el-button>
              <el-button icon="el-icon-circle-close" type="danger" @click="eWCRCardsresetForm('eWCRCardsaddJsonForm')">
                清除图文数组
              </el-button>
            </div>
            <p></p>
            <el-form-item label="图文数组">
              <el-input type="textarea" v-model="eWCRNews.articles" placeholder="请先点击添加图文数组数据" :readonly="true"/>
            </el-form-item>

            <!-- 企业微信机器人图文数组赋值-->
            <template>
              <div>
                <el-dialog
                  class="comn_dialog"
                  title="添加数据"
                  :visible.sync="eWCRCardsaddJsonVisible"
                  width="800px"
                  top="23vh"
                  append-to-body
                >
                  <el-button type="primary" @click="eWCRCardsaddTableItem">添加</el-button>
                  <el-button type="danger" @click="eWCRCardsdelTableItem">删除</el-button>

                  <el-form
                    :model="eWCRCardsaddJsonForm"
                    ref="eWCRCardsaddJsonForm"
                    :rules="eWCRCardsaddJsonForm.addJsonRules"
                    :inline="true"
                    label-width="108px"
                  >
                    <el-table
                      :data="eWCRCardsaddJsonForm.params"
                      style="width: 100%"
                      border
                      @selection-change="eWCRCardsaddJsonSelectionChange"
                    >
                      <el-table-column type="selection" width="55" align="center">
                      </el-table-column>

                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">标题</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.title'"
                            :rules="eWCRCardsaddJsonForm.addJsonRules.title"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.title"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>
                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">描述</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.description'"
                            :rules="eWCRCardsaddJsonForm.addJsonRules.description"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.description"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>
                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">跳转链接</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.url'"
                            :rules="eWCRCardsaddJsonForm.addJsonRules.url"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.url"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>

                      <el-table-column align="center">
                        <template slot="header" slot-scope="scope">
                          <span style="color:#2d65dc;">图片链接</span>
                          <i style="color:#F56C6C;">*</i>
                        </template>
                        <template slot-scope="scope">
                          <el-form-item
                            :prop="'params.' + scope.$index + '.picurl'"
                            :rules="eWCRCardsaddJsonForm.addJsonRules.picurl"
                          >
                            <el-input
                              type="text"
                              v-model="scope.row.picurl"
                              autocomplete="off"
                              placeholder="支持占位符${var}"
                            ></el-input>
                          </el-form-item>
                        </template>
                      </el-table-column>

                    </el-table>
                  </el-form>
                  <span slot="footer" class="dialog-footer">
                      <el-button @click="eWCRCardsresetAddJsonPopup">取 消</el-button>
                      <el-button type="primary" @click="eWCRCardssubmitAddJsonPopup">确定</el-button>
                    </span>
                </el-dialog>
              </div>
            </template>

          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '50'">
            <div v-if="form.sendAccount!=null">
              <el-upload
                ref="upload"
                class="upload-container"
                :action="uploadFileMaterial"
                :auto-upload="true"
                name="multipartFile"
                :with-credentials="true"
                :data="uploadObjs"
                :file-list="fileList"
                :headers="headers"
                :on-success="onUploadFileSuccess"

              >
                <el-button type="primary" size="mini" icon="el-icon-upload2">上传文件</el-button>
                <p></p>
              </el-upload>
            </div>

            <el-form-item label="文件id">
              <el-input v-model="eWCRFile.media_id" placeholder="文件id 文件大小不超过20M 通过文件上传接口获取 上传成功自动返回文件id id仅三天内有效"/>
            </el-form-item>
          </div>

          <div v-if="enterpriseWeChatRobot.sendType === '60'">
            <div v-if="form.sendAccount!=null">
              <el-upload
                ref="upload"
                class="upload-container"
                :action="uploadVoiceMaterial"
                :auto-upload="true"
                name="multipartFile"
                :with-credentials="true"
                :data="uploadObjs"
                :file-list="fileList"
                :headers="headers"
                :on-success="onUploadVoiceSuccess"
              >
                <el-button type="primary" size="mini" icon="el-icon-upload2">上传语音</el-button>
                <p></p>
              </el-upload>
            </div>

            <el-form-item label="语音id">
              <el-input v-model="eWCRVoice.media_id"
                        placeholder="语音文件id 文件大小不超过2M 播放长度不超过60s 仅支持AMR格式 上传成功自动返回语音文件id id仅三天内有效"/>
            </el-form-item>
            <p>音频转amr网址：https://www.aconvert.com/cn/format/amr/</p>
          </div>
        </div>
      </el-form>


      <div v-if="isShowTem" slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!--    发送消息对话框-->
    <el-dialog
      title="消息模板发送"
      :visible.sync="openSend"
      :before-close="beforeSendCancel"
      width="800px" append-to-body>
      <div v-if="ChannelReceiverType ==='10'">
        <el-form ref="sendForm" :model="sendForm" :rules="sendFormEmailRules" label-width="80px">
          <el-form-item label="邮箱" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="输入接受者地址,多个接受者用英文”,“隔开,有相同接受者会去重"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="ChannelReceiverType ==='20'">
        <el-form ref="sendForm" :model="sendForm" :rules="sendFormSmsRules" label-width="80px">
          <el-form-item label="手机号" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="输入接受者手机号,多个接受者用英文”,“隔开,有相同接受者会去重"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="ChannelReceiverType ==='30'">
        <el-form ref="sendForm" :model="sendForm" :rules="dingDingFormRules" label-width="80px">
          <el-form-item label="接受者" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="可输入@对象手机号或钉钉Id,@all为所有人,0为不@任何人,多个用英文”,“隔开"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="ChannelReceiverType ==='40'">
        <el-form ref="sendForm" :model="sendForm" :rules="sendFormWeChatServiceAccountRules" label-width="80px">
          <el-form-item label="微信号" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="输入接受者微信号,多个接受者用英文”,“隔开,有相同接受者会去重"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="ChannelReceiverType ==='50'">
        <el-form ref="sendForm" :model="sendForm" :rules="pushRules" label-width="80px">
          <el-form-item label="CID" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="输入应用CID,多个接受者用英文”,“隔开,有相同接受者会去重"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="ChannelReceiverType ==='60'">
        <el-form ref="sendForm" :model="sendForm" :rules="pushRules" label-width="80px">
          <el-form-item label="接受者" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="多个接受者用英文”,“隔开,有相同接受者会去重"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="ChannelReceiverType ==='70'">
        <el-form ref="sendForm" :model="sendForm" :rules="enterpriseWeChatRobotRules" label-width="80px">
          <el-form-item label="接受者" prop="receivers" required>
            <el-input v-model="sendForm.receivers" placeholder="仅文本类型支持输入@对象手机号/userid,@all表示提醒所有人,其余任意,多个用英文”,“隔开,相同会去重"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <template>
        <div>
          <div v-if="tableColumns !==undefined && tableColumns.length > 0">
            <el-button type="primary" @click="showPopup">赋值占位符数据</el-button>
            <el-button type="danger" @click="resetSendForm('myForm')">重置数据</el-button>
            <p></p>
            <el-input readonly v-model="FormInAddPopup.dataSourceJson" type="textarea" placeholder="数据预览(一组数据对应一个接受者)"/>
            <!--            <h3>-->
            <!--              数据预览(一组数据对应一个接受者):<span>{{ FormInAddPopup.dataSourceJson }}</span>-->
            <!--            </h3>-->
          </div>
          <el-dialog
            class="comn_dialog"
            title="添加数据"
            :visible.sync="addJsonVisible"
            width="800px"
            top="23vh"
            append-to-body
          >
            <el-button type="primary" @click="addTableItem">添加</el-button>
            <el-button type="danger" @click="delTableItem">删除</el-button>
            <el-form
              :model="addJsonForm"
              ref="addJsonForm"
              :rules="addJsonForm.addJsonRules"
              :inline="true"
              label-width="108px"
            >
              <el-table
                :data="addJsonForm.params"
                style="width: 100%"
                border
                @selection-change="addJsonSelectionChange"
              >
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column align="center" v-for="(column, index) in tableColumns" :label="column.label"
                                 :prop="column.prop" :key="index">
                  <template slot-scope="scope">

                    <el-form-item :prop="'params.' + scope.$index + '.' + column.prop"
                                  :rules="addJsonForm.addJsonRules[column.prop]">
                      <el-input type="text" v-model="scope.row[column.prop]" autocomplete="off"></el-input>
                    </el-form-item>
                  </template>
                </el-table-column>
              </el-table>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="resetAddJsonPopup">取 消</el-button>
              <el-button type="primary" @click="submitAddJsonPopup">确定</el-button>
          </span>
          </el-dialog>
        </div>
      </template>

      <span slot="footer" class="dialog-footer">
            <el-button @click="openSend = false">取 消</el-button>
            <el-button type="primary" @click="isSubmitSendForm">发送</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMessage_template,
  getMessage_template,
  delMessage_template,
  addMessage_template,
  updateMessage_template,
  getChannel_accountBySenChannel, listVariables, sendMessage, startMessage, stopMessage
} from "@/api/web/message_template";
import Link from "@/layout/components/Sidebar/Link";
import {listWxTemplate_account} from "@/api/web/channel_account";
import {getToken} from "@/utils/auth";

export default {
  name: "Message_template",
  dicts: ['sys_push_type', 'sys_audit_status', 'sys_msg_status'],
  components: {Link},
  data() {
    return {
      //是否能发送
      isCanSend: false,
      isShowTem: true,
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
      // 账号配置数组
      sendAccounts: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      openSend: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        pushType: null,
        msgContent: null,
        sendAccount: null,
        sendChannel: null,
        // creator: null,
      },

      uploadFileMaterial: process.env.VUE_APP_BASE_API + "/web/material/uploadFile", // 上传文件服务器地址
      uploadVoiceMaterial: process.env.VUE_APP_BASE_API + "/web/material/uploadVoice", // 上传语音服务器地址

      //企业微信图文数据
      eWCRCardsaddJsonVisible: false,
      eWCRCardsaddJsonMultiple: [],
      eWCRCardsFormInAddPopup: {
        eWCRCardsdataSourceJson: "" // 获取到的dataJson,显示为 [{name:"",value:""},{name:"",value:""}] 的格式
      },
      eWCRCardstabItemId: 1, // 表格数据的 id
      eWCRCardsaddJsonForm: {
        params: [],
        addJsonRules: {
          title: [
            {required: true, message: "请输入标题", trigger: "blur"}
          ],
          description: [
            {required: true, message: "请输入描述", trigger: "blur"}
          ],
          url: [
            {required: true, message: "请输入跳转链接", trigger: "blur"}
          ],
          picurl: [
            {required: true, message: "请输入图片链接", trigger: "blur"}
          ]
        }
      },

      //cards数据
      cardsaddJsonVisible: false,
      cardsaddJsonMultiple: [],
      cardsFormInAddPopup: {
        cardsdataSourceJson: "" // 获取到的dataJson,显示为 [{name:"",value:""},{name:"",value:""}] 的格式
      },
      cardstabItemId: 1, // 表格数据的 id
      cardsaddJsonForm: {
        params: [],
        addJsonRules: {
          title: [
            {required: true, message: "请输入标题", trigger: "blur"}
          ],
          messageURL: [
            {required: true, message: "请输入跳转链接", trigger: "blur"}
          ],
          picURL: [
            {required: true, message: "请输入图片链接", trigger: "blur"}
          ]
        }
      },

      //btns数据
      btnsaddJsonVisible: false,
      btnsaddJsonMultiple: [],
      btnsFormInAddPopup: {
        btnsdataSourceJson: "" // 获取到的dataJson,显示为 [{name:"",value:""},{name:"",value:""}] 的格式
      },
      btnstabItemId: 1, // 表格数据的 id
      btnsaddJsonForm: {
        params: [],
        addJsonRules: {
          title: [
            {required: true, message: "请输入标题", trigger: "blur"}
          ],
          actionURL: [
            {required: true, message: "请输入跳转链接", trigger: "blur"}
          ]
        }
      },
      //邮箱数据类型
      email: {
        title: "",
        content: "",
        url: ""
      },
      //短信数据类型
      sms: {
        content: "",
        url: ""
      },
      //钉钉机器人数据类型
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
      //飞书机器人数据类型
      feiShuRobot: {
        msgType: "",
        //文本类型
        text: {
          content: ""
        }
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
      form: {},
      sendForm: {
        messageTemplateId: "",
        receivers: "",
        //填充完占位符变量值的JSON
        variables: "",
        sendChannel: "",
        //0.没有 其他数字.占位符数量
        isExitVariables: ""
      },
      //当前发送窗口的接受者类型
      ChannelReceiverType: "",
      // 接受者表单校验
      sendFormEmailRules: {
        receivers: [
          {require: true, validator: this.validateEmails, trigger: 'blur'}
        ],
      },
      sendFormSmsRules: {
        receivers: [
          {require: true, validator: this.validateSmss, trigger: 'blur'}
        ],
      },
      dingDingFormRules: {
        title: [
          {required: true, message: "@对象为空", trigger: "blur"}
        ]

      },
      sendFormWeChatServiceAccountRules: {
        receivers: [
          {require: true, validator: this.validateWeChatServiceAccounts, trigger: 'blur'}
        ],
      },
      pushRules: {
        receivers: [
          {require: true, validator: this.validatePushs, trigger: 'blur'}
        ],
      },
      feiShuRobotRules: {
        receivers: [
          {require: true, message: "接受者为空", trigger: 'blur'}
        ],
      },
      enterpriseWeChatRobotRules: {
        receivers: [
          {require: true, message: "接受者为空", trigger: 'blur'}
        ],
      },

      // 表单校验
      rules: {
        name: [
          {required: true, message: "标题不能为空", trigger: "blur"}
        ],
        pushType: [
          {required: true, message: "消息推送类型不能为空", trigger: "change"}
        ],
        msgType: [
          {required: true, message: " 消息类型不能为空", trigger: "change"}
        ],
        cronCrowdPath: [
          {required: true, message: "定时发送人群的文件路径不能为空", trigger: "blur"}
        ],
        expectPushTime: [
          {required: true, message: "期望发送时间不能为空", trigger: "blur"}
        ],
        sendChannel: [
          {required: true, message: "消息发送渠道不能为空", trigger: "change"}
        ],
        msgContent: [
          {required: true, message: "不能为空", trigger: "blur"}
        ],
        sendAccount: [
          {required: true, message: "发送账号 不能为空", trigger: "change"}
        ],
      },
      //发送数据配置
      tableColumns: [],
      addJsonVisible: false,
      addJsonMultiple: [],
      FormInAddPopup: {
        dataSourceJson: "" // 获取到的dataJson,显示为 [{name:"",value:""},{name:"",value:""}] 的格式
      },
      tabItemId: 1, // 表格数据的 id
      addJsonForm: {
        params: [
          {
            tabItemId: 1 // 弹框中，删除空行的唯一标志，默认从1开始
          }
        ],
        addJsonRules: {}
      },
      uploadObjs: {},
      fileList: [],
      headers: {
        Authorization: "Bearer " + getToken(),
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    setUploadObjs(sendAccount) {
      this.uploadObjs = {
        sendAccount: sendAccount
      };
    },
    onUploadFileSuccess(res) {
      if (res.code !== 200) {
        this.$message.error(res.msg)
      } else {
        this.eWCRFile.media_id = res.msg
        this.$message.success("上传成功!已返回文件id")
      }
    },
    onUploadVoiceSuccess(res) {
      if (res.code !== 200) {
        this.$message.error(res.msg)
      } else {
        this.eWCRVoice.media_id = res.msg
        this.$message.success("上传成功!已返回语音文件id")
      }
    },

    beforeCancel() {
      this.$confirm('是否关闭此页面？', '系统提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.open = false
      }).catch(() => {
      });
    },

    beforeSendCancel() {
      this.$confirm('是否关闭此页面？', '系统提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openSend = false
      }).catch(() => {
      });
    },

    clear2(text1, text2) {
      if (text1 === 'intent' && text2 === 'payload') {
        this.push.intent = ""
        this.push.payload = ""
      }
      if (text1 === 'url' && text2 === 'payload') {
        this.push.url = ""
        this.push.payload = ""
      }
      if (text1 === 'intent' && text2 === 'url') {
        this.push.intent = ""
        this.push.url = ""
      }
    },
    clear3() {
      this.push.url = ""
      this.push.intent = ""
      this.push.payload = ""
    },

    getWxTemplateList(id) {
      listWxTemplate_account(id).then(response => {
        this.weChatTemplates = response.data
      })
    },

    // 企业微信机器人图文数据相关方法
    eWCRCardsresetForm(formName) {
      this.eWCRCardsFormInAddPopup.eWCRCardsdataSourceJson = "";
      this.$set(this.eWCRCardsaddJsonForm, "params", [{
        title: "",
        description: "",
        url: "",
        picurl: ""
      }]);
      this.eWCRNews.articles = ""
    },
    eWCRCardsshowPopup() {
      this.eWCRCardsaddJsonVisible = true;
    },
    eWCRCardsaddJsonSelectionChange(val) {
      this.eWCRCardsaddJsonMultiple = val;
    },
    eWCRCardsresetAddJsonPopup() {
      this.eWCRCardsaddJsonVisible = false;
    },
    eWCRCardssubmitAddJsonPopup() {
      //保存 固定值
      if (this.eWCRCardsaddJsonMultiple.length > 0) {
        this.$refs["eWCRCardsaddJsonForm"].validate(valid => {
          if (valid) {
            let result = [];
            this.eWCRCardsaddJsonMultiple.map(val => {
              // this.$delete(val, "tabItemId"); // 删除tabItemId属性
              result.push(val);
            });
            result.length ? (result = JSON.stringify(result)) : (result = "");
            this.eWCRCardsFormInAddPopup.eWCRCardsdataSourceJson = result;
            this.eWCRNews.articles = result;
            this.eWCRCardsaddJsonVisible = false;
          } else {
            return false;
          }
        });
      } else {
        this.$message.warning("请选择要保存的数据");
      }
    },
    eWCRCardsaddTableItem() {
      this.eWCRCardstabItemId = "T" + this.RndNum(6); //生成以T开头的七位随机数
      this.eWCRCardsaddJsonForm.params.push({
        tabItemId: this.eWCRCardstabItemId
      });
    },

    eWCRCardsdelTableItem() {
      // 确认删除
      if (this.eWCRCardsaddJsonMultiple.length > 0) {
        let arrs = [];
        let ids = this.eWCRCardsaddJsonMultiple.map(val => val.tabItemId); //拿到选中的数据id,
        this.eWCRCardsaddJsonForm.params.forEach(item => {
          if (!ids.includes(item.tabItemId)) {
            // 当id在params中，表示数据被选中，该将其删除，即将不被选中的保留
            arrs.push(item);
          }
        });
        this.eWCRCardsaddJsonForm.params = arrs;
      } else {
        this.$message.warning("请选择要删除的数据");
      }
    },

    // cards数据相关方法
    cardsresetForm(formName) {
      this.cardsFormInAddPopup.cardsdataSourceJson = "";
      this.$set(this.cardsaddJsonForm, "params", [{
        title: "",
        messageURL: "",
        picURL: ""
      }]);
      this.feedCard.links = ""
    },
    cardsshowPopup() {
      this.cardsaddJsonVisible = true;
    },
    cardsaddJsonSelectionChange(val) {
      this.cardsaddJsonMultiple = val;
    },
    cardsresetAddJsonPopup() {
      //关闭 固定值弹窗
      // this.$set(this.cardsaddJsonForm, "params", []);
      this.cardsaddJsonVisible = false;
    },
    cardssubmitAddJsonPopup() {
      //保存 固定值
      if (this.cardsaddJsonMultiple.length > 0) {
        this.$refs["cardsaddJsonForm"].validate(valid => {
          if (valid) {
            let result = [];
            this.cardsaddJsonMultiple.map(val => {
              // this.$delete(val, "tabItemId"); // 删除tabItemId属性
              result.push(val);
            });
            result.length ? (result = JSON.stringify(result)) : (result = "");
            this.cardsFormInAddPopup.cardsdataSourceJson = result;
            this.feedCard.links = result;
            this.cardsaddJsonVisible = false;
          } else {
            return false;
          }
        });
      } else {
        this.$message.warning("请选择要保存的数据");
      }
    },
    cardsaddTableItem() {
      this.cardstabItemId = "T" + this.RndNum(6); //生成以T开头的七位随机数
      this.cardsaddJsonForm.params.push({
        tabItemId: this.cardstabItemId
      });
    },

    cardsdelTableItem() {
      // 确认删除
      if (this.cardsaddJsonMultiple.length > 0) {
        let arrs = [];
        let ids = this.cardsaddJsonMultiple.map(val => val.tabItemId); //拿到选中的数据id,
        this.cardsaddJsonForm.params.forEach(item => {
          if (!ids.includes(item.tabItemId)) {
            // 当id在params中，表示数据被选中，该将其删除，即将不被选中的保留
            arrs.push(item);
          }
        });
        this.cardsaddJsonForm.params = arrs;
      } else {
        this.$message.warning("请选择要删除的数据");
      }
    },


    // btns数据相关方法
    btnsresetForm(formName) {
      this.btnsFormInAddPopup.btnsdataSourceJson = "";
      this.$set(this.btnsaddJsonForm, "params", [{
        title: "",
        actionURL: ""
      }]);
      this.actionCard.btns = ""
    },
    btnsshowPopup() {
      this.btnsaddJsonVisible = true;
    },
    btnsaddJsonSelectionChange(val) {
      this.btnsaddJsonMultiple = val
    },
    btnsresetAddJsonPopup() {
      //关闭 固定值弹窗
      // this.$set(this.btnsaddJsonForm, "params", []);
      this.btnsaddJsonVisible = false;
    },
    btnssubmitAddJsonPopup() {
      //保存 固定值
      if (this.btnsaddJsonMultiple.length > 0) {
        this.$refs["btnsaddJsonForm"].validate(valid => {
          if (valid) {
            let result = [];
            this.btnsaddJsonMultiple.map(val => {
              // this.$delete(val, "tabItemId"); // 删除tabItemId属性
              result.push(val);
            });
            result.length ? (result = JSON.stringify(result)) : (result = "");
            this.btnsFormInAddPopup.btnsdataSourceJson = result;
            this.actionCard.btns = result;
            this.btnsaddJsonVisible = false;
          } else {
            return false;
          }
        });
      } else {
        this.$message.warning("请选择要保存的数据");
      }
    },
    btnsaddTableItem() {
      this.btnstabItemId = "T" + this.RndNum(6); //生成以T开头的七位随机数
      this.btnsaddJsonForm.params.push({
        tabItemId: this.btnstabItemId
      });
    },

    btnsdelTableItem() {
      // 确认删除
      if (this.btnsaddJsonMultiple.length > 0) {
        let arrs = [];
        let ids = this.btnsaddJsonMultiple.map(val => val.tabItemId); //拿到选中的数据id,
        this.btnsaddJsonForm.params.forEach(item => {
          if (!ids.includes(item.tabItemId)) {
            // 当id在params中，表示数据被选中，该将其删除，即将不被选中的保留
            arrs.push(item);
          }
        });
        this.btnsaddJsonForm.params = arrs;
      } else {
        this.$message.warning("请选择要删除的数据");
      }
    },

    get_jecoco_report() {
      window.open('http://cron.ciding.cc/');
    },
    //定时任务暂停弹窗
    openStopDialog(row) {
      this.$confirm('确认暂停该消息定时任务吗?', '暂停确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.stopTask(row)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已暂停'
        });
      });
    },
    //定时任务启动弹窗
    openStartDialog(row) {
      this.$confirm('确认启动该消息定时任务吗?', '启动确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.startTask(row)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消启动'
        });
      });
    },
    startTask(row) {
      startMessage(row.id).then(response => {
        this.$modal.msgSuccess("定时任务启动成功");
        this.getList()
        this.openSend = false
      })
    },

    stopTask(row) {
      stopMessage(row.id).then(response => {
        this.$modal.msgSuccess("定时任务已暂停");
        this.getList()
        this.openSend = false
      })
    },

    // 发送消息方法 近期更新
    resetSendForm(formName) {
      this.FormInAddPopup.dataSourceJson = "";
      this.$set(this.addJsonForm, "params", []);
    },
    RndNum(n) {
      // 生成随机数
      let rdmNum = "";
      for (let i = 0; i < n; i++) {
        rdmNum += Math.floor(Math.random() * 10); // [0,10)的整数
      }
      return rdmNum;
    },
    //弹出占位符填充对话框
    showPopup() {
      this.addJsonVisible = true;
    },

    addJsonSelectionChange(val) {
      this.addJsonMultiple = val;
    },
    resetAddJsonPopup() {
      //关闭 固定值弹窗
      // this.$set(this.addJsonForm, "params", []);
      this.addJsonVisible = false;
    },
    submitAddJsonPopup() {
      //保存 固定值
      if (this.addJsonMultiple.length > 0) {
        this.$refs["addJsonForm"].validate(valid => {
          if (valid) {
            let result = [];
            this.addJsonMultiple.map(val => {
              this.$delete(val, "tabItemId"); // 删除tabItemId属性
              result.push(val);
            });
            result.length ? (result = JSON.stringify(result)) : (result = "");
            this.FormInAddPopup.dataSourceJson = result;
            this.addJsonVisible = false;
          } else {
            return false;
          }
        });
      } else {
        this.$message.warning("请选择要保存的数据");
      }
    },
    addTableItem() {
      this.tabItemId = "T" + this.RndNum(6); //生成以T开头的七位随机数
      this.addJsonForm.params.push({
        tabItemId: this.tabItemId
      });
    },
    delTableItem() {
      // 确认删除
      if (this.addJsonMultiple.length > 0) {
        let arrs = [];
        let ids = this.addJsonMultiple.map(val => val.tabItemId); //拿到选中的数据id,
        this.addJsonForm.params.forEach(item => {
          if (!ids.includes(item.tabItemId)) {
            // 当id在params中，表示数据被选中，该将其删除，即将不被选中的保留
            arrs.push(item);
          }
        });
        this.addJsonForm.params = arrs;
      } else {
        this.$message.warning("请选择要删除的数据");
      }
    },

    openSendDialog(row) {
      this.sendForm.messageTemplateId = row.id
      let channel = ""
      if (row.sendChannel === "email") {
        channel = 10
      } else if (row.sendChannel === "sms") {
        channel = 20
      } else if (row.sendChannel === "dingDingRobot") {
        channel = 30
      } else if (row.sendChannel === "weChatServiceAccount") {
        channel = 40
      } else if (row.sendChannel === "push") {
        channel = 50
      } else if (row.sendChannel === "feiShuRobot") {
        channel = 60
      } else if (row.sendChannel === "enterpriseWeChatRobot") {
        channel = 70
      }
      this.ChannelReceiverType = channel.toString()
      this.sendForm.sendChannel = channel
      this.tableColumns = []
      this.resetSendForm()
      listVariables(row.id).then(response => {
        const tableData = response.rows;
        for (let i = 0; i < tableData.length; i++) {
          const column = {
            label: tableData[i], // 列的标签
            prop: tableData[i] // 列的数据属性
          };
          this.tableColumns.push(column);
        }
      });
      this.openSend = true
      this.isCanSend = true
    },

    validateEmails(rule, value, callback) {
      // 将输入的邮箱地址以逗号分隔为数组
      const emailsArray = value.split(',');

      // 遍历数组，检查每个邮箱地址是否合法
      for (const email of emailsArray) {
        if (!this.validateEmail(email)) {
          callback(new Error('请输入有效的邮箱地址'));
          return;
        }
      }
      callback(); // 校验通过
    },
    //检验邮箱规则
    validateEmail(email) {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailPattern.test(email);
    },

    validateSmss(rule, value, callback) {
      // 将输入的手机号以逗号分隔为数组
      const smssArray = value.split(',');

      // 遍历数组，检查每个手机号是否合法
      for (const sms of smssArray) {
        if (!this.validateSms(sms)) {
          callback(new Error('请输入有效的手机号'));
          return;
        }
      }
      callback(); // 校验通过
    },
    //检验手机号规则
    validateSms(sms) {
      const smsPattern = /^1[3456789]\d{9}$/;
      return smsPattern.test(sms);
    },

    validateWeChatServiceAccounts(rule, value, callback) {
      // 将输入的微信号以逗号分隔为数组
      const wxsArray = value.split(',');

      const regex = /^[^,，]+(,[^,，]+)*$/;
      // 遍历数组，检查每个微信号是否合法
      for (const wx of wxsArray) {
        if (!regex.test(wx)) {
          callback(new Error('请输入有效的微信号'));
          return;
        }
      }
      callback(); // 校验通过
    },

    validatePushs(rule, value, callback) {
      // 将输入的cid以逗号分隔为数组
      const cidsArray = value.split(',');

      const regex = /^[^,，]+(,[^,，]+)*$/;
      // 遍历数组，检查每个cid是否合法
      for (const cid of cidsArray) {
        if (!regex.test(cid)) {
          callback(new Error('请输入有效的CID'));
          return;
        }
      }
      callback(); // 校验通过
    },

    /** 查询消息模板列表 */
    getList() {
      this.loading = true;
      listMessage_template(this.queryParams).then(response => {
        this.message_templateList = response.rows;
        for (let i = 0; i < this.message_templateList.length; i++) {
          let msgStatus = this.message_templateList[i].msgStatus.toString()
          this.message_templateList[i].msgStatus = msgStatus
        }
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
        msgType: null,
        msgStatus: null,
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
      this.email = {
        title: null,
        content: null,
        url: null
      };
      this.push = {
        title: "",
        body: "",
        clickType: "",
        url: "",
        intent: "",
        payload: ""
      };
      this.feiShuRobot = {
        msgType: "",
        text: {
          content: ""
        },
      }

      // this.resetForm("form");
    },
    getChannel_accounts(sendChannel) {
      this.form.sendAccount = null
      getChannel_accountBySenChannel(sendChannel).then(response => {
        this.sendAccounts = response.data
      });
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    resetData() {
      this.isShowTem = true
      this.reset();
      this.sms.content = ""
      //钉钉机器人各数据类型
      this.text.content = ""
      this.link.text = ""
      this.link.title = ""
      this.link.picUrl = ""
      this.link.messageUrl = ""

      this.markdown.title = ""
      this.markdown.text = ""

      this.actionCard.title = ""
      this.actionCard.text = ""
      this.actionCard.btnOrientation = ""
      this.actionCard.btns = ""

      this.feedCard.links = ""

      this.weChatServiceAccount.templateId = ""
      this.weChatServiceAccount.url = ""

      this.eWCRText.content = ""
      this.eWCRMarkdown.content = ""
      this.eWCRImage.base64 = ""
      this.eWCRImage.md5 = ""
      this.eWCRNews.articles = ""
      this.eWCRFile.media_id = ""
      this.eWCRVoice.media_id = ""
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.resetData()
      this.open = true;
      this.title = "添加消息模板";
    },
    handleShow(row) {
      this.isShowTem = false;
      this.reset();
      this.btnsaddJsonForm.params = []
      this.cardsaddJsonForm.params = []
      this.eWCRCardsaddJsonForm.params = []
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
    handleUpdate(row) {
      this.resetData
      this.isShowTem = true
      this.doHandleUpdate(row)
    },
    /** 修改按钮操作 */
    doHandleUpdate(row) {
      this.reset();
      this.btnsaddJsonForm.params = []
      this.cardsaddJsonForm.params = []
      this.eWCRCardsaddJsonForm.params = []
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
        this.title = "修改消息模板";
      });
    },
    //确认消息发送弹窗
    isSubmitSendForm() {
      this.$confirm('确认发送此消息吗?', '发送确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // if (this.isCanSend){
        //   this.submitSendForm()
        // }
        this.submitSendForm()
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消发送'
        });
      });
    },
    // 发送信息表单
    submitSendForm() {
      this.$refs["sendForm"].validate(valid => {
        if (valid) {
          this.isCanSend = false
          this.$set(this.sendForm, "variables", this.FormInAddPopup.dataSourceJson)
          if (this.tableColumns !== undefined && this.tableColumns.length > 0) {
            this.$set(this.sendForm, "isExitVariables", this.tableColumns.length)
          } else {
            this.$set(this.sendForm, "isExitVariables", 0)
          }
          sendMessage(this.sendForm).then(response => {
            this.$modal.msgSuccess("发送消息成功");
            this.openSend = false
          });
        }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            //设置发送渠道内容
            if (this.form.sendChannel === '10') {
              this.setEmail()
              this.$set(this.form, "msgContent", JSON.stringify(this.email))
            }
            if (this.form.sendChannel === '20') {
              this.$set(this.form, "msgContent", JSON.stringify(this.sms))
            }
            if (this.form.sendChannel === '30') {
              this.dingDingRobotDataForAdd()
              //判断actionCard和FeedCard里面是数组是否为空
              if (this.dingDingRobot.sendType === '40') {
                if (!this.actionCard.btns || this.actionCard.btns.length <= 0) {
                  this.$modal.alertError("actionCard按钮数组为空")
                  return
                }
              }
              if (this.dingDingRobot.sendType === '50') {
                if (!this.feedCard.links || this.feedCard.links.length <= 0) {
                  this.$modal.alertError("feedCard图文数组为空")
                  return
                }
              }
            }
            if (this.form.sendChannel === '40') {
              this.$set(this.form, "msgContent", JSON.stringify(this.weChatServiceAccount))
            }
            if (this.form.sendChannel === '50') {
              this.$set(this.form, "msgContent", JSON.stringify(this.push))
            }
            if (this.form.sendChannel === '60') {
              this.$set(this.form, "msgContent", JSON.stringify(this.feiShuRobot))
            }
            if (this.form.sendChannel === '70') {
              this.enterpriseWeChatRobotDataForAdd()
              if (this.enterpriseWeChatRobot.sendType === '40') {
                if (!this.eWCRNews.articles || this.eWCRNews.articles.length <= 0) {
                  this.$modal.alertError("图文数组为空")
                  return
                }
              }
            }
            updateMessage_template(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            //设置发送渠道内容
            if (this.form.sendChannel === '10') {
              this.setEmail()
              this.$set(this.form, "msgContent", JSON.stringify(this.email))
            }
            if (this.form.sendChannel === '20') {
              this.$set(this.form, "msgContent", JSON.stringify(this.sms))
            }
            if (this.form.sendChannel === '30') {
              this.dingDingRobotDataForAdd()
              //判断actionCard和FeedCard里面是数组是否为空
              if (this.dingDingRobot.sendType === '40') {
                if (!this.actionCard.btns || this.actionCard.btns.length <= 0) {
                  this.$modal.alertError("actionCard按钮数组为空")
                  return
                }
              }
              if (this.dingDingRobot.sendType === '50') {
                if (!this.feedCard.links || this.feedCard.links.length <= 0) {
                  this.$modal.alertError("feedCard图文数组为空")
                  return
                }
              }
            }
            if (this.form.sendChannel === '40') {
              this.$set(this.form, "msgContent", JSON.stringify(this.weChatServiceAccount))
            }
            if (this.form.sendChannel === '50') {
              this.$set(this.form, "msgContent", JSON.stringify(this.push))
            }
            if (this.form.sendChannel === '60') {
              this.$set(this.form, "msgContent", JSON.stringify(this.feiShuRobot))
            }
            if (this.form.sendChannel === '70') {
              this.enterpriseWeChatRobotDataForAdd()
              if (this.enterpriseWeChatRobot.sendType === '40') {
                if (!this.eWCRNews.articles || this.eWCRNews.articles.length <= 0) {
                  this.$modal.alertError("图文数组为空")
                  return
                }
              }
            }
            addMessage_template(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    setEmail() {
      if (!this.email.title) {
        this.email.title = "Metax默认标题"
      }
      if (!this.email.content) {
        this.email.content = "Metax默认内容"
      }

    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除消息模板编号为"' + ids + '"的数据项？').then(function () {
        return delMessage_template(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('web/message_template/export', {
        ...this.queryParams
      }, `message_template_${new Date().getTime()}.xlsx`)
    },
    /**
     * 赋值到钉钉机器人最终发送表单
     */
    dingDingRobotDataForAdd() {
      if (this.dingDingRobot.sendType === '10') {
        //文本类型
        this.$set(this.dingDingRobot, "content", this.text)
      }
      if (this.dingDingRobot.sendType === '20') {
        //link类型
        this.$set(this.dingDingRobot, "content", this.link)
      }
      if (this.dingDingRobot.sendType === '30') {
        //markdown类型
        this.$set(this.dingDingRobot, "content", this.markdown)
      }
      if (this.dingDingRobot.sendType === '40') {
        //actionCard类型
        this.$set(this.dingDingRobot, "content", this.actionCard)
      }
      if (this.dingDingRobot.sendType === '50') {
        //feedCard类型
        this.$set(this.dingDingRobot, "content", this.feedCard)
      }
      this.$set(this.form, "msgContent", JSON.stringify(this.dingDingRobot))

    },

    /**
     * 赋值到企业微信机器人最终发送表单
     */
    enterpriseWeChatRobotDataForAdd() {
      if (this.enterpriseWeChatRobot.sendType === '10') {
        //文本类型
        this.$set(this.enterpriseWeChatRobot, "content", this.eWCRText)
      }
      if (this.enterpriseWeChatRobot.sendType === '20') {
        //MarkDown类型
        this.$set(this.enterpriseWeChatRobot, "content", this.eWCRMarkdown)
      }
      if (this.enterpriseWeChatRobot.sendType === '30') {
        //图片类型
        this.$set(this.enterpriseWeChatRobot, "content", this.eWCRImage)
      }
      if (this.enterpriseWeChatRobot.sendType === '40') {
        //图文类型
        this.$set(this.enterpriseWeChatRobot, "content", this.eWCRNews)
      }
      if (this.enterpriseWeChatRobot.sendType === '50') {
        //file类型
        this.$set(this.enterpriseWeChatRobot, "content", this.eWCRFile)
      }
      if (this.enterpriseWeChatRobot.sendType === '60') {
        //feedCard类型
        this.$set(this.enterpriseWeChatRobot, "content", this.eWCRVoice)
      }
      this.$set(this.form, "msgContent", JSON.stringify(this.enterpriseWeChatRobot))

    },

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
        const arr = JSON.parse(this.eWCRNews.articles || '[]')
        for (let i = 0; i < arr.length; i++) {
          const column = {
            title: arr[i].title,
            description: arr[i].description,
            url: arr[i].url,
            picurl: arr[i].picurl,
            tabItemId: "T" + this.RndNum(6) //生成以T开头的七位随机数
          };
          this.eWCRCardsaddJsonForm.params.push(column);
        }
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
        const arr = JSON.parse(this.actionCard.btns || '[]')
        for (let i = 0; i < arr.length; i++) {
          const column = {
            title: arr[i].title, // 列的标签
            actionURL: arr[i].actionURL, // 列的数据属性
            tabItemId: "T" + this.RndNum(6) //生成以T开头的七位随机数
          };
          this.btnsaddJsonForm.params.push(column);

        }
      }
      if (this.dingDingRobot.sendType === '50') {
        //feedCard类型
        this.feedCard = this.dingDingRobot.content
        const arr = JSON.parse(this.feedCard.links || '[]')
        for (let i = 0; i < arr.length; i++) {
          const column = {
            title: arr[i].title, // 列的标签
            messageURL: arr[i].messageURL, // 列的数据属性
            picURL: arr[i].picURL,
            tabItemId: "T" + this.RndNum(6) //生成以T开头的七位随机数
          };
          this.cardsaddJsonForm.params.push(column);
        }
      }
    },
  },
};
</script>
