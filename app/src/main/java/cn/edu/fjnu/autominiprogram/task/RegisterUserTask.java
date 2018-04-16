package cn.edu.fjnu.autominiprogram.task;

import android.os.AsyncTask;
import android.util.Log;

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
        String recommandCode = strings[3];
        String photoBase64 = strings[4];
        UrlService service = ServiceManager.getInstance().getUrlService();
        try{
            JSONObject reqObject = new JSONObject();
            reqObject.put("phone", userName);
            reqObject.put("pwd", passwd);
            reqObject.put("nick", nickName);
            //if(recommandCode != null)
            reqObject.put("spreader", recommandCode);
            reqObject.put("base64", photoBase64);
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

    public int register(String userName, String passwd, String nickName, String recommendCode, String photoBase64){
        return doInBackground(userName, passwd, nickName, recommendCode, photoBase64);
    }
}
