package com.bigdo.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bigdo.R;
import com.bigdo.base.BaseActivity;
import com.bigdo.base.view.NoscrollListView;
import com.bigdo.base.view.SyncHorizontalScrollView;
import com.bigdo.entity.CollData;
import com.bigdo.netty.NettyClient;
import com.bigdo.utils.DBUtils;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @作者： 唐京伟
 * @创建时间： 2022/12/7
 * @版本： 1.0
 * @描述： 主页面
 */
public class MainActivity extends BaseActivity {

    private EditText edittext_IP;
    private EditText edittext_port;
    private EditText db_name;
    private EditText db_user;
    private EditText db_pwd;

    private Button btn_client;
    private Button btn_db;
    private Button btn_led1;
    private Button btn_led2;
    private Button btn_led3;
    private Button btn_led4;
    //    private Button btn_displayData;
    private Button btn_ctlSmg;
    private Switch switch_datadisplay;

    public static Handler mClientHandler;
    private ExecutorService mThreadPool;
    int led_en1 = 0;
    int led_en2 = 0;
    int led_en3 = 0;
    int led_en4 = 0;
    int smg_en = 0;

    NettyClient nettyClient;

    // 状态变量
    boolean flag_connectServer = false;


    //************* 数据显示 *************
    private NoscrollListView mLeft;
    private LeftAdapter mLeftAdapter;

    private NoscrollListView mData;
    private DataAdapter mDataAdapter; //数据适配器

    private SyncHorizontalScrollView mHeaderHorizontal;
    private SyncHorizontalScrollView mDataHorizontal;

    private SuperButton btnChart;

    boolean displayPause = false;
    Timer dataDisplayTimer = new Timer();  //定时器
    List<CollData> mListData = new ArrayList<>();  //初始化数组
    List<CollData> collData;    //DHT11数据 列表
    int listMax = 10; //列表最大展示列数
    //***********************************


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        edittext_IP = findViewById(R.id.editText_IP);
        edittext_port = findViewById(R.id.editText_port);
        db_name = findViewById(R.id.db_name);
        db_user = findViewById(R.id.db_user);
        db_pwd = findViewById(R.id.db_pwd);
        btn_client = findViewById(R.id.btn_client);
        btn_db = findViewById(R.id.btn_db);
        btn_led1 = findViewById(R.id.btn_led1);
        btn_led2 = findViewById(R.id.btn_led2);
        btn_led3 = findViewById(R.id.btn_led3);
        btn_led4 = findViewById(R.id.btn_led4);
        //btn_displayData = findViewById(R.id.btn_displayData);
        btn_ctlSmg = findViewById(R.id.btn_smg);
        switch_datadisplay = findViewById(R.id.switch_datadisplay);

        setTabHost();
        initView();

