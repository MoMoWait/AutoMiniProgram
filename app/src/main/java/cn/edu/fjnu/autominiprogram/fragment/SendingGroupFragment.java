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
import cn.edu.fjnu.autominiprogram.data.ConstData;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/4/1.
 * 发单群数
 */
@ContentView(R.layout.fragment_sending_group)
public class SendingGroupFragment extends AppBaseFragment {

    @ViewInject(R.id.edit_send_group_num)
    private EditText mEditSendGroupNum;
    @ViewInject(R.id.btn_ok)
    private Button mBtnOK;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNum = mEditSendGroupNum.getText().toString().trim();
                if(TextUtils.isEmpty(groupNum)){
                    ToastUtils.showToast(R.string.enter_send_group_num);
                    return;
                }
                StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.SEND_GROUP_NUM, groupNum);
                ToastUtils.showToast("设置成功");
                getActivity().finish();
            }
        });
    }
}
