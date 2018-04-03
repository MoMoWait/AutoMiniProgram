package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import cn.edu.fjnu.autominiprogram.task.RegisterUserTask;
import cn.edu.fjnu.autominiprogram.task.SuggestionUploadTask;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017\9\4 0004.
 * 意见反馈
 */

@ContentView(R.layout.fragment_suggestion)
public class SuggestionFragment extends AppBaseFragment {


    @ViewInject(R.id.edit_suggestion)
    private EditText mEditSuggestion;
    @ViewInject(R.id.edit_phone)
    private EditText mEditPhone;
    @ViewInject(R.id.btn_suggestion)
    private Button mBtnSuggestion;
    private UserInfo mUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
        mBtnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String suggestion = mEditSuggestion.getText().toString();
                if(TextUtils.isEmpty(suggestion)){
                    ToastUtils.showToast("请输入建议，反馈");
                    return;
                }
                DialogUtils.showLoadingDialog(getActivity(), false);
                Observable.fromArray(new Object()).map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("user_id", mUserInfo.getUserId());
                        reqObject.put("suggestion", suggestion);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                        ResponseBody responseBody = urlService.insertSuggestion(body).execute().body();
                        if(responseBody != null){
                            String result = responseBody.string();
                            if(new JSONObject(result).getString("msg").equals(ConstData.MsgResult.SUCC))
                                return ConstData.ErrorInfo.NO_ERR;
                            return ConstData.ErrorInfo.NET_ERR;
                        }
                        return ConstData.ErrorInfo.UNKNOW_ERR;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer result) throws Exception {
                        if(result == ConstData.ErrorInfo.NO_ERR){
                            ToastUtils.showToast(R.string.suggestion_succ);
                            getActivity().finish();
                        }else{
                            ToastUtils.showToast(R.string.suggestion_failed);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.suggestion_failed);
                    }
                });
            }
        });
    }

}
