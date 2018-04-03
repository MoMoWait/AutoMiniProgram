package cn.edu.fjnu.autominiprogram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.SuggestionReplyActivity;
import cn.edu.fjnu.autominiprogram.activity.SystemNotificationActivity;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;

/**
 * Created by gaofei on 2018/3/10.
 * 通知页面
 */

@ContentView(R.layout.fragment_notifaction)
public class NotifactionFragment extends AppBaseFragment {

    @ViewInject(R.id.layout_notification)
    private LinearLayout mLayoutNotification;
    @ViewInject(R.id.layout_suggestion_reply)
    private LinearLayout mLayoutSuggestionReply;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mLayoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SystemNotificationActivity.class);
                startActivity(intent);
            }
        });
        mLayoutSuggestionReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SuggestionReplyActivity.class));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
