package com.metax.web.vo.echarts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页ECharts图数据
 *
 * @Author: hanabi
 * @DateTime: 2023/11/3 19:16
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexEChartsData {

    /**
     * 第一层版块所有数据
     */
    private ShowPanelData showPanelData;

    /**
     * 审核模板情况扇形图数据
     */
    private ShowPieChartAuditData showPieChartAuditData;

    /**
     * 消息发送情况扇形图数据
     */
    private ShowPieChartData showPieChartData;

    /**
     * 近七天消息渠道柱状图发送情况
     */
    private ShowBarChartData showBarChartData;

    /**
     * 消息发送情况线形图数据
     */
    private ShowLineChartData showLineChartData;

    /**
     * 第一层版块所有数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowPanelData {

        /**
         * 下发总人数
         */
        private Integer totalNum;
        /**
         * 今日下发人数
         */
        private Integer dayNum;
        /**
         * 模板数
         */
        private Integer temNum;
        /**
         * 已启动模板数
         */
        private Integer startNum;
    }

    /**
     * 审核模板情况扇形图数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowPieChartAuditData {

        /**
         * 通过审核模板
         */
        private Integer passAudit;
        /**
         * 未通过审核模板
         */
        private Integer failAudit;
        /**
         * 等待审核模板
         */
        private Integer waitAudit;
    }

    /**
     * 消息发送情况扇形图数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowPieChartData {

        /**
         * 当天发送成功数
         */
        private Integer success;
        /**
         * 当天发送中数
         */
        private Integer sending;
        /**
         * 当天发送失败数
         */
        private Integer fail;
    }

    /**
     * 近七天消息渠道柱状图发送情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowBarChartData {

        /**
         * 柱状图X轴数据
         */
        private XAxis xAxis;

        /**
         * 柱状图Y轴数据 每一个渠道的发送情况
         */
        private List<Series> series;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class XAxis {

            /**
             * 近七天日期
             */
            private List<String> data;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Series {

            /**
             * 渠道名称
             */
            private String name;
            /**
             * bar类型
             */
            private final String type = "bar";
            /**
             * stack
             */
            private final String stack = "vistors";
            /**
             * 宽
             */
            private final String barWidth = "60%";
            /**
             * 该渠道七天的数据 按照时间顺序插入
             */
            private List<Integer> data;
        }

    }

    /**
     * 消息发送情况线形图数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowLineChartData {

        /**
         * 线性图X轴数据
         */
        private XAxis xAxis;

        /**
         * 线性图Y轴数据 近七天发送情况
         */
        private Series series;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class XAxis {

            /**
             * 近七天日期
             */
            private List<String> data;

        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Series {

            /**
             * 近七天发送情况数据
             */
            private Data data;

            @lombok.Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Data {

                /**
                 * 近七天发送成功数 按照时间顺序插入
                 */
                private List<Integer> successList;
                /**
                 * 近七天发送中数 按照时间顺序插入
                 */
                private List<Integer> sendingList;
                /**
                 * 近七天发送失败数 按照时间顺序插入
                 */
                private List<Integer> failList;

            }
        }

    }

}
