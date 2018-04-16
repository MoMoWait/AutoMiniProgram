package cn.edu.fjnu.autominiprogram.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.bean.SuggestionReplyInfo;

/**
 * Created by gaofei on 2018/3/31.
 * 通知适配器
 */

public class SuggestionReplyAdapter extends ArrayAdapter<SuggestionReplyInfo> {
    //private List<AllColorInfo> mObjects;
    private LayoutInflater mInflater;
    private int mResourceId;
    public SuggestionReplyAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<SuggestionReplyInfo> objects) {
        super(context, resource, textViewResourceId, objects);
        // mObjects = objects;
        mResourceId = resource;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = null;
        if(convertView != null){
            itemView = convertView;
        }else{
            itemView = mInflater.inflate(mResourceId, parent, false);
        }
        SuggestionReplyInfo itemColorInfo = getItem(position);
        TextView textSuggestion = (TextView) itemView.findViewById(R.id.text_suggestion);
        TextView textReply = (TextView) itemView.findViewById(R.id.text_reply);
        TextView textTime = (TextView) itemView.findViewById(R.id.text_time);

        if(itemColorInfo != null){
            textSuggestion.setText(itemColorInfo.getMsg());
            textReply.setText(itemColorInfo.getReply());
            textTime.setText(itemColorInfo.getTime());
        }

        return itemView;

    }
}