        // 设置按钮Button监听器 buttonListener
        Button.OnClickListener buttonListener = new Button.OnClickListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_client:
                        Log.d("ipAndPort", "IP=>" + edittext_IP.getText().toString() + ":" + Integer.parseInt(edittext_port.getText().toString()));
                        start_client(edittext_IP.getText().toString(), Integer.parseInt(edittext_port.getText().toString()));
                        DBUtils.IP = edittext_IP.getText().toString();
                        break;
                    case R.id.btn_db:
                        DBUtils.IP = edittext_IP.getText().toString();
                        DBUtils.DB_NAME = db_name.getText().toString();
                        DBUtils.USER = db_user.getText().toString();
                        DBUtils.PASSWORD = db_pwd.getText().toString();
                        connect_dataBase();
                        break;
                    case R.id.btn_led1:
                        btn_led1();
                        break;
                    case R.id.btn_led2:
                        btn_led2();
                        break;
                    case R.id.btn_led3:
                        btn_led3();
                        break;
                    case R.id.btn_led4:
                        btn_led4();
                        break;
                    case R.id.btn_smg:
                        btn_smg();
                        break;
                }
            }
        };

        btn_client.setOnClickListener(buttonListener);
        btn_db.setOnClickListener(buttonListener);
        btn_led1.setOnClickListener(buttonListener);
        btn_led2.setOnClickListener(buttonListener);
        btn_led3.setOnClickListener(buttonListener);
        btn_led4.setOnClickListener(buttonListener);
        //btn_displayData.setOnClickListener(buttonListener);
        btn_ctlSmg.setOnClickListener(buttonListener);
        mThreadPool = Executors.newCachedThreadPool();

        // 数据显示开关监听
        switch_datadisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!DBUtils.flag_linkDB && switch_datadisplay.isChecked()) {
                    Toast.makeText(MainActivity.this, "请先连接数据库", Toast.LENGTH_SHORT).show();
                    switch_datadisplay.setChecked(false);
                } else {
                    if (switch_datadisplay.isChecked()) {
                        displayPause = true;
                        Toast.makeText(MainActivity.this, "开启", Toast.LENGTH_SHORT).show();
                    } else {
                        displayPause = false;
                        Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mClientHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(MainActivity.this, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                switch (msg.what) {
                    case 0:
//                        String[] split = msg.getData().getString("msg").split(" ");
//                        Log.d("wifi_data", String.valueOf(msg.getData().getString("msg")));
//                        textView2_temp.setText(split[0] + "℃");
//                        textView2_humi.setText(split[1] + "RH%");
                        break;
                    case 1:
//                        temp_str = textView_receive.getText().toString();
//                        textView_receive.setText(temp_str + "\r\n" + msg.getData().getString("msg"));
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // 测试、连接数据库
    private void connect_dataBase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != DBUtils.getConnection()) {   //连接数据库成功
                    btn_db.setEnabled(false);
                    btn_db.setText("连接数据库成功");
                    btn_db.setBackgroundColor(Color.RED);
                    Toast.makeText(MainActivity.this, "连接数据库成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "连接数据库失败", Toast.LENGTH_LONG).show();
                }
            }
        }).start();//启动线程
    }


    // Tabs的内容
    protected void setTabHost() {
        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("服务连接")
                .setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("控制中心")
                .setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("数据展示")
                .setContent(R.id.tab3));
    }

    //服务器操作-连接服务器
    void start_client(String ip, int port) {
        nettyClient = new NettyClient();
        try {
            if (nettyClient.startNetty(ip, port)) {
                Toast.makeText(this, "连接服务器成功！", Toast.LENGTH_SHORT).show();
                flag_connectServer = true;
                btn_client.setEnabled(false);
                btn_client.setText("已连接");
                btn_client.setBackgroundColor(Color.RED);
            } else {
                Toast.makeText(this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                flag_connectServer = false;
            }
        } catch (InterruptedException e) {
            Toast.makeText(this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // LED操作
    void btn_led1() {
        try {
            btn_send("0");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (led_en1 == 0) {
                btn_led1.setText("关灯");
                led_en1 = 1;
            } else if (led_en1 == 1) {
                btn_led1.setText("开灯");
                led_en1 = 0;
            }
        }
    }

    void btn_led2() {
        try {
            btn_send("1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (led_en2 == 0) {
                btn_led2.setText("关灯");
                led_en2 = 1;
            } else if (led_en2 == 1) {
                btn_led2.setText("开灯");
                led_en2 = 0;
            }
        }

    }

    void btn_led3() {
        try {
            btn_send("2");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (led_en3 == 0) {
                btn_led3.setText("关灯");
                led_en3 = 1;
            } else if (led_en3 == 1) {
                btn_led3.setText("开灯");
                led_en3 = 0;
            }
        }
    }

    void btn_led4() {
        try {
            btn_send("3");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (led_en4 == 0) {
                btn_led4.setText("关灯");
                led_en4 = 1;
            } else if (led_en4 == 1) {
                btn_led4.setText("开灯");
                led_en4 = 0;
            }
        }
    }

    // 控制数码管
    private void btn_smg() {
        try {
            btn_send("4");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (smg_en == 0) {
                btn_ctlSmg.setText("开");
                smg_en = 1;
            } else if (smg_en == 1) {
                btn_ctlSmg.setText("关");
                smg_en = 0;
            }
        }
    }

    //netty发送消息
    void btn_send(String text) {
        // 利用线程池直接开启一个线程 & 执行该线程
        nettyClient.clientSend("2," + text);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataDisplayTimer.purge();
        dataDisplayTimer.cancel();
    }

    private void initView() {
        btnChart = findViewById(R.id.btn_chart);  //图形展示按钮

//        mLeft = findViewById(R.id.lv_left);
        mData = findViewById(R.id.lv_data);

        mDataHorizontal = findViewById(R.id.data_horizontal);  //数据展示
        mHeaderHorizontal = findViewById(R.id.header_horizontal);  //头行展示

        // 设置 <可滚动> 样式
        mDataHorizontal.setScrollView(mHeaderHorizontal);
        mHeaderHorizontal.setScrollView(mDataHorizontal);

        initListen();
    }

    private void initListen() {
        btnChart.setOnClickListener(v -> {
            openNewPage(ChartsFragment.class);  //检测打开 “图形展示” 事件，跳转到 charts 页面
        });

        //启动就自动查询，每3秒查一次 -- period置为3000
        dataDisplayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (displayPause) {
                    new Thread(() -> {
                        collData = DBUtils.getCollData();//得到表里面的数据

                        mListData.clear();  //每次都清空数组
                        //算法实现获得数据库最后的五个数据，也就是最近采集到的5个数据
                        for (int i = 0; i < listMax; i++) {
                            assert collData != null;
                            CollData Data = collData.get(collData.size() - (listMax - i));//获得单个dht11数据对象
                            mListData.add(Data); //添加到数组中
                        }

                    }).start();

                    // 必须要 new Adapter(); 才能显示数据
                    runOnUiThread(() -> {
//                        mLeftAdapter = new LeftAdapter();
//                        mLeft.setAdapter(mLeftAdapter);

                        mDataAdapter = new DataAdapter();
                        mData.setAdapter(mDataAdapter);
                    });
                }

            }
        }, 1000, 3000);
    }

    class LeftAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LeftAdapter.ViewHolder holder;
            if (convertView == null) {
                holder = new LeftAdapter.ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_left, null);
                holder.tvLeft = convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (LeftAdapter.ViewHolder) convertView.getTag();
            }

            holder.tvLeft.setText("第" + (position + 1) + "条");

            return convertView;
        }

        class ViewHolder {
            TextView tvLeft;
        }
    }

    class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DataAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DataAdapter.ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_data, null);

                //将item_data组件转换成对象
                holder.tvData1 = convertView.findViewById(R.id.tv_data1);
                holder.tvData2 = convertView.findViewById(R.id.tv_data2);
                holder.tvData3 = convertView.findViewById(R.id.tv_data3);
                holder.tvData4 = convertView.findViewById(R.id.tv_data4);
                holder.tvData5 = convertView.findViewById(R.id.tv_data5);
                holder.linContent = convertView.findViewById(R.id.lin_content);

                convertView.setTag(holder);
            } else {
                holder = (DataAdapter.ViewHolder) convertView.getTag();
            }

            holder.tvData1.setText(String.valueOf(mListData.get(position).getId()));
            holder.tvData2.setText(mListData.get(position).getTemp());
            holder.tvData3.setText(mListData.get(position).getHum());
            holder.tvData4.setText(mListData.get(position).getDistance());
            holder.tvData5.setText(mListData.get(position).getLight());

            return convertView;
        }

        class ViewHolder {
            TextView tvData1;
            TextView tvData2;
            TextView tvData3;
            TextView tvData4;
            TextView tvData5;

            LinearLayout linContent;
        }
    }
}
