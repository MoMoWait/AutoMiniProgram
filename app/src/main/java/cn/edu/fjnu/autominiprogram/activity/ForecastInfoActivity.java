package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.ForcaestInfoFragment;

/**
 * Created by gaofei on 2018/2/7.
 * 预测消息
 */

public class ForecastInfoActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new ForcaestInfoFragment();
    }
}
