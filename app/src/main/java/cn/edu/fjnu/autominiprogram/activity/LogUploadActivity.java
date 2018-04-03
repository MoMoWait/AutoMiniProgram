package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.LogUploadFragment;

/**
 * Created by gaofei on 2018/4/3.
 * 日志上传页面
 */

public class LogUploadActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new LogUploadFragment();
    }
}
