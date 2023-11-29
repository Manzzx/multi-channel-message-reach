<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="账号" prop="account">
        <el-input
          v-model="queryParams.account"
          placeholder="发送账号id 必填"
          clearable
          @keyup.enter.native="handleQuery"
          style="width: 190px"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="接受者手机号 必填"
          clearable
          @keyup.enter.native="handleQuery"
          style="width: 180px"
        />
      </el-form-item>

      <el-form-item label="发送时间" prop="sendDate">
        <el-date-picker clearable
                        v-model="queryParams.sendDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="必填"
                        style="width: 180px"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="流水号" prop="serialId">
        <el-input
          v-model="queryParams.serialId"
          placeholder="短信流水号 选填"
          clearable
          @keyup.enter.native="handleQuery"
          style="width: 180px"
        />
      </el-form-item>

      <el-form-item style="margin-top:0px">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">拉取</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="danger"
            icon="el-icon-delete"
            size="mini"
            @click="handleDelete"
            v-hasPermi="['web:send_task_info:remove']"
          >清空近期拉取
          </el-button>
        </el-col>
      </el-row>

    </el-form>


    <el-table v-loading="loading" :data="sms_recordsList">
      <el-table-column label="手机号" align="center" prop="phone"/>
      <el-table-column label="发送渠道" align="center" prop="channelName"/>
      <el-table-column label="模板名称" align="center" prop="template">
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
      <el-table-column label="发送时间" align="center" prop="sendDate" min-width="110%">
        <template slot-scope="scope">
          <div v-if="scope.row.sendDate">
            <span>{{ parseTime(scope.row.sendDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </div>
          <div v-if="!scope.row.sendDate">
            <span>无返回结果</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="用户接受时间" align="center" prop="receiveDate" min-width="110%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receiveDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="拉取时间" align="center" prop="queryDate" min-width="110%">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.queryDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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
      @pagination="getRecentRecords"
    />

    <!-- 更多详情弹窗-->
    <el-dialog
      title="发送详情"
      :visible.sync="dialogVisible"
      width="80%">
      <el-table :data="smsRecords">
        <el-table-column label="请求id" align="center" prop="requestId"/>
        <el-table-column label="流水号" align="center" prop="serialId"/>
        <el-table-column label="短信内容" align="center" prop="content"/>
        <el-table-column label="日志信息" align="center" prop="log"/>
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
  clearRecent,
  push_recent_sms_records,
  push_sms_records,
} from "@/api/web/sms_records";

export default {
  name: "sms_records",
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
      sms_recordsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        account: null,
        phone: null,
        sendDate: null,
        serialId: null,

      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},
      smsRecords: []
    };
  },
  created() {
    this.getRecentRecords();
  },
  methods: {
    getRecentRecords() {
      this.loading = true;
      push_recent_sms_records(this.queryParams).then(response => {
        this.sms_recordsList = response.rows;
        this.total = response.total;
        if (response.total === 0) {
          this.$message({
            message: '近30天还没有查过短信回执哦!',
            type: 'success'
          });
        }
        this.loading = false;
      });
    },
    showDetail(smsRecords) {
      this.smsRecords = []
      this.smsRecords.push(smsRecords)
      this.dialogVisible = true
    },

    /** 查询定时模板链路追踪列表 */
    getList() {
      this.loading = true;
      push_sms_records(this.queryParams).then(response => {
        this.sms_recordsList = response.rows;
        this.total = response.total;
        if (response.total === 0) {
          this.$message({
            message: '没有查询到该短信回执!',
            type: 'success'
          });
        }else {
          this.$message({
            message: '查询到 '+response.total+' 条短信回执!',
            type: 'success'
          });
        }
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.getRecentRecords();
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否清空近30天的所有数据？').then(() => {
        return clearRecent();
      }).then(() => {
        this.getRecentRecords();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    }

  }
};
</script>
