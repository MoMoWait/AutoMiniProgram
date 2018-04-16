package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gaofei on 2018/4/3.
 * 日志上传页面
 */

@ContentView(R.layout.fragment_log_upload)
public class LogUploadFragment extends AppBaseFragment {

    @ViewInject(R.id.text_file_size)
    private TextView mTextFileSize;

    @ViewInject(R.id.btn_upload_logcat)
    private Button mBtnUploadLogcat;

    private boolean mHaveLogFile;
    private File mLogFile;

    private UserInfo mUserInfo;

    private static final String TAG = LogUploadFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        try{
            mUserInfo = (UserInfo) JsonUtils.jsonToObject(UserInfo.class, new JSONObject(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_USER_INFO)));
        }catch (Exception e){
            e.printStackTrace();
        }

        if(mUserInfo == null){
            ToastUtils.showToast(R.string.app_exception);
            getActivity().finish();
            return;
        }
         mLogFile = getContext().getFileStreamPath(ConstData.COMMON_LOG_FILE_NAME);
        if(mLogFile != null && mLogFile.exists()){
            mHaveLogFile = true;
            long kb = mLogFile.length() / 1024;
            if(kb == 0){
                mTextFileSize.setText("日志文件大小:" + mLogFile.length() + "B");
            }else{
                mTextFileSize.setText("日志文件大小:" + kb + "KB");
            }

        }else{
            mTextFileSize.setText("日志文件不存在");
        }

        mBtnUploadLogcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHaveLogFile){
                    DialogUtils.showLoadingDialog(getContext(), false);
                    UrlService urlService = ServiceManager.getInstance().getUrlService();
                    final File newFile = getContext().getFileStreamPath(mUserInfo.getUserId() + "-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                    mLogFile.renameTo(newFile);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), newFile);
                    // MultipartBody.Part is used to send also the actual file name
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", newFile.getName(), requestFile);
                    Call<ResponseBody> call = urlService.uploadLogFile(body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.v("Upload", "success");
                            ToastUtils.showToast(R.string.log_upload_succ);
                            DialogUtils.closeLoadingDialog();
                            if(newFile.exists()){
                                newFile.delete();
                            }
                            File newLogFile = new File(mLogFile.getAbsolutePath());
                            if(newLogFile.exists())
                                newLogFile.delete();
                            getActivity().finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("Upload error:", t.getMessage());
                            ToastUtils.showToast(R.string.log_upload_failed);
                            DialogUtils.closeLoadingDialog();
                        }
                    });

                    // urlService.uploadLogFile()
                }else{
                    ToastUtils.showToast(R.string.no_log_file);
                }
            }
        });
    }
}
