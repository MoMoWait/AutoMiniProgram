package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.HelloFragment;

/**
 * Created by gaofei on 2018/4/2.
 * 问候语
 */

public class HelloActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new HelloFragment();
    }
}
