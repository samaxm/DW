package com.sx.dw.social.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sx.dw.R;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/16 15:00
 */

public class UserIconFragment extends Fragment{


    private String iconURI;
    private SimpleDraweeView iconHolder;


    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private View.OnClickListener clickListener;

    public void setTouchListener(View.OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    private View.OnTouchListener touchListener;
    private int position;
    public static String PLACE_HOLDER="PLACE_HOLDER";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_user_icon, null);
        iconHolder= (SimpleDraweeView) view.findViewById(R.id.user_icon);
        iconHolder.setOnClickListener(clickListener);
        iconHolder.setOnTouchListener(touchListener);
        if(!iconURI.equals(PLACE_HOLDER)){
            iconHolder.setImageURI(iconURI);
        }else{
            iconHolder.getHierarchy().setPlaceholderImage(R.drawable.add_icon);
        }
        return view;
    }

    public void setIconURI(String iconURI) {
        this.iconURI = iconURI;
        if(iconHolder!=null)
            iconHolder.setImageURI(iconURI);

    }


    public void setPosition(int position) {
        this.position = position;
    }
}
