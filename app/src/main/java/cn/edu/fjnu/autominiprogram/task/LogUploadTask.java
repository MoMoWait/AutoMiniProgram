package cn.edu.fjnu.autominiprogram.task;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by gaofei on 2018/4/2.
 * 日志上传任务块
 */

public class LogUploadTask extends AsyncTask<File,Integer,Integer> {
    private File mLogFile;
    @Override
    protected Integer doInBackground(File... files) {
        mLogFile = files[0];
        if(mLogFile != null && mLogFile.exists() && mLogFile.length() > 0){
            try{
                FileInputStream inputStream = new FileInputStream(mLogFile);
                byte[] content = new byte[(int)mLogFile.length()];
                inputStream.read(content);
                UserInfo userInfo = (UserInfo) JsonUtils.jsonToObject(UserInfo.class, new JSONObject(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_USER_INFO)));
                String logContent = new String(content);
                //上传日志内容
                UrlService service = ServiceManager.getInstance().getUrlService();
                JSONObject reqObject = new JSONObject();
                reqObject.put("user_id", userInfo.getUserId());
                reqObject.put("abnormal_msg", logContent);
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                Call<ResponseBody> resBody = service.uploadLogContent(body);
                Response<ResponseBody> response  = resBody.execute();
                String result = response.body().string();
                JSONObject resObject = new JSONObject(result);
                return   ConstData.MsgResult.SUCC.equals(resObject.getString("msg")) ? ConstData.ErrorInfo.NO_ERR : ConstData.ErrorInfo.UNKNOW_ERR;

            }catch (Exception e){
                e.printStackTrace();
                //no handle
            }


        }
        return ConstData.ErrorInfo.UNKNOW_ERR;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result == ConstData.ErrorInfo.NO_ERR && mLogFile != null && mLogFile.exists())
            mLogFile.delete();

    }
}
