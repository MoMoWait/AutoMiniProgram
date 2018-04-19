package cn.edu.fjnu.autominiprogram.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.service.FloatingwindowService;
import momo.cn.edu.fjnu.androidutils.data.CommonValues;
import momo.cn.edu.fjnu.androidutils.utils.ActivityExitUtils;

/**
 * Created by gaofei on 2018/3/31.
 * 通用工具
 */

public class CommonUtils {
    private CommonUtils(){

    }

    public static void weriteLogToFile(String message){
        try{
            File logFile = CommonValues.application.getFileStreamPath(ConstData.COMMON_LOG_FILE_NAME);
            if(logFile.exists() && logFile.length() >= (long)1024 * 1024){
                logFile.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(logFile, true);
            outputStream.write(( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()) + ":" + message + "\r\n").getBytes());
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     *  重启悬浮框服务
     */
    public static void restartFloatingWindowService(){
        Intent serviceIntent = new Intent(CommonValues.application, FloatingwindowService.class);
        PendingIntent restartServiceIntent =  PendingIntent.getService(
                CommonValues.application, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)CommonValues.application.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartServiceIntent);
        ActivityExitUtils.exitAllActivitys();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
