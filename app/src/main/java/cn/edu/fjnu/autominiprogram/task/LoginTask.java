package cn.edu.fjnu.autominiprogram.task;

import android.os.AsyncTask;
import org.json.JSONObject;
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

public class LoginTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "LoginTask";

    public interface Callback{
        void onResult(int status);
    }

    private Callback mCallback;


    public LoginTask(){

    }

    public LoginTask(Callback callback){
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String userName = strings[0];
        String passwd = strings[1];
        UrlService service = ServiceManager.getInstance().getUrlService();
        try{
            JSONObject reqObject = new JSONObject();
            reqObject.put("username", userName);
            reqObject.put("password", passwd);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
            Call<ResponseBody> resBody = service.loigin(body);
            Response<ResponseBody> response  = resBody.execute();
            String result = response.body().string();
            JSONObject resObject = new JSONObject(result);
            return resObject.getString("Msg").equals("success")? ConstData.TaskResult.SUCC : ConstData.TaskResult.FAILED;
        }catch (Exception e){
            e.printStackTrace();
            return ConstData.TaskResult.FAILED;
        }

    }

    @Override
    protected void onPostExecute(Integer result) {
        mCallback.onResult(result);
    }

    public Integer login(String userName, String passwd){
        return doInBackground(userName, passwd);
    }
}
