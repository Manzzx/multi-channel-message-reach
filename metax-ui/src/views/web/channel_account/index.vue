<template>

  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="账号id" prop="id">
        <el-input
          v-model="queryParams.id"
          placeholder="请输入账号id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账号名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入账号名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="拥有者" prop="creator">
        <el-input
          v-model="queryParams.creator"
          placeholder="请输入拥有者"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['web:channel_account:add']"
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
          v-hasPermi="['web:channel_account:edit']"
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
          v-hasPermi="['web:channel_account:remove']"
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
          v-hasPermi="['web:channel_account:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="channel_accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="ID" align="center" prop="id"/>
      <el-table-column label="账号名称" align="center" prop="name"/>
      <el-table-column label="消息发送渠道" align="center" prop="sendChannel"/>
<!--      <el-table-column label="账号配置" align="center" prop="accountConfig"/>-->
      <el-table-column label="拥有者" align="center" prop="creator"/>
      <el-table-column label="创建时间" align="center" prop="created"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <div class="button-container">
            <el-button size="small" style="margin-left: 0" type="warning" icon="el-icon-edit"
                       @click="handleUpdate(scope.row)">修改
            </el-button>
            <el-button size="small" style="margin-left: 0" type="info" icon="el-icon-view"
                       @click="handleShow(scope.row)">查看
            </el-button>
            <el-button size="small"  style="margin-left: 0;" type="danger" icon="el-icon-delete"
                       @click="handleDelete(scope.row)">删除
            </el-button>
          </div>
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

    <!-- 添加或修改渠道账号对话框 -->
    <el-dialog :title="title" :visible.sync="open" :before-close="beforeCancel" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="账号名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入账号名称"/>
        </el-form-item>
        <el-form-item label="发送渠道" prop="sendChannel">
          <el-radio v-model="form.sendChannel" label="10">邮箱</el-radio>
          <el-radio v-model="form.sendChannel" label="20">短信</el-radio>
          <el-radio v-model="form.sendChannel" label="30">钉钉群机器人</el-radio>
          <el-radio v-model="form.sendChannel" label="40">微信公众号</el-radio>
          <el-radio v-model="form.sendChannel" label="50">APP通知栏</el-radio>
          <el-radio v-model="form.sendChannel" label="60">飞书机器人</el-radio>
        </el-form-item>
        <div v-if="form.sendChannel === '10'">
          <el-form-item label="账号配置" prop="accountConfig">
            <el-input v-model="form.accountConfig" type="textarea" placeholder="请输入内容"/>
            <p>
              qq邮箱配置案例:{"host":"smtp.qq.com","port":465,"user":"12345678@qq.com","pass":"fmlsdfsdfbawdsfea","from":"12345678@qq.com","starttlsEnable":"true","auth":true,"sslEnable":true}</p>
            <p>
              163邮箱配置案例:{"host":"smtp.163.com","port":465,"user":"12345678@163.com","pass":"USUFHISFJJSJDJ","from":"12345678@163.com","starttlsEnable":"true","auth":true,"sslEnable":true}
            </p>
          </el-form-item>
        </div>
        <div v-if="form.sendChannel === '20'">
          <el-form-item label="账号配置" prop="accountConfig">
            <el-input v-model="form.accountConfig" type="textarea" placeholder="请输入内容"/>
            <p>
              阿里云短信配置案例:{"regionId":"cn-hangzhou","accessKeyId":"LTt8JSkd6sf545derasfsfWJ","accessSecret":"kv6Jdsf5dsfsdfg34f6sdfA5iBI","templateCode":"SMS_20965498","signName":"xxx个人网站","serviceName":"alibabaCloudServiceSms"}</p>
            <p>
              腾讯云短信配置案例:{"endpoint":"sms.tencentcloudapi.com","region":"ap-guangzhou","secretId":"AKIFG3DSfdsfsd54g54456sgsdf","secretKey":"e9awD6Umsdfdsfd5654624554fdf3dgdfg9","smsSdkAppId":"12345678","templateId":"12345678","signName":"Metax日常个人公众号","serviceName":"tencentCloudServiceSms"}</p>
          </el-form-item>
        </div>
        <div v-if="form.sendChannel === '30'">
          <el-form-item label="账号配置" prop="accountConfig">
            <el-input v-model="form.accountConfig" type="textarea" placeholder="请输入内容"/>
            <p>
              钉钉机器人配置案例:{"secret":"SEC3c47ed78f08fsadsad546456fff8b696957df87fa42070","webhook":"https://oapi.dingtalk.com/robot/send?access_token=0c78024csdfds252556dsf94766228f6bcdbfdsg55asf54fgdfhewe5b2"}</p>
          </el-form-item>
        </div>
        <div v-if="form.sendChannel === '40'">
          <el-form-item label="账号配置" prop="accountConfig">
            <el-input v-model="form.accountConfig" type="textarea" placeholder="请输入内容"/>
            <p>微信公众号配置案例:{"appId":"wxfdssadasd5657676dfe","secret":"badgfdfh25ds5fsd26sdfsdfjnsd56456fdgfd07d"}</p>
          </el-form-item>
        </div>
        <div v-if="form.sendChannel === '50'">
          <el-form-item label="账号配置" prop="accountConfig">
            <el-input v-model="form.accountConfig" type="textarea" placeholder="请输入内容"/>
            <p>
              个推账号样例：{"appId":"y1vNsdfkm556df6566fdffsdflsf5XbE1","appKey":"gdfsdf1158s686hdsfdf25556sdf4k","masterSecret":"PDJSJDGc54545F86dfgKSFKfdgFF9"}</p>
          </el-form-item>
        </div>

        <div v-if="form.sendChannel === '60'">
          <el-form-item label="账号配置" prop="accountConfig">
            <el-input v-model="form.accountConfig" type="textarea" placeholder="请输入内容"/>
            <p>
              飞书机器人账号样例：{"webhook":"https://open.feishu.cn/open-apis/bot/v2/hook/d1d5686786dsdfskf-d4e1-sd54a4-bsdf361e-40sad1515sdaa4"}</p>
          </el-form-item>
        </div>

      </el-form>
      <div v-if="isShowTem" slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  listChannel_account,
  getChannel_account,
  delChannel_account,
  addChannel_account,
  updateChannel_account
} from "@/api/web/channel_account";

