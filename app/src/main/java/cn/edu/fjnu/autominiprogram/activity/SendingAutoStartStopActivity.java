package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.SendingAutoStartStopFragment;

/**
 * Created by gaofei on 2018/4/1.
 * 自动启停
 */

public class SendingAutoStartStopActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SendingAutoStartStopFragment();
    }
}
