package com.sx.dw.social.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sx.dw.R;
import com.sx.dw.core.util.C;
import com.sx.dw.im.entity.LinkMan;

import java.util.List;

/**
 * @Description: 收藏列表适配器
 * @author: fanjie
 * @date: 2016/10/6 14:11
 */

public class LikeListAdapter extends RecyclerView.Adapter<LikeListAdapter.LikeViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<LinkMan> likes;

    public LikeListAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<LinkMan> likes){
        this.likes = likes;
        //        FIXME 更换吊炸天的策略 DiffUtil
        notifyDataSetChanged();
    }

    @Override
    public LikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LikeViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_like_records,parent,false));
    }

    @Override
    public void onBindViewHolder(LikeViewHolder h, final int position) {
        final LinkMan user = likes.get(position);
        if(C.STAR.equals(user.getType())){
            if(user.getTag()!=null){
                h.tvTag.setText(user.getTag());
                h.tvTag.setVisibility(View.VISIBLE);
            }
            h.starLogo.setVisibility(View.VISIBLE);
        }

        h.sdvUserIcon.setImageURI(user.getPrimaryIcon());
        h.tvUserName.setText(user.getName());
        if(TextUtils.isEmpty(user.getSign())) {
            h.tvUserSign.setVisibility(View.GONE);
        }else {
            h.tvUserSign.setVisibility(View.VISIBLE);
            h.tvUserSign.setText(user.getSign());
        }
        h.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(user);
            }
        });
        h.tvSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onBtnClick(user);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(likes == null) {
            return 0;
        }else {
            return likes.size();
        }
    }

    class LikeViewHolder extends RecyclerView.ViewHolder{
        View root;
        SimpleDraweeView sdvUserIcon;
        TextView tvUserName;
        TextView tvUserSign;
        TextView tvSendMsg;
        TextView tvTag;
        ImageView starLogo;

        LikeViewHolder(View itemView) {
            super(itemView);
            root =  itemView;
            starLogo= (ImageView) itemView.findViewById(R.id.starLogo);
            tvTag= (TextView) itemView.findViewById(R.id.tag);
            sdvUserIcon = (SimpleDraweeView) itemView.findViewById(R.id.sdv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvUserSign = (TextView) itemView.findViewById(R.id.tv_user_sign);
            tvSendMsg = (TextView) itemView.findViewById(R.id.tv_send_msg);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(LinkMan entity);
        void onBtnClick(LinkMan entity);
    }
}
