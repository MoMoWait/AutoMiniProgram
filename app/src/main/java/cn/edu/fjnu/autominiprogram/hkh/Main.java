package cn.edu.fjnu.autominiprogram.hkh;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hkh on 18-3-17.
 */

public class Main {
    private static final String TAG = "MainActivity";


    private boolean hasGotToken = false;
    private AlertDialog.Builder alertDialog;
    private Context mContext;
    private Activity mActivity;
    private List<Point> sale_point;

    public Main(Context context){
        Log.e(TAG, "start with Main");
        mContext = context;
        //mActivity = activity;
        init();
    }
    private void init(){
        initAccessTokenWithAkSk();
        ShellUtils.execCommand("ls", true);
        getData();
    }
    public void initRegularPosition(int x, int y){
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
        for(int i = 0; i < Constant.count_chat; i++){
            Point tmp = new Point(Constant.point_chat.m_x, Constant.point_chat.m_y+ i*Constant.interval_chat);
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
    public void sale(){
        ShellUtils.screencap(Constant.filePath_main);
        sale_point = new ArrayList<>();
        Constant.mContinue = false;
        Ocr.RecognizeText(serviceListener, Constant.filePath_main);
        while(!Constant.mContinue);
        try {
            Log.e(TAG,"识别月售之后休眠5S！！！");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Point point: sale_point){
            ShellUtils.click_point(point);
            ShellUtils.click_point(Constant.point_start);
            ShellUtils.click_point(Constant.point_tran);
            ShellUtils.click_point(Constant.point_checkbox);

            //选择聊天
            for(int i = 0; i < Constant.count_chat; i++){
                Point tmp = new Point(Constant.point_chat.m_x, Constant.point_chat.m_y+ i*Constant.interval_chat);
                Log.e(TAG, "point is x = " + String.valueOf(tmp.m_x) + " y = " +String.valueOf(tmp.m_y));
                ShellUtils.click_point(tmp);

            }

            ShellUtils.click_point(Constant.point_checkbox);
            ShellUtils.click_point(Constant.point_send);

            ShellUtils.click_back();

        }
        ShellUtils.swipe_top(sale_point.get(sale_point.size() - 1));
    }


    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
                Log.e(TAG, "AkSk success!!!");
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, mContext.getApplicationContext(), "iu76ZuXOqPuluG5GKWT95Zza", "ma8o0qpZG5BfZqKrFL1GTseiLDsWAWz2");
    }


    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(mContext.getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void alertText(final String title, final String message) {
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
        editor.putInt(Constant.point_start_x, Constant.point_start.m_x);
        editor.putInt(Constant.point_start_y, Constant.point_start.m_y);
        editor.putInt(Constant.point_tran_x, Constant.point_tran.m_x);
        editor.putInt(Constant.point_tran_y, Constant.point_tran.m_y);
        editor.putInt(Constant.count_chat_1, Constant.count_chat);
        editor.putInt(Constant.interval_chat_1, Constant.interval_chat);
        //4.提交
        editor.commit();
    }

    public boolean getData(){
        SharedPreferences userSettings= mContext.getSharedPreferences("userData", 0);
        Constant.count_chat = userSettings.getInt(Constant.count_chat_1, 3);

        Constant.interval_chat = userSettings.getInt(Constant.interval_chat_1, -1);
        Constant.point_chat = new Point(userSettings.getInt(Constant.point_chat_x, -1), userSettings.getInt(Constant.point_chat_y, -1));
        Constant.point_checkbox = new Point(userSettings.getInt(Constant.point_checkbox_x, -1), userSettings.getInt(Constant.point_checkbox_y, -1));
        Constant.point_send = new Point(userSettings.getInt(Constant.point_send_x, -1), userSettings.getInt(Constant.point_send_y, -1));
        Constant.point_start = new Point(userSettings.getInt(Constant.point_start_x, -1), userSettings.getInt(Constant.point_start_y, -1));
        Constant.point_tran = new Point(userSettings.getInt(Constant.point_tran_x, -1), userSettings.getInt(Constant.point_tran_y, -1));

        if(Constant.count_chat == 3){
            Log.e(TAG, "getData failed!!!");
            return false;
        }
        return true;
    }
    private Ocr.ServiceListener tranListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            if(name.contains(Constant.click_tran)){
                Constant.point_tran = new Point(location.getLeft(), location.getTop());
                Log.e(TAG, name + "---转发:Left = " + location.getLeft() + ",Top = "+location.getTop());
            }

        }
    };

    private Ocr.ServiceListener checkboxListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            for(String string:Constant.filter){
                if(name.contains(string)){
                    Log.e(TAG, "过滤!!!");
                    return;
                }
            }

            if(name.contains(Constant.click_checkbox)){
                Log.e(TAG, name + "---多选框:Left = " + location.getLeft() + ",Top = "+location.getTop());
                Constant.point_checkbox = new Point(location.getLeft(), location.getTop());
            }else if(name.contains(Constant.click_chat)){
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
                }
                Log.e(TAG, name + "---Left = " + location.getLeft() + ",Top = "+location.getTop());
            }
        }
    };

    private Ocr.ServiceListener sendListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            if(name.contains(Constant.click_send)){
                Constant.point_send = new Point(location.getLeft()+location.getWidth()/3*2, location.getTop());
                Log.e(TAG, name + "---发送:Left = " + Constant.point_send.m_x + ",Top = "+Constant.point_send.m_y);
            }
        }
    };

    private Ocr.ServiceListener serviceListener = new Ocr.ServiceListener() {
        @Override
        public void onResult(String name, Location location) {
            if(name.contains(Constant.click_swipe)){
                sale_point.add(new Point(location.getLeft(), location.getTop()));
                Log.e(TAG, "Left = " + location.getLeft() + ",Top = "+location.getTop());
            }

        }
    };
}
