# 多渠道消息推送平台

# 介绍
多渠道消息推送平台是一个为应用开发者提供服务的平台，旨在解决发送消息的需求。

# 软件架构
api模块：系统接口

common模块：通用模块

gateway模块：网关

modules模块：系统基础功能模块

ui模块：前端

visual模块：系统监控

web模块：消息推送功能模块

# 项目特性
1. 引入动态可监控线程池来处理各渠道消息发送任务，提高消息发送任务并发量和消息任务处理速度
2. 使用Nacos管理项目各微服务实例和服务配置，并且可动态管理线程池参数，提升系统灵活性
3. 使用Redis缓存消息发送记录，提高消息记录的查询速度
4. 引入Xxl-job定时启动定时消息任务，实现消息的定时发送
5. 引入消息中间件RabbitMQ，在用户进行实时消息发送任务或定时消息任务启动后，将发送任务交给RabbitMQ监听消费，实现消息发送异步解耦，降低系统耦合度
6. 使用Docker统一部署各组件，降低系统部署困难度
7. 使用RabbitMQ实现延迟队列，处理超时消息任务，提高消息可靠性
8. 对接多个第三方消息服务API比如邮件、短信、钉钉、APP通知栏、微信公众号、飞书机器人
9. 使用Redis实现消息的链路追踪，对消息的各个阶段进行实时监控，掌控消息的生命周期
10. 使用Mysql存储消息发送模板信息和第三方账号配置信息
11. 通过使用ECharts，可以对消息模板下发用户数、今日消息送达率、每天各时间段发送情况以及消息模板用户等数据进行可视化展示。这样可以方便地进行消息模板的数据分析
12. 使用Sentinel对消息发送接口限流


# 安装教程
#### 运行必需服务
Redis、RabbitMQ、Xxl-job、Nacos、Mysql

#### 运行非必需服务
Sentinel、SpringbootAdmin（visual模块）

#### 后端

1. docker安装redis、rabbitMQ、mysql、nacos

2. 需要给调用xxl-job的接口加上@PermissionLimit(limit=false)开放权限

3. 修改各服务组件地址,各服务的bootstrap.yml文件和Nacos配置文件

#### 前端
#### 进入项目目录
1. cd metax-ui

#### 安装依赖
2. npm install --registry=https://registry.npmmirror.com

#### 启动服务
3. npm run dev

#### 使用说明
1. 管理员账号密码：admin admin123

2. 配置个人渠道账号

3. 新增渠道消息模板

# 演示图

 

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
