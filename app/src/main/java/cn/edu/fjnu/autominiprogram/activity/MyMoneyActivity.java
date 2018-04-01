package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.MyMoneyFragment;

/**
 * Created by gaofei on 2018/3/31.
 * 我的奖金页面
 */

public class MyMoneyActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new MyMoneyFragment();
    }
}
