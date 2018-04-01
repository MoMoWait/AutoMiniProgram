package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by gaofei on 2018/3/30.
 * 密码修改页面
 */
@ContentView(R.layout.fragment_password_change)
public class PasswordChangeFragment extends AppBaseFragment {

    @ViewInject(R.id.edit_password)
    private EditText mEditPassword;
    @ViewInject(R.id.edit_confirm_password)
    private EditText mEditConfirmPassword;
    @ViewInject(R.id.btn_confirm_change)
    private Button mBtnConfirmChange;
    @ViewInject(R.id.text_phone_num)
    private TextView mTextPhoneNum;
    private String mPhoneNum;
    private String mFirstPassword;
    private String mConfirmPassword;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mPhoneNum = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.FORGET_PASSWORD_PHONE);
        mTextPhoneNum.setText(mPhoneNum);
        mBtnConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showLoadingDialog(getContext(), false);
                mFirstPassword = mEditPassword.getText().toString();
                mConfirmPassword = mEditConfirmPassword.getText().toString();
                if(TextUtils.isEmpty(mFirstPassword) || TextUtils.isEmpty(mConfirmPassword)){
                    //请输入密码
                    ToastUtils.showToast(R.string.input_password);
                    return;
                }

                if(!mFirstPassword.equals(mConfirmPassword)){
                    ToastUtils.showToast(R.string.password_not_same);
                    return;
                }

                Observable.just(new Object()).map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("phone", mPhoneNum);
                        reqObject.put("newpwd", mFirstPassword);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                        ResponseBody responseBody = urlService.resetPassword(body).execute().body();
                        if(responseBody != null){
                            String result = responseBody.string();
                            String msgResult = new JSONObject(result).getString("msg");
                            if(ConstData.MsgResult.SUCC.equals(msgResult)){
                                return ConstData.ErrorInfo.NO_ERR;
                            }else{
                                return ConstData.ErrorInfo.UNKNOW_ERR;
                            }
                            //new JSONObject(result).
                        }

                        return ConstData.ErrorInfo.NET_ERR;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer result) throws Exception {
                        DialogUtils.closeLoadingDialog();
                        if(result == ConstData.ErrorInfo.NO_ERR){
                            ToastUtils.showToast(R.string.password_change_succ);
                            getActivity().finish();
                        }else{
                            ToastUtils.showToast(R.string.password_change_failed);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        DialogUtils.closeLoadingDialog();
                        ToastUtils.showToast(R.string.password_change_failed);
                    }
                });
            }
        });
    }



    @Override
    public CharSequence getActivityLable() {
        return getString(R.string.password_change);
    }
}
