package com.sx.dw.social.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/16 14:57
 */

public class UserIconPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

    private List<String> icons;
    private HashMap<String,UserIconFragment> fragments=new HashMap<>();
    private int currentPostion;
    private boolean leftEnd;
    private boolean rightEnd;
    private boolean draging;
    private ViewPager pager;
    private View.OnClickListener clickListener;
    private View.OnTouchListener touchListener;

    public void setTouchListener(View.OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }
    public UserIconFragment getCurrentUserIconFragment(){
        return fragments.get(icons.get(currentPostion));
    }
    public String getCurrentIcon(){
        return icons.get(currentPostion);
    }

    public UserIconPageAdapter(FragmentManager fm, List<String> icons, ViewPager pager, View.OnClickListener listener) {
        super(fm);
        this.icons=icons;
        this.pager=pager;
        this.clickListener=listener;
    }

    @Override
    public Fragment getItem(int position) {
        UserIconFragment fragment=new UserIconFragment();
        fragments.put(icons.get(position),fragment);
        fragment.setIconURI(icons.get(position));
        fragment.setPosition(position);
        fragment.setClickListener(clickListener);
        fragment.setTouchListener(touchListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return icons==null?0:icons.size();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(draging){
            if(positionOffset==0&&position==0&&currentPostion==position){
                leftEnd=true;
            }else{
                leftEnd=false;
            }
            if(position==icons.size()-1&&positionOffset==0&&currentPostion==position){
                rightEnd=true;
            }else {
                rightEnd=false;
            }
            System.out.println(position+positionOffset+positionOffsetPixels+"%"+leftEnd+"&"+rightEnd);
        }
    }

    @Override
    public void onPageSelected(int position) {
        currentPostion=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        System.out.println("state#"+state);
        if(state==1){
            draging=true;
        }else {
            draging=false;
        }
        if(state==0&&rightEnd){
            pager.setCurrentItem(0);
            rightEnd=false;
        }else if(state==0&&leftEnd){
            pager.setCurrentItem(icons.size()-1);
            leftEnd=false;
        }
    }

    public void addPlaceHolder(){
        if(icons.size()<6){
            icons.add(UserIconFragment.PLACE_HOLDER);
            notifyDataSetChanged();
        }
    }


}
