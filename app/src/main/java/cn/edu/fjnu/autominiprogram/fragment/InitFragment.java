package cn.edu.fjnu.autominiprogram.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.LoginActivity;
import cn.edu.fjnu.autominiprogram.activity.MainActivity;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.TrendInfo;
import cn.edu.fjnu.autominiprogram.data.Configs;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.task.AppLoadTask;
import cn.edu.fjnu.autominiprogram.task.ContentLoadTask;
import cn.edu.fjnu.autominiprogram.utils.LottyDataGetUtils;
import cn.edu.fjnu.autominiprogram.view.UpdateProgressDialog;
import momo.cn.edu.fjnu.androidutils.utils.NetWorkUtils;
import momo.cn.edu.fjnu.androidutils.utils.PackageUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;

/**
 * 初始化封面
 * Created by GaoFei on 2016/3/24.
 */
@ContentView(R.layout.fragment_init)
public class InitFragment extends AppBaseFragment{
    private static final String TAG = "InitFragment";
    @ViewInject(R.id.img_init)
    private ImageView mImgInit;

    private InitTask mInitTask;
    private AppLoadTask mLoadTask;
    private ContentLoadTask mContentTask;
    private volatile boolean mNeedDownload = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mInitTask.getStatus() == AsyncTask.Status.RUNNING)
            mInitTask.cancel(true);
    }


    @Override
    public void init() {
        super.init();
        //设置状态栏颜色
        if(Build.VERSION.SDK_INT >= 21)
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#097c9e"));
        mInitTask = new InitTask();
        if(NetWorkUtils.haveInternet(getContext()))
            mInitTask.execute();
        else
            showNetWorkErrorDialog();
    }


    public class InitTask extends AsyncTask<String, Integer, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(Configs.INIT_TIME);
                return ConstData.TaskResult.SUCC;
            } catch (Exception e) {
                e.printStackTrace();
                return ConstData.TaskResult.FAILED;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == ConstData.TaskResult.SUCC){
                Activity activity = getActivity();
                if(activity != null){
                    activity.startActivity(new Intent(getActivity(), LoginActivity.class));
                    activity.finish();
                }
            }else{
                //超时，直接关闭页面
                if(getActivity() != null)
                    getActivity().finish();
            }
        }
    }


}
