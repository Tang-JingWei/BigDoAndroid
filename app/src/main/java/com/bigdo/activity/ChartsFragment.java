package com.bigdo.activity;


import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import com.bigdo.R;
import com.bigdo.base.webview.BaseWebViewFragment;
import com.bigdo.entity.CollData;
import com.bigdo.utils.DBUtils;
import com.bigdo.utils.Utils;
import com.github.abel533.echarts.DataZoom;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.DataZoomType;
import com.github.abel533.echarts.code.SeriesType;
import com.github.abel533.echarts.code.Symbol;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.just.agentweb.AgentWeb;
import com.xuexiang.xpage.annotation.Page;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


@Page(name = "图形")
public class ChartsFragment extends BaseWebViewFragment {

    FrameLayout flContainer1;
    FrameLayout flContainer2;
    FrameLayout flContainer3;

    ChartInterface mChartInterface;
    AgentWeb mAgentWeb1;
    AgentWeb mAgentWeb2;
    AgentWeb mAgentWeb3;

    //数据从数据库中读入到这些数组中，12345是初始化长度为5，为了让图表展示5个数据
    String[] time = new String[]{"1", "2", "3", "4", "5"};
    String[] temp = new String[]{"1", "2", "3", "4", "5"};
    String[] hum = new String[]{"1", "2", "3", "4", "5"};
    String[] dis = new String[]{"1", "2", "3", "4", "5"};

    Timer timer = new Timer(); //定时器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_line;
    }


    @Override
    protected void initViews() {
        flContainer1 = findViewById(R.id.fl_container_line1);
        flContainer2 = findViewById(R.id.fl_container_line2);
        flContainer3 = findViewById(R.id.fl_container_line3);

        //目前Echarts-Java只支持3.x
        mAgentWeb1 = Utils.createAgentWeb(this, flContainer1, "file:///android_asset/chart/src/template.html");
        mAgentWeb2 = Utils.createAgentWeb(this, flContainer2, "file:///android_asset/chart/src/template.html");
        mAgentWeb3 = Utils.createAgentWeb(this, flContainer3, "file:///android_asset/chart/src/template.html");

        //注入接口,供JS调用
        mAgentWeb1.getJsInterfaceHolder().addJavaObject("Android", mChartInterface = new ChartInterface());
        mAgentWeb2.getJsInterfaceHolder().addJavaObject("Android", mChartInterface = new ChartInterface());
        mAgentWeb3.getJsInterfaceHolder().addJavaObject("Android", mChartInterface = new ChartInterface());

        //这里需要延迟才能画图
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<CollData> collData;
                collData = DBUtils.getCollData(); //得到表里面的数据

                //算法实现获得数据库最后的五个数据，也就是最近采集到的5个数据
                for (int i = 0; i < 5; i++) {
                    temp[i] = collData.get(collData.size()-(5-i)).getTemp();
                    hum[i] = collData.get(collData.size()-(5-i)).getHum();
                    dis[i] = collData.get(collData.size()-(5-i)).getDistance();
                }

                initLineChart();
            }
        },1000,2000);

    }

    private void initLineChart() {
        mAgentWeb1.getJsAccessEntrace().quickCallJs("loadChartView", "chart", mChartInterface.makeLineChartOptions(time, temp));
        mAgentWeb2.getJsAccessEntrace().quickCallJs("loadChartView", "chart", mChartInterface.makeBarChartOptions(time, hum));
        mAgentWeb3.getJsAccessEntrace().quickCallJs("loadChartView", "chart", mChartInterface.makeDistanceBar(time, dis));
    }


    /**
     * 注入到JS里的对象接口
     */
    public static class ChartInterface {
        /**
         * @params xAxis x轴数据
         * @params y1Axis 第一个y轴数据数组
         */
        @NotNull
        @JavascriptInterface
        public String makeLineChartOptions(Object[] xAxis, Object[] y1Axis) {
            GsonOption option = new GsonOption();
            option.legend("温度");
            option.toolbox().show(false);
            option.calculable(true);
            ValueAxis valueAxis = new ValueAxis();
            option.yAxis(valueAxis);

            DataZoom dataZoom = new DataZoom();
            dataZoom.setType(DataZoomType.inside);
            option.dataZoom(dataZoom);


            CategoryAxis categorxAxis = new CategoryAxis();
            categorxAxis.axisLine().onZero(false);
            categorxAxis.axisLabel();
            categorxAxis.boundaryGap(false);
            categorxAxis.data(xAxis);//x轴数据
            categorxAxis.splitLine().show(false);//去掉网格线
            option.xAxis(categorxAxis);

            Line line = new Line();
            line.setSymbol(Symbol.none);
            line.smooth(true).name("温度").data(y1Axis).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");

            option.series(line);
            return option.toString();

        }

        @NotNull
        @JavascriptInterface
        public  String makeBarChartOptions(Object[] xAxis, Object[] yAxis) {
            GsonOption option = new GsonOption();
            option.legend("水质");
            DataZoom dataZoom  = new DataZoom();
            dataZoom.setType(DataZoomType.inside);
            option.dataZoom(dataZoom);


            CategoryAxis categoryAxis = new CategoryAxis();
            categoryAxis.setType(AxisType.category);
            categoryAxis.data(xAxis);
            option.xAxis(categoryAxis);

            CategoryAxis categoryAxis2 = new CategoryAxis();
            categoryAxis2.setType(AxisType.value);
            option.yAxis(categoryAxis2);

            Bar bar = new Bar();
            bar.data(yAxis).setType(SeriesType.bar);
            bar.setName("水质");
            option.series(bar);
            return option.toString();
        }

        @NotNull
        @JavascriptInterface
        public String makeDistanceBar(Object[] xAxis, Object[] y1Axis) {
            GsonOption option = new GsonOption();
            option.legend("超声波");
            option.toolbox().show(false);
            option.calculable(true);
            ValueAxis valueAxis = new ValueAxis();
            option.yAxis(valueAxis);

            DataZoom dataZoom = new DataZoom();
            dataZoom.setType(DataZoomType.inside);
            option.dataZoom(dataZoom);


            CategoryAxis categorxAxis = new CategoryAxis();
            categorxAxis.axisLine().onZero(false);
            categorxAxis.axisLabel();
            categorxAxis.boundaryGap(false);
            categorxAxis.data(xAxis);//x轴数据
            categorxAxis.splitLine().show(false);//去掉网格线
            option.xAxis(categorxAxis);

            Line line = new Line();
            line.setSymbol(Symbol.none);
            line.smooth(true).name("超声波").data(y1Axis).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");

            option.series(line);
            return option.toString();

        }
    }

    /**
     * @author XiaoTF
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
        timer.purge();
    }
}
