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
import cn.edu.fjnu.autominiprogram.bean.RecommendUserInfo;

/**
 * Created by gaofei on 2018/3/31.
 * 通知适配器
 */

public class RecommendAdapter extends ArrayAdapter<RecommendUserInfo> {
    //private List<AllColorInfo> mObjects;
    private LayoutInflater mInflater;
    private int mResourceId;
    public RecommendAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<RecommendUserInfo> objects) {
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
        RecommendUserInfo itemColorInfo = getItem(position);
        TextView textUserId = (TextView) itemView.findViewById(R.id.text_user_id);
        TextView textPhone = (TextView) itemView.findViewById(R.id.text_phone);
        TextView textNickName = (TextView) itemView.findViewById(R.id.text_nick_name);
        TextView textUserType = (TextView) itemView.findViewById(R.id.text_user_type);
        TextView textUserRecommendTime = (TextView) itemView.findViewById(R.id.text_recommend_time);
        TextView textCanGet = (TextView) itemView.findViewById(R.id.text_can_get);

        if(itemColorInfo != null){
            textUserId.setText("用户ID：" + itemColorInfo.getUserId());
            textPhone.setText("手机号："  + itemColorInfo.getPhone());
            textNickName.setText("昵称：" + itemColorInfo.getNickName());
            int type = itemColorInfo.getType();
            switch (type){
                case 1:
                    textUserType.setText("用户类型：" + "正式用户");
                    break;
                case 2:
                    textUserType.setText("用户类型：" + "待审核用户");
                    break;
                case 3:
                    textUserType.setText("用户类型：" + "审核未通过用户");
                    break;
                case 4:
                    textUserType.setText("用户类型：" + "正式用户（可提现）");
                    break;
            }
            textNickName.setText("推荐时间：" + itemColorInfo.getRestime());
            textCanGet.setText("可提金额：" + itemColorInfo.getCanGet());

        }

        return itemView;

    }
}
