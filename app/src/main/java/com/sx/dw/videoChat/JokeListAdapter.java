package com.sx.dw.videoChat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sx.dw.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/23 16:02
 */

public class JokeListAdapter extends RecyclerView.Adapter<JokeListAdapter.JokeViewHolder> {

    private Context context;
    private List<String> jokes;

    public JokeListAdapter(Context context) {
        this.context = context;
        jokes = new ArrayList<>();
//        jokes.add("嗨~又寂寞了么？");
//        jokes.add("先看会儿段子哈，《大我》给你找人，活的，ta愿不愿陪你聊，就看你咯");
//        jokes.add("如果你的相机卡了，点左下角，重新加载相机");
//        jokes.add("对了，此处禁止一言不合就掏鸟，禁止色情，一旦被举报（右上角）直接封号，严重的直接把你打包上交给国家~");
        jokes.add("欢迎来到<大我>，作为全球最有趣的高端社交app，年轻的我们感激您的支持。");
        jokes.add("美好的事，总在偶然发生。随机视频对网络环境要求很高，摄像如果卡顿，点左下角刷新一下。");
        jokes.add("软件可一键换人，所以自己可能会突然被换掉，如果不想错过，请第一时间点击左上角的[收藏ta]。");
        jokes.add("视频中，点右下角的[赏]，将每次打赏对方100“大洋”（每充值1元人民币可获得100“大洋“）");
        jokes.add("右上角进入文字区。对方的每句来信，您将会按照自己“身价”获得收入积分，并可兑奖人民币，汇入微信零钱。 ");
        jokes.add("陌生人的善意，是我们的解药。与直播不同，这里大家都是“主播“，一对一会更有趣更好的保护隐私，但请勿利用软件功能开展色情、欺诈行为。");
        jokes.add("有建议、想合作，请在“放大镜”搜索“Decent world“，直接跟创始人交流。 ");
        jokes.add("接下来，就看您的魅力和耐心值了。");
    }

    public void addJoke(String joke){
        if(!TextUtils.isEmpty(joke)) {
            jokes.add(joke);
            notifyItemInserted(jokes.size() - 1);
        }
    }

    @Override
    public JokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JokeListAdapter.JokeViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_joke, parent, false));
    }

    @Override
    public void onBindViewHolder(JokeViewHolder holder, int position) {
        holder.tvJoke.setText(jokes.get(position));
    }

    @Override
    public int getItemCount() {
        return jokes == null ? 0 : jokes.size();
    }

    public void clear() {
        jokes.clear();
//        LogUtils.d(jokes);
        notifyDataSetChanged();
    }

    class JokeViewHolder extends RecyclerView.ViewHolder {
        TextView tvJoke;
        public JokeViewHolder(View itemView) {
            super(itemView);
            tvJoke = (TextView) itemView.findViewById(R.id.tv_joke_content);
            tvJoke.getBackground().setAlpha(51);
        }
    }
}
