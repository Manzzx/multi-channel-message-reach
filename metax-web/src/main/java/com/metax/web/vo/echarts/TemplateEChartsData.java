package com.metax.web.vo.echarts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模板分析ECharts图数据
 *
 * @Author: hanabi
 * @DateTime: 2023/11/8 9:17
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateEChartsData {
    /**
     * 第一层版块所有数据
     */
    private ShowPanelData showPanelData;

    /**
     * 模板发送的用户情况柱状图数据
     */
    private ShowUserEncodeData showUserEncodeData;

    /**
     * 消息发送情况扇形图数据
     */
    private ShowPieChartData showPieChartData;

    /**
     * 当天六个时间段的发送情况
     */
    private ShowBarChartData showBarChartData;

    /**
     * 消息模板近七天的发送情况
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
         * 模板送达率
         */
        private double deliverAbility;
        /**
         * 今日模板推送占比
         */
        private double pushPercentage;
    }

    /**
     * 展示模板发送的用户情况数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowUserEncodeData {

        /**
         * 格式
         * ['score', 'amount', 'product'],
         * ['0', '58212', 'Matcha Latte'],
         * ['0', '78254', 'Milk Tea'],
         */
        private List<List<String>> source;
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
     * 当天六个时间段消息柱状图发送情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowBarChartData {

        /**
         * 柱状图X轴数据 时间段
         */
        private XAxis xAxis;

        /**
         * 柱状图Y轴数据 每一个消息的发送情况
         */
        private List<Series> series;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class XAxis {

            /**
             * 存放六个时间段数据
             */
            private List<String> data;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Series {

            /**
             * 发送状态
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
             * 该状态六个时间段的数据 按照时间顺序插入
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
