package cn.edu.fjnu.autominiprogram.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.LogUploadActivity;
import cn.edu.fjnu.autominiprogram.activity.SuggestionReplyActivity;
import cn.edu.fjnu.autominiprogram.activity.SystemNotificationActivity;
import cn.edu.fjnu.autominiprogram.adapter.RecommendAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.RecommendUserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import cn.edu.fjnu.autominiprogram.view.UpdateProgressDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.PackageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by gaofei on 2018/3/10.
 * 通知页面
 */

@ContentView(R.layout.fragment_notifaction)
public class NotifactionFragment extends AppBaseFragment {

    @ViewInject(R.id.layout_notification)
    private LinearLayout mLayoutNotification;
    @ViewInject(R.id.layout_suggestion_reply)
    private LinearLayout mLayoutSuggestionReply;
    @ViewInject(R.id.layout_logcat_trace)
    private LinearLayout mLayoutLogcatTrace;
    @ViewInject(R.id.layout_update)
    private LinearLayout mLayoutUpdate;
    /**
     * 是否需要更新
     */
    private boolean mIsNeedUpdate;
    /**
     * 下载地址
     */
    private String mDownloadUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mLayoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SystemNotificationActivity.class);
                startActivity(intent);
            }
        });
        mLayoutSuggestionReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SuggestionReplyActivity.class));
            }
        });

        mLayoutLogcatTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LogUploadActivity.class));
            }
        });

        mLayoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showLoadingDialog(getContext(), false);
                Observable.just(new Object()).map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        ResponseBody responseBody = urlService.getAppVersion().execute().body();
                        if(responseBody != null){
                            String result = responseBody.string();
                            JSONObject resultObject = new JSONObject(result);
                            int appVersion = resultObject.getInt("file_verson");
                            mDownloadUrl = resultObject.getString("file_url");
                            mIsNeedUpdate = appVersion > getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionCode;
                            return ConstData.ErrorInfo.NO_ERR;

                        }
                        return ConstData.ErrorInfo.UNKNOW_ERR;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer result) throws Exception {
                        DialogUtils.closeLoadingDialog();
                        if(result == ConstData.ErrorInfo.NO_ERR){
                            if(mIsNeedUpdate){
                                updateApp();
                            }else{
                                ToastUtils.showToast(R.string.curr_newest_version);
                            }
                        }else{
                            ToastUtils.showToast(R.string.check_version_failed);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        DialogUtils.closeLoadingDialog();
                        ToastUtils.showToast(R.string.check_version_failed);
                    }
                });
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void updateApp(){
        new AlertDialog.Builder(getContext()).setTitle("更新提示").setMessage("有新的版本，是否更新").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new UpdateProgressDialog(getContext(), mDownloadUrl).show();
            }
        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
       // new UpdateProgressDialog(getContext(), mDownloadUrl).show();
    }
}
