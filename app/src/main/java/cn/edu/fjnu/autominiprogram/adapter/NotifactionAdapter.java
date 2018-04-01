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
import cn.edu.fjnu.autominiprogram.bean.NotifactionInfo;

/**
 * Created by gaofei on 2018/3/31.
 * 通知适配器
 */

public class NotifactionAdapter extends ArrayAdapter<NotifactionInfo> {
    //private List<AllColorInfo> mObjects;
    private LayoutInflater mInflater;
    private int mResourceId;
    public NotifactionAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<NotifactionInfo> objects) {
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
        NotifactionInfo itemColorInfo = getItem(position);
        TextView oneColorText = (TextView) itemView.findViewById(R.id.text_content);
        TextView twoColorText = (TextView) itemView.findViewById(R.id.text_time);
        if(itemColorInfo != null){
            oneColorText.setText(itemColorInfo.getContent());
            twoColorText.setText(itemColorInfo.getDateTime());
        }

        return itemView;

    }
}
