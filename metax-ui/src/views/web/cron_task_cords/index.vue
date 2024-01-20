<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板id" prop="messageTemplateId">
        <el-input
          v-model="queryParams.messageTemplateId"
          placeholder="请输入模板id"
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
          icon="el-icon-loading"
          size="mini"
          @click="startFlash()"
        >开启监听
        </el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-video-pause"
          size="mini"
          @click="stopFlash()"
        >停止监听
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>


    <el-table v-loading="loading" :data="cron_task_cordsList">
      <el-table-column label="模板id" align="center" prop="messageTemplateId"/>
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
      <el-table-column label="期望发送时间" align="center" prop="expectPushTime" min-width="100%">
        <template slot-scope="scope">
          <div v-if="scope.row.expectPushTime === null || scope.row.expectPushTime === '0'">
            <p>立即发送</p>
          </div>
          <div v-if="scope.row.expectPushTime != null && scope.row.expectPushTime != '0'">
            <p>{{ scope.row.expectPushTime }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="当前状态" align="center" prop="status" min-width="80%">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_cron_task_status"
            :value="scope.row.status"
          >
          </dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="启动阶段花费时间" align="center" prop="startTakeTime" min-width="95%">
        <template slot-scope="scope">
          <p>{{scope.row.startTakeTime}}毫秒</p>
        </template>
      </el-table-column>
      <el-table-column label="发送阶段花费时间" align="center" prop="sendTakeTime" min-width="95%">
        <template slot-scope="scope">
          <p>{{scope.row.sendTakeTime}}毫秒</p>
        </template>
      </el-table-column>
      <el-table-column label="总花费时间" align="center" prop="totalTakeTime">
        <template slot-scope="scope">
          <p>{{scope.row.totalTakeTime}}毫秒</p>
        </template>
      </el-table-column>
      <el-table-column label="任务开始时间" align="center" prop="startTime" min-width="110%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="启动时间" align="center" prop="schedulingTime" min-width="110%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.schedulingTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="更多信息" align="center">
        <template slot-scope="scope">
          <el-button type="info" icon="el-icon-view" size="mini" @click="showDetail(scope.row)">查看
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

    <!-- 更多详情弹窗-->
    <el-dialog
      title="发送详情"
      :visible.sync="dialogVisible"
      width="80%">
      <el-table v-loading="loading" :data="messageTemplate">
        <el-table-column label="发送时间" align="center" prop="sendingTime" >
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.sendingTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发送成功时间" align="center" prop="successTime" min-width="75%">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.successTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="暂停时间" align="center">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.stopTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发送失败时间" align="center" prop="failTime">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.failTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发送日志信息" align="center" prop="log"/>
      </el-table>
      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCron_task_cords,
} from "@/api/web/cron_task_cords";

export default {
  name: "Cron_task_cords",
  dicts: ['sys_cron_task_status'],
  data() {
    return {
      //定时器
      cronTimer: null,
      dialogVisible: false,
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
      // 定时模板链路追踪表格数据
      cron_task_cordsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        messageTemplateId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
      messageTemplate: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    showDetail(messageTemplate) {
      this.messageTemplate = []
      this.messageTemplate.push(messageTemplate)
      this.dialogVisible = true
    },

    startFlash() {
      this.cronTimer = setInterval(() => {
        this.flashCronTask()
      }, 1000)
      this.$message({
        message: '离开页面记得关闭监听哦!',
        type: 'warning'
      });
      this.$notify({
        title: '监听中',
        message: '正在实时监控定时模板消息...',
        type: 'success',
        duration: 5000
      });
      document.addEventListener('visibilitychange', this.handleVisibilityChange);
    },
    stopFlash() {
      clearInterval(this.cronTimer); // 关闭定时器
      document.removeEventListener('visibilitychange', this.handleVisibilityChange);
    },
    flashCronTask() {
      this.getList()
    },
    handleVisibilityChange() {
      if (document.visibilityState === 'hidden') {
        // 页面不可见，停止定时器
        this.stopFlash();
      }
    },

    /** 查询定时模板链路追踪列表 */
    getList() {
      this.loading = true;
      listCron_task_cords(this.queryParams).then(response => {
        this.cron_task_cordsList = response.rows;
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
        messageTemplateId: null,
        expectPushTime: null,
        status: null,
        log: null,
        startTime: null,
        schedulingTime: null,
        sendingTime: null,
        stopTime: null,
        successTime: null,
        failTime: null,
        startTakeTime: null,
        sendTakeTime: null,
        totalTakeTime: null
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

  }
};
</script>
