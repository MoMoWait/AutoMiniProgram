package cn.edu.fjnu.autominiprogram.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017\9\14 0014.
 * 登录验证模块
 */

public class LoginTask extends AsyncTask<String, Integer, UserInfo> {

    private static final String TAG = "LoginTask";

    public interface Callback{
        void onResult(UserInfo userInfo);
    }

    private Callback mCallback;


    public LoginTask(){

    }

    public LoginTask(Callback callback){
        mCallback = callback;
    }

    @Override
    protected UserInfo doInBackground(String... strings) {
        String userName = strings[0];
        String passwd = strings[1];
        UrlService service = ServiceManager.getInstance().getUrlService();
        try{
            JSONObject reqObject = new JSONObject();
            reqObject.put("phone", userName);
            reqObject.put("pwd", passwd);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
            Call<ResponseBody> resBody = service.loigin(body);
            Response<ResponseBody> response  = resBody.execute();
            String result = response.body().string();
            JSONObject resObject = new JSONObject(result);
            UserInfo resInfo = new UserInfo();
            resInfo.setUserId(resObject.getInt("user_id"));
            resInfo.setPhone(resObject.getString("user_phone"));
            resInfo.setPasswd(resObject.getString("user_pwd"));
            resInfo.setType(resObject.getInt("user_type"));
            resInfo.setState(resObject.getInt("user_state"));
            resInfo.setUserName(resObject.getString("user_nickname"));
            resInfo.setSpreadSpace(resObject.getString("user_spreadspace"));
            resInfo.setGroupNum(resObject.getInt("user_groupnum"));
            resInfo.setSingleNum(resObject.getInt("user_singlenum"));
            resInfo.setSpreader(resObject.getInt("user_spreader"));
            resInfo.setMoney(resObject.get("money") == null  || resObject.getString("money").equals("null")?  0 : resObject.getDouble("money"));
            return resInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(UserInfo result) {
        mCallback.onResult(result);
    }

    public UserInfo login(String userName, String passwd){
        return doInBackground(userName, passwd);
    }
}
