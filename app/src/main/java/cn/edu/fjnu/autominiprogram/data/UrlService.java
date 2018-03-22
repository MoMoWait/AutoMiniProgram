package cn.edu.fjnu.autominiprogram.data;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by GaoFei on 2018/1/27.
 * 定义网络请求接口
 */

public interface UrlService {
    @GET("app-diyicaipiao-release.apk")
    @Streaming
    Call<ResponseBody> downloadApkFile();

    @GET("com.qihoo360.mobilesafe_258.apk")
    @Streaming
    Call<ResponseBody> downloadOtherFile();

    @POST("manager/login")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> loigin(@Body RequestBody body);


}
