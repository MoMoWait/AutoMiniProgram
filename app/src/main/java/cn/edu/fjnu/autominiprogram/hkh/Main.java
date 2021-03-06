package cn.edu.fjnu.autominiprogram.hkh;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.utils.CommonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;

/**
 * Created by hkh on 18-3-17.
 */

public class Main {
    private static final String TAG = "HKH_Main";


    private boolean hasGotToken = false;
    private AlertDialog.Builder alertDialog;
    private Context mContext;
    private Activity mActivity;
    private List<Point> sale_point;

    public Main(Context context){
        Log.e(TAG, "start with Main");
        CommonUtils.weriteLogToFile("start with Main");
        mContext = context;
        //mActivity = activity;
        init();
    }
    private void init(){
        initAccessTokenWithAkSk();
        ShellUtils.execCommand("ls", true);
        //getData();
    }
    public void initRegularPosition(int x, int y){
        Log.i(TAG, "initRegularPosition->x:" + x);
        Log.i(TAG, "initRegularPosition->y:" + y);
        CommonUtils.weriteLogToFile("initRegularPosition->x:" + x);
        CommonUtils.weriteLogToFile("initRegularPosition->y:" + y);
        Point point = new Point(x, y);
        ShellUtils.click_point(point);
        Constant.point_start = point;
        //点击三个小点点
        //ShellUtils.click_point(Constant.point_start);
        //截转发图片,并保存为
        ShellUtils.screencap(Constant.filePath_tran);
        //通过ocr获取转发的 x,y坐标
        Constant.mContinue = false;
        Ocr.RecognizeText(tranListener, Constant.filePath_tran);
        while(!Constant.mContinue);

        //while(!Constant.mContinue);
        //通过获得的x,y坐标 点击转发
        ShellUtils.click_point(Constant.point_tran);

        //截聊天图片, 并保存为
        ShellUtils.screencap(Constant.filePath_chat);
        //通过ocr获取多选按钮，要发送的置顶聊天位置 x,y坐标
        Constant.mContinue = false;
        Ocr.RecognizeText(checkboxListener, Constant.filePath_chat);
        while(!Constant.mContinue);

        //点击多选
        ShellUtils.click_point(Constant.point_checkbox);
        //选择聊天
        if(StorageUtils.getDataFromSharedPreference(Constant.count_chat_1).isEmpty()){
            Constant.count_chat = 2;
        }else{
            Constant.count_chat = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.count_chat_1));
        }
        for(int i = 0; i < Constant.count_chat; i++){
            Point tmp = new Point(Constant.point_chat.m_x, Constant.point_chat.m_y+ i*Constant.interval_chat);
            CommonUtils.weriteLogToFile("point is x = " + String.valueOf(tmp.m_x) + " y = " +String.valueOf(tmp.m_y));
            Log.e(TAG, "point is x = " + String.valueOf(tmp.m_x) + " y = " +String.valueOf(tmp.m_y));
            ShellUtils.click_point(tmp);

        }
        //点击多选
        ShellUtils.click_point(Constant.point_checkbox);

        //截图发送图片
        ShellUtils.screencap(Constant.filePath_send);

        //初始化发送
        Constant.mContinue = false;
        Ocr.RecognizeText(sendListener, Constant.filePath_send);
        while(!Constant.mContinue);
        ShellUtils.click_point(Constant.point_send);

        //将获取的点存储到数据库里面去
        saveData();
    }
    public void start_now(){
        //如果没有设置，默认为直接启动
        Constant.exit_thread = false;

        if(Constant.start_now.isEmpty()){
            return ;
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");

        String time= sDateFormat.format(new Date());

        while(!time.equals(Constant.start_now)){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean stop_now(){
        if(Constant.exit_thread){
            //退出线程
            return true;
        }
        //如果没有设置，默认为没有停止
        if(Constant.stop_now.isEmpty()){
            return false;
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");

        String time= sDateFormat.format(new Date());

        return time.equals(Constant.stop_now);

    }
    public void sale(){
        Log.e(TAG,"start with sale!!!");
        CommonUtils.weriteLogToFile("start with sale!!!");
        ShellUtils.screencap(Constant.filePath_main);
        sale_point = new ArrayList<>();
        Constant.mContinue = false;
        Log.e(TAG,"start with sale --- ocr!!!");
        CommonUtils.weriteLogToFile("start with sale ---- ocr!!!");
        Ocr.RecognizeText(serviceListener, Constant.filePath_main);
        while(!Constant.mContinue);
        Log.e(TAG,"end with sale --- ocr!!!");
        CommonUtils.weriteLogToFile("end with sale ---- ocr!!!");
        try {
            Log.e(TAG,"识别月售之后休眠5S！！！");
            CommonUtils.weriteLogToFile("识别月售之后休眠5S！！！");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Point point: sale_point){
            if(stop_now()){
                return ;
            }
            ShellUtils.click_point(point);
            Log.e(TAG,"end with click_point point_table !!!");
            CommonUtils.weriteLogToFile("end with click_point point_table!!!");
            try {
                int tmp_time;
                if(Constant.is_send_tween_random){
                    Random r = new Random();
                    tmp_time = r.nextInt(15)*1000*60;
                }else{
                    tmp_time = Constant.send_tween_time*1000*60;
                }
                Log.e(TAG, "间隔时间为" + tmp_time +" ms");
                CommonUtils.weriteLogToFile("间隔时间为" + tmp_time +" ms");
                Thread.sleep(tmp_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ShellUtils.click_point(Constant.point_start);
            Log.e(TAG,"end with click_point point_start !!!");
            CommonUtils.weriteLogToFile("end with click_point point_start!!!");
            ShellUtils.click_point(Constant.point_tran);
            Log.e(TAG,"end with click_point point_tran !!!");
            CommonUtils.weriteLogToFile("end with click_point point_tran!!!");
            ShellUtils.click_point(Constant.point_checkbox);
            Log.e(TAG,"end with click_point point_checkbox 11111 !!!");
            CommonUtils.weriteLogToFile("end with click_point point_checkbox 1111!!!");
            if(StorageUtils.getDataFromSharedPreference(Constant.count_chat_1).isEmpty()){
                Constant.count_chat = 2;
            }else{
                Constant.count_chat = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.count_chat_1));
            }
            //选择聊天
            for(int i = 0; i < Constant.count_chat; i++){
                Point tmp = new Point(Constant.point_chat.m_x, Constant.point_chat.m_y+ i*Constant.interval_chat);
                Log.e(TAG, "point is x = " + String.valueOf(tmp.m_x) + " y = " +String.valueOf(tmp.m_y));
                CommonUtils.weriteLogToFile("point is x = " + String.valueOf(tmp.m_x) + " y = " +String.valueOf(tmp.m_y));
                ShellUtils.click_point(tmp);

            }
            Log.e(TAG,"end with click_point point_chat!!!");
            CommonUtils.weriteLogToFile("end with click_point point_chat!!!");

            ShellUtils.click_point(Constant.point_checkbox);
            Log.e(TAG,"end with click_point point_checkbox 2222!!!");
            CommonUtils.weriteLogToFile("end with click_point point_checkbox 2222!!!");

            //发送问候语句
            /*
            SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
            String time = sDateFormat.format(new Date());
            if(time.equals(Constant.hello_time)){
                Log.e(TAG,"问候语句触发，内容是：" + Constant.hello_content);
                CommonUtils.weriteLogToFile("问候语句触发，内容是：" + Constant.hello_content);
                ShellUtils.inputtext(Constant.hello_content);
            }*/
            ShellUtils.click_point(Constant.point_send);
            Log.e(TAG,"end with click_point point_send!!!");
            CommonUtils.weriteLogToFile("end with click_point point_send!!!");

            ShellUtils.click_back();
            Log.e(TAG,"end with click_back!!!");
            CommonUtils.weriteLogToFile("end with click_back!!!");

        }
        if(!sale_point.isEmpty())
            ShellUtils.swipe_top(sale_point.get(sale_point.size() - 1));
        else{
            Log.e(TAG, "Error here Ocr get failed.");
            CommonUtils.weriteLogToFile("Error here Ocr get failed.");
        }

        Log.e(TAG,"end with sale!!!");
        CommonUtils.weriteLogToFile("end with sale!!!");
    }


    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
                Log.e(TAG, "AkSk success!!!");
                CommonUtils.weriteLogToFile("AkSk success!!!");
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, mContext.getApplicationContext(), "hWhRxR3T0tl4ulhpCI6qzOdS", "ie7j8mganSmgxc0d0m8A9lKDIGGdvgve");
    }


    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(mContext.getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void alertText(final String title, final String message) {
        CommonUtils.weriteLogToFile(message);
        Log.e(TAG, message);
/*        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });*/
    }

    public void saveData(){
        StorageUtils.saveDataToSharedPreference(Constant.point_chat_x, String.valueOf(Constant.point_chat.m_x));
        StorageUtils.saveDataToSharedPreference(Constant.point_chat_y, String.valueOf(Constant.point_chat.m_y));
        StorageUtils.saveDataToSharedPreference(Constant.point_checkbox_x, String.valueOf(Constant.point_checkbox.m_x));
        StorageUtils.saveDataToSharedPreference(Constant.point_checkbox_y, String.valueOf(Constant.point_checkbox.m_y));
        StorageUtils.saveDataToSharedPreference(Constant.point_send_x, String.valueOf(Constant.point_send.m_x));
        StorageUtils.saveDataToSharedPreference(Constant.point_send_y, String.valueOf(Constant.point_send.m_y));
        StorageUtils.saveDataToSharedPreference(Constant.point_tran_x, String.valueOf(Constant.point_tran.m_x));
        StorageUtils.saveDataToSharedPreference(Constant.point_tran_y, String.valueOf(Constant.point_tran.m_y));
        StorageUtils.saveDataToSharedPreference(Constant.interval_chat_1, String.valueOf(Constant.interval_chat));

        /*
        //1.打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
        SharedPreferences userSettings = mContext.getSharedPreferences("userData", 0);
        //2.让setting处于编辑状态
        SharedPreferences.Editor editor = userSettings.edit();
        //3.存放数据
        editor.putInt(Constant.point_chat_x, Constant.point_chat.m_x);
        editor.putInt(Constant.point_chat_y, Constant.point_chat.m_y);
        editor.putInt(Constant.point_checkbox_x, Constant.point_checkbox.m_x);
        editor.putInt(Constant.point_checkbox_y, Constant.point_checkbox.m_y);
        editor.putInt(Constant.point_send_x, Constant.point_send.m_x);
        editor.putInt(Constant.point_send_y, Constant.point_send.m_y);
        //editor.putInt(Constant.point_start_x, Constant.point_start.m_x);
        //editor.putInt(Constant.point_start_y, Constant.point_start.m_y);
        editor.putInt(Constant.point_tran_x, Constant.point_tran.m_x);
        editor.putInt(Constant.point_tran_y, Constant.point_tran.m_y);
        editor.putInt(Constant.count_chat_1, Constant.count_chat);
        editor.putInt(Constant.interval_chat_1, Constant.interval_chat);
        //4.提交
        editor.commit();
        */
    }

    public boolean getData(){
        //获取启动时间
        Constant.start_now = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.AUTO_SEND_START_TIME);
        //获取结束时间
        Constant.stop_now = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.AUTO_SEND_END_TIME);

        //获取发单间隔
        if(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.SEND_TWEEN_TIME).isEmpty()){
            //如果没有获取到值得，默认设置为15分钟
            Log.e(TAG,"间隔默认15分钟");
            CommonUtils.weriteLogToFile("间隔默认15分钟");
            Constant.send_tween_time = 15;
        }else{
            Constant.send_tween_time = Integer.parseInt(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.SEND_TWEEN_TIME));
            Log.e(TAG, "间隔为 " + Constant.send_tween_time);
            CommonUtils.weriteLogToFile("间隔为 " + Constant.send_tween_time);
        }

        //获取问候语句和时间
        if(!StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.HELLO_CONTENT).isEmpty()){
            Constant.hello_content = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.HELLO_CONTENT);
        }
        if(!StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.HELLO_TIME).isEmpty()){
            Constant.hello_time = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.HELLO_TIME);
        }

        //获取是否随机转发
        if(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.IS_SEND_TWEEN_RANDOM).equals("true")){
            Constant.is_send_tween_random = true;
        }

        ///////飞飞，看这里，Constant.count_chat_1  读出来的值是初始化的值，不是更新后的
        if(StorageUtils.getDataFromSharedPreference(Constant.count_chat_1).isEmpty()){
            Constant.count_chat = 2;
        }else{
            Constant.count_chat = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.count_chat_1));
        }
        Log.i(TAG, "Constant.count_chat:" + Constant.count_chat);
        CommonUtils.weriteLogToFile("Constant.count_chat:" + Constant.count_chat);
        Constant.interval_chat = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.interval_chat_1));
        int tmp_x, tmp_y;
        tmp_x = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_chat_x));
        tmp_y = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_chat_y));
        Constant.point_chat = new Point(tmp_x, tmp_y);
        tmp_x = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_checkbox_x));
        tmp_y = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_checkbox_y));
        Constant.point_checkbox = new Point(tmp_x, tmp_y);
        tmp_x = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_send_x));
        tmp_y = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_send_y));
        Constant.point_send = new Point(tmp_x, tmp_y);
        tmp_x = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_start_x));
        tmp_y = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_start_y));
        Constant.point_start = new Point(tmp_x, tmp_y);
        tmp_x = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_tran_x));
        tmp_y = Integer.parseInt(StorageUtils.getDataFromSharedPreference(Constant.point_tran_y));
        Constant.point_tran = new Point(tmp_x, tmp_y);
        /*
        SharedPreferences userSettings= mContext.getSharedPreferences("userData", 0);
        Constant.count_chat = userSettings.getInt(Constant.count_chat_1, 3);

        Constant.interval_chat = userSettings.getInt(Constant.interval_chat_1, -1);
        Constant.point_chat = new Point(userSettings.getInt(Constant.point_chat_x, -1), userSettings.getInt(Constant.point_chat_y, -1));
        Constant.point_checkbox = new Point(userSettings.getInt(Constant.point_checkbox_x, -1), userSettings.getInt(Constant.point_checkbox_y, -1));
        Constant.point_send = new Point(userSettings.getInt(Constant.point_send_x, -1), userSettings.getInt(Constant.point_send_y, -1));
        Constant.point_start = new Point(userSettings.getInt(Constant.point_start_x, -1), userSettings.getInt(Constant.point_start_y, -1));
        Constant.point_tran = new Point(userSettings.getInt(Constant.point_tran_x, -1), userSettings.getInt(Constant.point_tran_y, -1));
        */

        return true;
    }
    private Ocr.ServiceListener tranListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            if(name.contains(Constant.click_tran)){
                Constant.point_tran = new Point(location.getLeft(), location.getTop());
                CommonUtils.weriteLogToFile(name + "---转发:Left = " + location.getLeft() + ",Top = "+location.getTop());
                Log.e(TAG, name + "---转发:Left = " + location.getLeft() + ",Top = "+location.getTop());
            }

        }

        @Override
        public void onError(OCRError error) {

        }
    };

    private Ocr.ServiceListener checkboxListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            for(String string:Constant.filter){
                if(name.contains(string)){
                    Log.e(TAG, "过滤!!!");
                    CommonUtils.weriteLogToFile("过滤!!!");
                    return;
                }
            }

            if(name.contains(Constant.click_checkbox)){
                Log.e(TAG, name + "---多选框:Left = " + location.getLeft() + ",Top = "+location.getTop());
                CommonUtils.weriteLogToFile(name + "---多选框:Left = " + location.getLeft() + ",Top = "+location.getTop());
                Constant.point_checkbox = new Point(location.getLeft(), location.getTop());
            }else if(name.contains(Constant.click_chat)){
                Log.e(TAG, name);
                Constant.chat_status = true;
                return ;
            }
            if(Constant.chat_status){
                if(Constant.interval_chat == -1){
                    Constant.point_chat = new Point(location.getLeft(), location.getTop());
                    Constant.interval_chat = -2;
                }else if(Constant.interval_chat == -2){
                    Constant.interval_chat = location.getTop() - Constant.point_chat.m_y;
                    Log.e(TAG, "interval_chat = " +String.valueOf(Constant.interval_chat));
                    CommonUtils.weriteLogToFile("interval_chat = " +String.valueOf(Constant.interval_chat));
                }
                Log.e(TAG, name + "---Left = " + location.getLeft() + ",Top = "+location.getTop());
                CommonUtils.weriteLogToFile(name + "---Left = " + location.getLeft() + ",Top = "+location.getTop());
            }
        }

        @Override
        public void onError(OCRError error) {

        }
    };

    private Ocr.ServiceListener sendListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            if(name.contains(Constant.click_send)){
                Constant.point_send = new Point(location.getLeft()+location.getWidth()/3*2, location.getTop());
                Log.e(TAG, name + "---发送:Left = " + Constant.point_send.m_x + ",Top = "+Constant.point_send.m_y);
                CommonUtils.weriteLogToFile(name + "---发送:Left = " + Constant.point_send.m_x + ",Top = "+Constant.point_send.m_y);
            }
        }

        @Override
        public void onError(OCRError error) {

        }
    };

    private Ocr.ServiceListener serviceListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            if(name.contains(Constant.click_swipe)){
                sale_point.add(new Point(location.getLeft(), location.getTop()));
                Log.e(TAG, "Left = " + location.getLeft() + ",Top = "+location.getTop());
                CommonUtils.weriteLogToFile("Left = " + location.getLeft() + ",Top = "+location.getTop());
            }

        }

        @Override
        public void onError(OCRError error) {

        }
    };
}
