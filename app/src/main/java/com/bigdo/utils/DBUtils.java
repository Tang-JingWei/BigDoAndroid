package com.bigdo.utils;

import android.util.Log;
import com.bigdo.entity.CollData;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author 唐京伟
 */

public class DBUtils {
    private static final String TAG = "DBUtils";
    public static String DB_NAME = ""; //我的数据库名称为 test
    public static String IP = "";
    public static String USER = "";
    public static String PASSWORD ="";
    public static boolean flag_linkDB = false; //连接数据库标志位

    /**
     * @author 唐京伟
     * mysql驱动类加载与测试连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(!("".equals(IP)) && !("".equals(USER)) && !("".equals(PASSWORD))){
            try {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + IP + ":3306/" + DB_NAME + "?serverTimezone=Asia/Shanghai&useSSL=false",
                        USER, PASSWORD);
                Log.d(TAG, "数据库连接成功");
                flag_linkDB = true;
            } catch (SQLException ex) {
                Log.d(TAG, "数据库连接失败" + ex);
                flag_linkDB = false;
                ex.printStackTrace();
            }
        }
        return conn;
    }

    /**
     * @author 唐京伟
     * mysql查询操作（查询所有）
     */
    public static ArrayList<CollData> getCollData() {
        ArrayList<CollData> list = new ArrayList<>();
        Connection conn = getConnection();
        try {
            //sql语句
            String sql = "SELECT * FROM alldata ";
            // 创建用来执行sql语句的对象
            Statement st = conn.createStatement();
            // 执行sql查询语句并获取查询信息
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                while (res.next()) {
                    CollData collData = new CollData();
                    collData.setId(res.getLong("id"));
                    collData.setTemp(res.getString("temp"));
                    collData.setHum(res.getString("hum"));
                    collData.setDistance(res.getString("distance"));
                    collData.setLight(res.getString("light"));

                    list.add(collData);
                }
                //关闭数据库
                conn.close();
                st.close();
                res.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 查询数据操作异常");
            return null;
        }
    }
}
