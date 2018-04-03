package cn.edu.fjnu.autominiprogram.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.HelloActivity;
import cn.edu.fjnu.autominiprogram.activity.SendingAutoStartStopActivity;
import cn.edu.fjnu.autominiprogram.activity.SendingTweenActivity;
import cn.edu.fjnu.autominiprogram.activity.SettingSendGroupActivity;
import cn.edu.fjnu.autominiprogram.activity.SuggestionActivity;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
/**
 * Created by gaofei on 2018/3/8.
 * 设置页面
 */

@ContentView(R.layout.fragment_settings)
public class SettingsFramgnet extends AppBaseFragment implements View.OnClickListener{

    @ViewInject(R.id.layout_send_group_num)
    private LinearLayout mLayoutSendGroupNum;

    @ViewInject(R.id.layout_hello)
    private LinearLayout mLayoutHello;

    @ViewInject(R.id.layout_suggestion)
    private LinearLayout mLayoutSuggestion;

    @ViewInject(R.id.layout_tween_time)
    private LinearLayout mLayoutTweenTime;

    @ViewInject(R.id.layout_auto_start_stop)
    private LinearLayout mLayoutAutoStartStop;

    @ViewInject(R.id.layout_logcat_trace)
    private LinearLayout mLayoutLogcatTrace;

    @ViewInject(R.id.layout_update)
    private LinearLayout mLayoutUpdate;

    private boolean mIsLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }


    @Override
    public void init(){
        mLayoutSendGroupNum.setOnClickListener(this);
        mLayoutTweenTime.setOnClickListener(this);
        mLayoutAutoStartStop.setOnClickListener(this);
        mLayoutSuggestion.setOnClickListener(this);
        mLayoutHello.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_send_group_num:
                startActivity(new Intent(getContext(), SettingSendGroupActivity.class));
                break;
            case R.id.layout_tween_time:
                startActivity(new Intent(getContext(), SendingTweenActivity.class));
                break;
            case R.id.layout_auto_start_stop:
                startActivity(new Intent(getContext(), SendingAutoStartStopActivity.class));
                break;
            case R.id.layout_suggestion:
                startActivity(new Intent(getContext(), SuggestionActivity.class));
                break;
            case R.id.layout_hello:
                startActivity(new Intent(getContext(), HelloActivity.class));
                break;
        }
    }
}
