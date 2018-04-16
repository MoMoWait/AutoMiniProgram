package cn.edu.fjnu.autominiprogram.data;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

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

    @POST("usermanage/userlogin")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> loigin(@Body RequestBody body);

    @POST("usermanage/resacc")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> register(@Body RequestBody body);

    @POST("usermanage/getCode")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> getCode(@Body RequestBody body);

    @POST("usermanage/checkCode")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> checkCode(@Body RequestBody body, @Header("Cookie") String sessionID);

    @POST("usermanage/changepwd")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> changePassword(@Body RequestBody body);

    @POST("usermanage/resetpwd")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> resetPassword(@Body RequestBody body);

    @POST("usermanage/tixianApply")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> requestMoney(@Body RequestBody body);

    @POST("userinfo/showSystemMsg")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> showSystemMsg(@Body RequestBody body);

    @POST("userinfo/getMySpreaderList")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> getRecommendList(@Body RequestBody body);

    @POST("suggestion/insertSuggestion")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> insertSuggestion(@Body RequestBody body);

    @POST("userinfo/getSuggestionListByUserId")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> getSuggestionReplyList(@Body RequestBody body);

    @POST("userinfo/insertAbnormal")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> uploadLogContent(@Body RequestBody body);

    @Multipart
    @POST("abnormal/upload")
    Call<ResponseBody> uploadLogFile(@Part MultipartBody.Part file);

    @GET("manager/ downloadApps")
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> getAppVersion();


    @GET
    @Headers({"Content-Type:application/json;charset=UTF-8"})
    Call<ResponseBody> downloadNewVersion(@Url String url);

}
