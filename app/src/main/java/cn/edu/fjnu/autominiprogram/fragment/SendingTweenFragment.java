package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/4/1.
 * 发单间隔页面
 */
@ContentView(R.layout.fragment_sending_tween)
public class SendingTweenFragment extends AppBaseFragment {

    @ViewInject(R.id.check_random)
    private CheckBox mCheckRandom;
    @ViewInject(R.id.btn_ok)
    private Button mBtnOK;
    @ViewInject(R.id.edit_sending_tween)
    private EditText mEditSendingTween;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mEditSendingTween.setText(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.SEND_TWEEN_TIME));
        mCheckRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //mEditSendingTween.setFocusable(false);
                    mEditSendingTween.setEnabled(false);
                    //mEditSendingTween.setText("");
                }else{
                    mEditSendingTween.setEnabled(true);
                }
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mCheckRandom.isChecked();
                if(isChecked){
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_SEND_TWEEN_RANDOM, "true");
                    ToastUtils.showToast("设置成功");
                }else{
                    String strNum = mEditSendingTween.getText().toString();
                    if(TextUtils.isEmpty(strNum)){
                        ToastUtils.showToast(R.string.enter_sending_tween);
                        return;
                    }
                    //int num = Integer.parseInt(strNum);
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_SEND_TWEEN_RANDOM, "false");
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.SEND_TWEEN_TIME, strNum);
                    ToastUtils.showToast("设置成功");
                }

                getActivity().finish();
            }
        });
    }
}
