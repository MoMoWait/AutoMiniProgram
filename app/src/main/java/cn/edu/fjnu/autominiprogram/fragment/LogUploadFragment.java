package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.task.LogUploadTask;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
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
                    new LogUploadTask(new LogUploadTask.Callback() {
                        @Override
                        public void onResult(int error) {
                            DialogUtils.closeLoadingDialog();
                            if(error == ConstData.ErrorInfo.NO_ERR){
                                ToastUtils.showToast(R.string.log_upload_succ);
                            }else{
                                ToastUtils.showToast(R.string.log_upload_failed);
                            }
                        }
                    }).execute(mLogFile);
                }else{
                    ToastUtils.showToast(R.string.no_log_file);
                }
            }
        });
    }
}
