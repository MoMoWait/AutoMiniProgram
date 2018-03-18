package cn.edu.fjnu.autominiprogram.hkh;

import android.content.SharedPreferences;

/**
 * Created by hkh on 18-3-10.
 */

public class Constant {
    public static final String click_checkbox = "多选";
    public static final String click_tran = "转发";
    public static final String click_send = "取消发送";
    public static final String click_swipe = "月售";
    public static final String click_chat = "最近聊天";

    public static final String []filter = {"开始校准", "系统设置", "自动启停", "定位坐标"};

    public static final String filePath_main = "/storage/emulated/0/main.png"; //主界面图片
    public static final String filePath_tran = "/storage/emulated/0/tran.png";//转发图片
    public static final String filePath_chat = "/storage/emulated/0/chat.png";//聊天界面
    public static final String filePath_send= "/storage/emulated/0/send.png";//发送界面


    public static boolean chat_status = false;
    public static boolean mContinue = false;

    public static String point_start_x = "point_start_x";
    public static String point_start_y = "point_start_y";
    public static String point_tran_x = "point_tran_x";
    public static String point_tran_y = "point_tran_y";
    public static String point_checkbox_x = "point_checkbox_x";
    public static String point_checkbox_y = "point_checkbox_y";
    public static String point_chat_x = "point_chat_x";
    public static String point_chat_y = "point_chat_y";
    public static String point_send_x = "point_send_x";
    public static String point_send_y = "point_send_y";
    public static String count_chat_1 = "count_chat";
    public static String interval_chat_1 = "interval_chat";


    //三个小点点的坐标
    public static Point point_start;

    //转发的坐标
    public static Point point_tran;

    //多选的坐标
    public static Point point_checkbox;

    //置顶聊天第一个坐标
    public static Point point_chat;
    //转发到几个置顶聊天中
    public static int count_chat = 3;
    //置顶聊天的间隔
    public static int interval_chat = -1;   //-1 初始值，-2,读取完第一个。

    //发送的坐标
    public static Point point_send;
}
