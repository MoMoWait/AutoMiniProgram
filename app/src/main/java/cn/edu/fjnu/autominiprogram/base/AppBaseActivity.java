package cn.edu.fjnu.autominiprogram.base;
import android.os.Bundle;
import momo.cn.edu.fjnu.androidutils.base.BaseActivity;
import momo.cn.edu.fjnu.androidutils.utils.ActivityExitUtils;

/**
 * Created by gaofei on 2017/9/8.
 * 应用基本Acitivy
 */

public class AppBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityExitUtils.addActivity(this);
    }
}

