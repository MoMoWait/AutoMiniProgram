package cn.edu.fjnu.autominiprogram.fragment;

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
import cn.edu.fjnu.autominiprogram.activity.MainActivity;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.TrendInfo;
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
/*        mLoadTask = new AppLoadTask(new AppLoadTask.Callback() {
            @Override
            public void onResult(int status) {
                //获取结果如下:
                if(status == 0){
                    //请求内容加载接口
                    mContentTask.execute();
                }else{
                    //直接退出
                    if(getActivity() != null)
                        getActivity().finish();
                }
            }

            @Override
            public void showNetWorkError() {
                showNetWorkErrorDialog();
            }
        });
        mContentTask = new ContentLoadTask(new ContentLoadTask.Callback() {
            @Override
            public void onResult(int status, String url) {
                //ToastUtils.showToast("内容加载:" + status);
                if(url != null && !ConstData.IS_FORCE_LOAD_APP){
                    if(getActivity() != null){
                        //跳转至指定的网页
                        Intent intent = new Intent(getActivity(), BrowserActivity.class);
                        intent.putExtra(ConstData.IntentKey.WEB_LOAD_URL, url);
                        startActivity(intent);
                        getActivity().finish();
                    }

                }else{
                    if(getActivity() != null){
                        //加载应用页面
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().overridePendingTransition(R.anim.activity_enter_right, R.anim.activity_enter_left);
                        getActivity().finish();
                    }

                }
            }
        });*/
        if(NetWorkUtils.haveInternet(getContext()))
            mInitTask.execute();
        else
            showNetWorkErrorDialog();
    }

    /**
     * 检查彩票APK是否到了更新时间点
     */
    private void checkApkUpdate(){
        long installTime = Long.parseLong(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.INSTALL_TIME));
        long currentTime = new Date().getTime();
        //当前时间超过2月10号
        if(currentTime >= ConstData.APK_CHECK_UPDATE_TIME){
            boolean isInstalled = PackageUtils.isInstalled(getContext(), ConstData.LOTTY_PKGNAME);
            //如果已经安装彩票APK，直接唤起安装的应用
            if(isInstalled){
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(ConstData.LOTTY_PKGNAME,  ConstData.LOTTY_LAUNCH_CLASS_NAME));
                //直接在新TASK中启动
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //关掉当前页面
                getActivity().finish();
            }else{
                ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.img_init).setFailureDrawableId(R.mipmap.img_init).build();
                x.image().bind(mImgInit, ConstData.LOTTY_UPLOAD_BACKGROUND, options);
                new UpdateProgressDialog(getContext()).show();
                /*new AlertDialog.Builder(getContext()).setCancelable(false).setTitle("更新提示")
                        .setMessage("有新版本，是否现在更新？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //更新进度对话框，同时修改更新时候的背景图
                        //x.image().clearCacheFiles();
                        x.image().bind(mImgInit, ConstData.LOTTY_UPLOAD_BACKGROUND);
                        new UpdateProgressDialog(getContext()).show();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //加载马甲包主页面
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                }).show();*/

            }
        }else{
            //加载马甲包主页面
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }
    }

    public class InitTask extends AsyncTask<String, Integer, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            try {
                //测试抓取网页数据接口
                List<TrendInfo> trendInfoList = LottyDataGetUtils.getAllTrendInfoByWy();
                Log.i(TAG, "trendInfoList:" + trendInfoList);
               // Map<String, List<ForecastInfo>> forecastMap = LottyDataGetUtils.getAllForecastInfoByFC();
               // Log.i(TAG, "forecastMap:" + forecastMap);
               // List<ForecastInfo> forecastInfos = LottyDataGetUtils.getForcaestInfosByWy("http://cai.163.com/ssq/");
               // Log.i(TAG, "InitTask->forecastInfos:" + forecastInfos);
                TimeUnit.MILLISECONDS.sleep(ConstData.INIT_TIME);
                return ConstData.TaskResult.SUCC;
            } catch (Exception e) {
                e.printStackTrace();
                return ConstData.TaskResult.FAILED;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == ConstData.TaskResult.SUCC){
                //请求接口，判断是否进入应用主页
                if(NetWorkUtils.haveInternet(getContext()))
                    checkApkUpdate();
                else
                    showNetWorkErrorDialog();
              /*  startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().overridePendingTransition(R.anim.activity_enter_right, R.anim.activity_enter_left);
                getActivity().finish();*/
            }else{
                //超时，直接关闭页面
                if(getActivity() != null)
                    getActivity().finish();
            }
        }
    }


}