export default {
  name: "Channel_account",
  data() {
    return {
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
      // 渠道账号表格数据
      channel_accountList: [],
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
        sendChannel: null,
        creator: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {required: true, message: "账号名称不能为空", trigger: "blur"}
        ],
        sendChannel: [
          {required: true, message: "消息发送渠道：Email 短信 钉钉机器人 微信公众号 APP通知栏 飞书机器人", trigger: "change"}
        ],
        accountConfig: [
          {required: true, message: "账号配置不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    beforeCancel() {
      this.$confirm('是否关闭此页面？', '系统提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.open = false
      }).catch(() => {});
    },
    /** 查询渠道账号列表 */
    getList() {
      this.loading = true;
      listChannel_account(this.queryParams).then(response => {
        this.channel_accountList = response.rows;
        const arr = this.channel_accountList
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].sendChannel === 10) {
            this.channel_accountList[i].sendChannel = "邮箱"
          }
          if (arr[i].sendChannel === 20) {
            this.channel_accountList[i].sendChannel = "短信"
          }
          if (arr[i].sendChannel === 30) {
            this.channel_accountList[i].sendChannel = "钉钉群机器人"
          }
          if (arr[i].sendChannel === 40) {
            this.channel_accountList[i].sendChannel = "微信公众号"
          }
          if (arr[i].sendChannel === 50) {
            this.channel_accountList[i].sendChannel = "APP通知栏"
          }
          if (arr[i].sendChannel === 60){
            this.channel_accountList[i].sendChannel = "飞书机器人"
          }
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
        sendChannel: null,
        accountConfig: null,
        creator: null,
      };
      this.resetForm("form");
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
    /** 新增按钮操作 */
    handleAdd() {
      this.isShowTem = true;
      this.reset();
      this.open = true;
      this.title = "添加渠道账号";
    },

    handleShow(row) {
      this.isShowTem = false;
      this.reset();
      const id = row.id || this.ids
      getChannel_account(id).then(response => {
        this.form = response.data;
        let sendChannel = this.form.sendChannel.toString()
        this.form.sendChannel = sendChannel
        this.open = true;
        this.title = "查看渠道账号";
      });
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.isShowTem = true;
      this.reset();
      const id = row.id || this.ids
      getChannel_account(id).then(response => {
        this.form = response.data;
        let sendChannel = this.form.sendChannel.toString()
        this.form.sendChannel = sendChannel
        this.open = true;
        this.title = "修改渠道账号";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateChannel_account(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addChannel_account(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除渠道账号编号为"' + ids + '"的数据项？').then(function () {
        return delChannel_account(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('web/channel_account/export', {
        ...this.queryParams
      }, `channel_account_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style>
.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
</style>
