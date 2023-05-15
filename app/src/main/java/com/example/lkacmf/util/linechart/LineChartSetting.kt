package com.example.lkacmf.util.linechart

import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.view.BaseLineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis

class LineChartSetting {
    fun SettingLineChart(linechar: BaseLineChart){
        linechar.setDrawGridBackground(false)//是否显示表格颜色
        linechar.setDrawBorders(true)// 是否在折线图上添加边框
        linechar.setScaleEnabled(true)// 是否可以缩放
        linechar.setPinchZoom(false) // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
//        linechar.isDragEnabled = true// 是否可以拖拽
        linechar.isScaleXEnabled = true
        linechar.setTouchEnabled(false) // 设置是否可以触摸
        linechar.description = null// 数据描述
//        linechar.isDoubleTapToZoomEnabled = false
        linechar.viewPortHandler.setMaximumScaleX(30.0f)//限制X轴放大限制
        linechar.viewPortHandler.setMaximumScaleY(3.0f)
        linechar.legend.isEnabled = false;// 不显示图例
        linechar.setVisibleXRangeMaximum(80.0f)
        linechar.extraTopOffset = 5.0f
        linechar.extraLeftOffset = 0.0f
        linechar.extraRightOffset = 0.0f
        linechar.extraBottomOffset = 5.0f
        linechar.minOffset = 0.0f
        // 当前统计图表中最多在x轴坐标线上显示的总量
        linechar.setVisibleXRangeMaximum(300f);
        linechar.moveViewToX(300f);

        //X轴
        val xAxis = linechar.xAxis
        xAxis.textColor = MyApplication.context.resources.getColor(R.color.theme_color)
        xAxis.gridColor = MyApplication.context.resources.getColor(R.color.line)
        xAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.theme_color)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum(0.0f);
        xAxis.axisMaximum(80.0f);
//        xAxis.isEnabled = false//是否显示X轴
        xAxis.setAvoidFirstLastClipping(true) //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘

        //左侧Y轴
        val leftYAxis = linechar.axisLeft
        leftYAxis.textColor = MyApplication.context.resources.getColor(R.color.red)
        leftYAxis.gridColor = MyApplication.context.resources.getColor(R.color.line)//网格颜色
        leftYAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.theme_color)//轴线颜色
        //leftYAxis.spaceBottom = 10f // 最小值距离底部比例。默认10，y轴独有
//        leftYAxis.spaceTop = 10f // 设置最大值到图标顶部的距离占所有数据范围的比例。默认10，y轴独有
        leftYAxis.axisMinimum = 0.0f;
        leftYAxis.axisMaximum = 100f//为此轴设置自定义最大值。
        leftYAxis.isEnabled = true;//是否显示
        leftYAxis.granularity = 1.0f;//设置Y轴坐标之间的最小间隔（因为此图有缩放功能，X轴,Y轴可设置可缩放）
        leftYAxis.setDrawGridLines(false)
////        yLeftAxis.axisMinimum = -30f
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART); //Y轴标签显示位置


        //右侧Y轴
        val yRightAxis = linechar.axisRight
        yRightAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.theme_color)//轴线颜色
        yRightAxis.setDrawLabels(false)//右侧轴线不显示标签
    }
}

private operator fun Float.invoke(fl: Float) {

}