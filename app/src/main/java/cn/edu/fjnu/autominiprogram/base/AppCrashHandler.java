package cn.edu.fjnu.autominiprogram.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.io.File;
import java.io.FileOutputStream;
import cn.edu.fjnu.autominiprogram.activity.InitActivity;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.utils.CommonUtils;
import momo.cn.edu.fjnu.androidutils.data.CommonValues;
import momo.cn.edu.fjnu.androidutils.exception.BaseCrashHandler;
import momo.cn.edu.fjnu.androidutils.utils.ActivityExitUtils;

/**
 * Created by gaofei on 2018/4/2.
 * APK奔溃处理
 */

public class AppCrashHandler extends BaseCrashHandler{
    private static final String TAG = AppCrashHandler.class.getName();
    @Override
    public void handleException(String errorMsg) {

        Intent intent = new Intent(CommonValues.application, InitActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(
                CommonValues.application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //退出程序
        AlarmManager mgr = (AlarmManager)CommonValues.application.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000, restartIntent); // 1秒钟后重启应用
        //Log.i(TAG, "errorMsg:" + errorMsg);
        //StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.CRASH_MSG, errorMsg)
        CommonUtils.weriteLogToFile(errorMsg);
        try{
            File crashFile = CommonValues.application.getFileStreamPath(ConstData.CRASH_FILE_NAME);
            FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
            fileOutputStream.write(errorMsg.getBytes());
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        ActivityExitUtils.exitAllActivitys();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
