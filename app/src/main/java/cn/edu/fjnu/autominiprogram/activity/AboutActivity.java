package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.AboutFragment;


/**
 * Created by Administrator on 2017\9\4 0004.
 * 关于我们
 */

public class AboutActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new AboutFragment();
    }
}
