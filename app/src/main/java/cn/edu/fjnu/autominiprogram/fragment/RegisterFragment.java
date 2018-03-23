package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.task.RegisterUserTask;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by Administrator on 2017\9\14 0014.
 * 注册页面
 */

@ContentView(R.layout.fragment_register)
public class RegisterFragment extends AppBaseFragment {
    private static final String TAG = "RegisterFragment";
    @ViewInject(R.id.edit_phone_num)
    private EditText mEditPhoneNum;
    @ViewInject(R.id.edit_password)
    private EditText mEditPassword;
    @ViewInject(R.id.edit_confirm_password)
    private EditText mEditConfirmPassword;
    @ViewInject(R.id.btn_register)
    private Button mBtnRegister;
    @ViewInject(R.id.edit_recommand_code)
    private EditText mEditRecommandCode;
    @ViewInject(R.id.edit_wechat_nickname)
    private EditText mEditWechatNickName;

    private RegisterUserTask mRegisterTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mRegisterTask = new RegisterUserTask();
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mEditPhoneNum.getText().toString().trim();
                String password = mEditPassword.getText().toString();
                String confirmPassword = mEditConfirmPassword.getText().toString();
                final String nickeName = mEditWechatNickName.getText().toString().trim();
                final String recommandCode = mEditRecommandCode.getText().toString().trim();
                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(nickeName)){
                    ToastUtils.showToast(getString(R.string.input_all));
                    return;
                }
                if(!password.equals(confirmPassword)){
                    ToastUtils.showToast(getString(R.string.password_not_same));
                    return;
                }
                DialogUtils.showLoadingDialog(getContext(), false);
                UserInfo info = new UserInfo();
                info.setUserName(userName);
                info.setPasswd(password);
                Observable.just(info).map(new Function<UserInfo, Integer>() {
                    @Override
                    public Integer apply(@NonNull UserInfo userInfo) throws Exception {
                        return  mRegisterTask.register(userInfo.getUserName(), userInfo.getPasswd(), nickeName, recommandCode);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer status) throws Exception {
                                DialogUtils.closeLoadingDialog();
                                if(status == ConstData.TaskResult.SUCC){
                                    ToastUtils.showToast(getString(R.string.register_succ));
                                    getActivity().finish();
                                }else{
                                    ToastUtils.showToast(getString(R.string.register_failed));
                                }
                            }
                        });
            }
        });
    }
}
