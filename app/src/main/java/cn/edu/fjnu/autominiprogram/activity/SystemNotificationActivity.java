package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.SystemNotificationFragment;

/**
 * Created by gaofei on 2018/3/31.
 * 系统通知页面
 */

public class SystemNotificationActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SystemNotificationFragment();
    }
}
