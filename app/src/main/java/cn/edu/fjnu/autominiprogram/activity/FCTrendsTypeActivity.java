package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.FCTrendsTypeFramgnet;

/**
 * Created by gaofei on 2018/2/7.
 * 发彩网走势页面
 */

public class FCTrendsTypeActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new FCTrendsTypeFramgnet();
    }
}
