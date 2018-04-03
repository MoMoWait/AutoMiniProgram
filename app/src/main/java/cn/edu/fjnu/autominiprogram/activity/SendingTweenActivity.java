package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.SendingTweenFragment;

/**
 * Created by gaofei on 2018/4/1.
 * 发单建隔
 */

public class SendingTweenActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SendingTweenFragment();
    }
}
