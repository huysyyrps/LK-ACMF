package com.example.lkacmf.util.linechart

import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import com.example.lk_epk.util.LogUtil
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
import com.github.mikephil.charting.listener.OnChartGestureListener
import kotlinx.android.synthetic.main.activity_main.*


class LineChartSetting {
    var oldScaleX = 0F
    var oldScaleY = 0F
    fun SettingLineChart(
        activity: MainActivity,
        linechar: LineChart,
        yAxixSetting: CardView,
        showX: Boolean,
    ){
        linechar.setDrawGridBackground(false)//是否显示表格颜色
        linechar.setDrawBorders(true)// 是否在折线图上添加边框
        linechar.setScaleEnabled(true)// 是否可以缩放
        linechar.setPinchZoom(false) // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        linechar.isScaleXEnabled = true  // X轴上的缩放,默认true
        linechar.isScaleYEnabled = true  // Y轴上的缩放,默认true
        linechar.isDragEnabled = true// 是否可以拖拽
//        linechar.setTouchEnabled(true) // 设置是否可以触摸
        linechar.description = null// 数据描述
        //设置是否可以通过双击屏幕放大图表。默认是true
        linechar.isDoubleTapToZoomEnabled = false;

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
//        linechar.setVisibleXRangeMaximum(300f);
//        linechar.moveViewToX(300f);
        linechar.onChartGestureListener = object : OnChartGestureListener {
            // 手势监听器
            override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartGesture) {
                // 按下
                LogUtil.e("TAG","按下")
                yAxixSetting.visibility = View.GONE
            }

            override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartGesture) {
                // 抬起,取消
            }

            override fun onChartLongPressed(me: MotionEvent) {
                // 长按
                LogUtil.e("TAG","长按")
                yAxixSetting.visibility = View.VISIBLE
            }

            override fun onChartDoubleTapped(me: MotionEvent) {
                // 双击
                LogUtil.e("TAG","双击")
            }

            override fun onChartSingleTapped(me: MotionEvent) {
                // 单击
            }

            override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
                // 甩动
                LogUtil.e("TAG","甩动")
            }

            override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {
                // 缩放
                when(linechar.id){
                    R.id.lineChartBX->{
                        if (oldScaleX!=scaleX||oldScaleY!=scaleY){
                            activity.lineChartBZ.fitScreen()
                            oldScaleX = scaleX
                            oldScaleY = scaleY
                            activity.lineChartBZ.viewPortHandler.matrixTouch.preScale(scaleX, scaleY)
                            activity.lineChartBZ.invalidate()
                        }
                    }
                    R.id.lineChartBZ->{
                        if (oldScaleX!=scaleX||oldScaleY!=scaleY){
                            activity.lineChartBX.fitScreen()
                            oldScaleX = scaleX
                            oldScaleY = scaleY
                            activity.lineChartBX.viewPortHandler.matrixTouch.preScale(scaleX, scaleY)
                            activity.lineChartBX.invalidate()
                        }
                    }
                }
            }

            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
                // 移动
                LogUtil.e("TAG","移动")
                LogUtil.e("TAG","$dX  $dY")
            }
        }



        //X轴
        val xAxis = linechar.xAxis
        xAxis.textColor = MyApplication.context.resources.getColor(R.color.theme_color)
        xAxis.gridColor = MyApplication.context.resources.getColor(R.color.back_color)
        xAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.black)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineWidth = 2f//轴线宽度
        xAxis.isEnabled = showX//是否显示X轴
        xAxis.granularity = 1F//设置 后 value是从0开始的，每次加1
        xAxis.setAvoidFirstLastClipping(true) //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘


        //左侧Y轴
        val leftYAxis = linechar.axisLeft
        leftYAxis.textColor = MyApplication.context.resources.getColor(R.color.red)
        leftYAxis.gridColor = MyApplication.context.resources.getColor(R.color.back_color)//网格颜色
        leftYAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.black)//轴线颜色
        leftYAxis.axisLineWidth = 4f//轴线宽度
        //leftYAxis.spaceBottom = 10f // 最小值距离底部比例。默认10，y轴独有
//        leftYAxis.spaceTop = 10f // 设置最大值到图标顶部的距离占所有数据范围的比例。默认10，y轴独有
//        leftYAxis.axisMinimum = -10.0f;
//        leftYAxis.axisMaximum = 50f//为此轴设置自定义最大值。
        leftYAxis.isEnabled = true;//是否显示
//        leftYAxis.granularity = 1.0f;//设置Y轴坐标之间的最小间隔（因为此图有缩放功能，X轴,Y轴可设置可缩放）
        leftYAxis.setDrawGridLines(false)
////        yLeftAxis.axisMinimum = -30f
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART); //Y轴标签显示位置


        //右侧Y轴
        val yRightAxis = linechar.axisRight
        yRightAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.black)//轴线颜色
        yRightAxis.setDrawLabels(false)//右侧轴线不显示标签
        yRightAxis.axisLineWidth = 4f//轴线宽度
    }
}