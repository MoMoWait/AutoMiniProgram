package cn.edu.fjnu.autominiprogram.task;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import momo.cn.edu.fjnu.androidutils.data.CommonValues;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;


/**
 * Created by gaofei on 2018/4/2.
 * 日志上传任务块
 */

public class LogUploadTask extends AsyncTask<File,Integer,Integer> {
    private File mLogFile;
    private Callback mCallback;
    private UserInfo mUserInfo;
    public interface Callback{
        void onResult(int error);
    }


    public LogUploadTask(Callback callback){
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(File... files) {
        try{
            mUserInfo = (UserInfo) JsonUtils.jsonToObject(UserInfo.class, new JSONObject(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_USER_INFO)));
        }catch (Exception e){
            e.printStackTrace();
        }

        if(mUserInfo == null){
            return ConstData.ErrorInfo.UNKNOW_ERR;
        }
        mLogFile = files[0];
        if(mLogFile != null && mLogFile.exists() && mLogFile.length() > 0){
            try{
                UrlService urlService = ServiceManager.getInstance().getUrlService();
                final File newFile = CommonValues.application.getFileStreamPath(mUserInfo.getUserId() + "-crash_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                mLogFile.renameTo(newFile);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), newFile);
                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", newFile.getName(), requestFile);
                Call<ResponseBody> call = urlService.uploadLogFile(body);
                call.execute();
                if(newFile.exists()){
                    newFile.delete();
                }
                File newLogFile = new File(mLogFile.getAbsolutePath());
                if(newLogFile.exists())
                    newLogFile.delete();
               return ConstData.ErrorInfo.NO_ERR;
            }catch (Exception e){
                e.printStackTrace();
                //no handle
            }


        }
        return ConstData.ErrorInfo.UNKNOW_ERR;
    }

    @Override
    protected void onPostExecute(Integer result) {
        mCallback.onResult(result);


    }
}
