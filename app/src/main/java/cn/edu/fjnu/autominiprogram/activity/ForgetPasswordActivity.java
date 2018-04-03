package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;
import cn.edu.fjnu.autominiprogram.fragment.ForgetPasswordFragment;

/**
 * Created by gaofei on 2018/3/29.
 * 忘记密码页面
 */

public class ForgetPasswordActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new ForgetPasswordFragment();
    }
}
