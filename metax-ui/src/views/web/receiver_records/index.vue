<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="接受者" prop="receiver">
        <el-input
          v-model="queryParams.receiver"
          placeholder="请输入接受者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发送日期" prop="sendMessageKey">
        <el-date-picker clearable
                        v-model="queryParams.sendMessageKey"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择消息发送日期">
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

    <el-table v-loading="loading" :data="receiver_records_list">
      <el-table-column label="接受者" align="center" prop="receiver" min-width="70%"/>
      <el-table-column label="发送模板" align="center" min-width="90%">
        <template slot-scope="scope">
          {{ scope.row.sendTask.messageTemplate.name }}
        </template>
      </el-table-column>
      <el-table-column label="推送类型" align="center" min-width="40%">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_push_type"
            :value="scope.row.sendTask.messageTemplate.pushType"
          >
          </dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送渠道" align="center" min-width="80%">
        <template slot-scope="scope">
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'email'">
            <p>邮箱</p>
          </div>
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'sms'">
            <p>短信</p>
          </div>
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'dingDingRobot'">
            <p>钉钉群机器人</p>
          </div>
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'weChatServiceAccount'">
            <p>微信公众号</p>
          </div>
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'push'">
            <p>APP通知栏</p>
          </div>
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'feiShuRobot'">
            <p>飞书机器人</p>
          </div>
          <div v-if="scope.row.sendTask.messageTemplate.sendChannel === 'enterpriseWeChatRobot'">
            <p>企业微信机器人</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发送状态" align="center" min-width="50%">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.sys_send_task_status"
            :value="scope.row.sendTask.messageTemplate.msgStatus"
          >
          </dict-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送方" align="center" min-width="60%">
        <template slot-scope="scope">
          {{ scope.row.sendTask.sender }}
        </template>
      </el-table-column>

      <el-table-column label="发送阶段开始时间" align="center" min-width="90%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTask.sendStartTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发送阶段结束时间" align="center" min-width="90%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTask.sendEndTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="耗时" align="center" prop="takeTime">
        <template slot-scope="scope">
          <p>{{ scope.row.sendTask.takeTime }}毫秒</p>
        </template>
      </el-table-column>
      <el-table-column label="发送详情" align="center" prop="messageTemplate" min-width="50%">
        <template slot-scope="scope">
          <el-button type="info" icon="el-icon-view" size="mini"
                     @click="showDetail(scope.row.sendTask.messageTemplate)">查看
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


    <!-- 详情弹窗-->
    <el-dialog
      title="发送详情"
      :visible.sync="dialogVisible"
      width="80%">
      <el-table v-loading="loading" :data="messageTemplate">
        <el-table-column label="模板ID" align="center" prop="id"/>
        <el-table-column label="期望发送时间" align="center" prop="expectPushTime"/>
        <el-table-column label="消息内容" align="center" prop="msgContent" min-width="160%"/>
        <el-table-column label="发送日志" align="center" prop="sendLogs" min-width="120%"/>
        <el-table-column label="第三方渠道配置账号 " align="center" prop="sendAccount"/>
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
  listSend_task_info,
  getSend_task_info,
  delSend_task_info,
  addSend_task_info,
  updateSend_task_info
} from "@/api/web/send_task_info";
import {list_receiver_records} from "@/api/web/receiver_records";

export default {
  name: "receiver_records",
  dicts: ['sys_send_task_status', 'sys_push_type'],
  data() {
    return {
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
      // 消息查询表格数据
      receiver_records_list: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        receiver: null,
        sendMessageKey: null,
      },
      // 表单校验
      messageTemplate: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
        message: '正在实时监控接受者消息...',
        type: 'success',
        duration: 0
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

    showDetail(messageTemplate) {
      this.messageTemplate = []
      this.messageTemplate.push(messageTemplate)
      this.dialogVisible = true
    },

    /** 查询消息查询列表 */
    getList() {
      this.loading = true;
      list_receiver_records(this.queryParams).then(response => {
        this.receiver_records_list = response.rows;
        this.total = response.total;
        if (this.receiver_records_list.length <= 0) {
          this.$modal.msgSuccess("该用户今天尚未接受过消息哦");
        }
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

    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否清空删这天的所有数据？').then(() => {
        return delSend_task_info(this.queryParams.sendMessageKey);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    }
  }
}
;
</script>
