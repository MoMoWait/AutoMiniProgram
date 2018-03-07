package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.WyJoinMethodDisplayFragment;

/**
 * Created by gaofei on 2018/2/9.
 * 网易玩法介绍显示页面
 */

public class WyJoinMethodDisplayActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new WyJoinMethodDisplayFragment();
    }
}
