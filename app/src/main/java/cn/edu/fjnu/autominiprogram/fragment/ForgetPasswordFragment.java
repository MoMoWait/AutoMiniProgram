package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;

/**
 * Created by gaofei on 2018/3/29.
 * 忘记密码页面
 */
@ContentView(R.layout.fragment_forget_password)
public class ForgetPasswordFragment extends AppBaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
    }
}
