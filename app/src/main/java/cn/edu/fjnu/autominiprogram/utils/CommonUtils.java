package cn.edu.fjnu.autominiprogram.utils;

import java.io.File;
import java.io.FileOutputStream;

import cn.edu.fjnu.autominiprogram.data.Configs;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import momo.cn.edu.fjnu.androidutils.data.CommonValues;

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
            outputStream.write((message + "\r\n").getBytes());
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
