package cn.edu.fjnu.autominiprogram.data;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by GaoFei on 2018/1/27.
 * 加载网络请求接口
 */

public class ServiceManager {
    private static final String TAG = ServiceManager.class.getSimpleName();
    private static final ServiceManager manger =new ServiceManager();
    private UrlService mService;
    private ServiceManager(){
        init();
    }

    public static ServiceManager getInstance(){

        return manger;
    }

    private void init(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, message);
            }
        });
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Configs.BASE_URL).client(builder.build()).build();
        mService = retrofit.create(UrlService.class);
    }

    public UrlService getUrlService(){
        return mService;
    }
}
