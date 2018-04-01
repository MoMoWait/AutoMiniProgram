package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by gaofei on 2018/3/31.
 * 提现申请页面
 */
@ContentView(R.layout.fragment_request_money)
public class RequestMoneyFragment extends AppBaseFragment {

    @ViewInject(R.id.edit_ailipay_account)
    private EditText mEditAilipayAccount;
    @ViewInject(R.id.edit_request_money)
    private EditText mEditRequestMoney;
    @ViewInject(R.id.btn_request_money)
    private Button mBtnRequestMoney;
    private UserInfo mUserInfo;
    private String mAiliPayAccount;
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
        View rootView = getView();
        if(rootView != null){
            rootView.setFocusable(true);
            rootView.setFocusableInTouchMode(true);
            rootView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyMoneyFragment()).commit();
                        return true;
                    }
                    return false;
                }
            });
        }

        mBtnRequestMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAiliPayAccount = mEditAilipayAccount.getText().toString();
                if(TextUtils.isEmpty(mAiliPayAccount)){
                    ToastUtils.showToast(R.string.enter_ailipay_account);
                    return;
                }
                Observable.fromArray(new Object()).map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("spreader_id", mUserInfo.getUserId());
                        reqObject.put("aliacc", mAiliPayAccount);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                        ResponseBody responseBody = urlService.checkCode(body).execute().body();
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
                            ToastUtils.showToast(R.string.request_money_success);
                            getActivity().finish();
                        }else{
                            ToastUtils.showToast(R.string.request_money_failed);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.request_money_failed);
                    }
                });


            }
        });
    }




}
