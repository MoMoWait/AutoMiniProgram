package cn.edu.fjnu.autominiprogram.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import momo.cn.edu.fjnu.androidutils.utils.PackageUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017\9\14 0014.
 * 用户注册异步块
 */

public class RegisterUserTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = RegisterUserTask.class.getSimpleName();
    public interface Callback{
        void onResult(int status);
    }

    private Callback mCallback;

    public RegisterUserTask(){

    }

    public RegisterUserTask(Callback callback){
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String userName = strings[0];
        String passwd = strings[1];
        String nickName = strings[2];
        String recommandCode = null;
        if(strings.length > 3)
         recommandCode = strings[3];
        UrlService service = ServiceManager.getInstance().getUrlService();
        try{
            JSONObject reqObject = new JSONObject();
            reqObject.put("phone", userName);
            reqObject.put("pwd", passwd);
            reqObject.put("nick", nickName);
            if(recommandCode != null)
                reqObject.put("spreader", recommandCode);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
            Call<ResponseBody> resBody = service.register(body);
            Response<ResponseBody> response  = resBody.execute();
            String result = response.body().string();
            Log.i(TAG, "doInbackgournd->result:" + result);
            JSONObject resObject = new JSONObject(result);
            return resObject.getString("msg").equals("success")? ConstData.ErrorInfo.NO_ERR : ConstData.ErrorInfo.ACCOUNT_EXIST;
        }catch (Exception e){
            e.printStackTrace();
            return ConstData.ErrorInfo.UNKNOW_ERR;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        mCallback.onResult(result);
    }

    public int register(String userName, String passwd, String nickName, String recommandCode){
        if(TextUtils.isEmpty(recommandCode))
            return doInBackground(userName, passwd, nickName);
        return doInBackground(userName, passwd, nickName, recommandCode);
    }
}
