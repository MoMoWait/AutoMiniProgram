package cn.edu.fjnu.autominiprogram.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.bean.TrendInfo;
import momo.cn.edu.fjnu.androidutils.utils.SizeUtils;

/**
 * Created by gaofei on 2017/11/30.
 * 所有彩票类型适配器
 */

public class FcTrendTypeAdapter extends ArrayAdapter<TrendInfo> {
    private int mResource;
    private Context mContext;
    public FcTrendTypeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<TrendInfo> objects) {
        super(context, resource, objects);
        mResource = resource;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView;
        if(convertView != null)
            itemView = convertView;
        else
            itemView = LayoutInflater.from(mContext).inflate(mResource, parent ,false);
        ViewHolder holder = (ViewHolder) itemView.getTag();
        if(holder == null){
            holder = new ViewHolder();
            holder.itemImg = (ImageView) itemView.findViewById(R.id.img_lottery_type);
            holder.itemImg.setPadding(SizeUtils.dp2px(20),SizeUtils.dp2px(20),SizeUtils.dp2px(20),SizeUtils.dp2px(20));
            holder.itemText = (TextView) itemView.findViewById(R.id.text_lottery_name);
            itemView.setTag(holder);
        }
        TrendInfo itemInfo = getItem(position);
        //x.image().bind(holder.itemImg, itemInfo.getImgUrl());
        holder.itemText.setText(itemInfo.getName());
        return  itemView;
    }

    class ViewHolder{
        ImageView itemImg;
        TextView itemText;
    }
}
