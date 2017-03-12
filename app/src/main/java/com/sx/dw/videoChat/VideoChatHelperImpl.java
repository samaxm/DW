package com.sx.dw.videoChat;

import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.accountAndSecurity.entity.BaseDisplayUserInfo;
import com.sx.dw.core.AppConfig;
import com.sx.dw.core.GlobalData;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.im.entity.LinkMan;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.inChatRoom;
import static com.sx.dw.videoChat.VideoChatActivity.LIKED;
import static com.sx.dw.videoChat.VideoChatActivity.UNLIKED;

/**
 * 视频聊天的帮助类，包含启动匹配（回调）、发送在线统计、举报、喜欢收藏等方法
 */

public class VideoChatHelperImpl implements VideoChatHelper {

    private RecyclerView rvJokeList;
    private JokeListAdapter jokeListAdapter;
    private List<String> jokes;

    private VideoChatApi videoChatApi;
    private Handler jokeLoopHandler;
    private Runnable jokeTimeRunnable;
    private Handler matchLoopHandler;
    private Runnable matchTimeRunnable;
    private Handler pingLoopHandler;
    private Runnable pingTimeRunnable;

    private MatchCallback callback;
    private int requestJokeIndex = 0;
    private int jokeIndex = 0;
    //    是否优先，第一次匹配不优先，第二次开始优先
    private boolean isPrioritized = false;


    public VideoChatHelperImpl(VideoChatActivity context, RecyclerView rvJokeList, MatchCallback callback) {
        this.callback = callback;
        videoChatApi = DwRetrofit.getInstance().createApi(VideoChatApi.class);
        this.rvJokeList = rvJokeList;
        jokes = new ArrayList<>();
        jokeListAdapter = new JokeListAdapter(context);
        rvJokeList.setLayoutManager(new LinearLayoutManager(context));
        rvJokeList.setItemAnimator(new DefaultItemAnimator());
        rvJokeList.setAdapter(jokeListAdapter);
    }

    @Override
    public void startMatch(boolean starJoke) {
        if (matchLoopHandler == null) {
            matchLoopHandler = new Handler();
            matchTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    if (GlobalData.inChatRoom) {
                        match(isPrioritized);
//                  在这里设定下一次执行，即形成循环
                        matchLoopHandler.postDelayed(this, AppConfig.INTERVAL_MATCH);
                    }
                }
            };
        }
        // 先取消一次
        matchLoopHandler.removeCallbacks(matchTimeRunnable);
//      启动计时器，5秒后执行
        // FIXME: 2016/11/22 调试一番
        matchLoopHandler.postDelayed(matchTimeRunnable, 5000);
        if(starJoke)
            startJoke();
    }

    @Override
    public void stopMatch() {
        if(matchLoopHandler!=null) {
            matchLoopHandler.removeCallbacks(matchTimeRunnable);
        }
        stopJoke();
    }

    @Override
    public void removeMatch() {

        videoChatApi.removeMatch(accountInfo.getId(), accountInfo.getIcon(), accountInfo.getName(),accountInfo.getSign(),accountInfo.getTag(), true).enqueue(new DwCallback<EntityHead>() {
            @Override
            public void getBody(Call call, EntityHead body) {
                LogUtils.d(body);
            }
        });
    }

    @Override
    public void startPing() {
        if (pingLoopHandler == null) {
            pingLoopHandler = new Handler();
            pingTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    videoChatApi.ping(accountInfo.getId(), "").enqueue(new DwCallback<ResponseBody>() {
                        @Override
                        public void getBody(Call call, ResponseBody body) {

                        }
                    });
//                  在这里设定下一次执行，即形成循环
                    pingLoopHandler.postDelayed(this, AppConfig.INTERVAL_PING);
                }
            };
        }
        // 先取消一次
        pingLoopHandler.removeCallbacks(pingTimeRunnable);
//      启动计时器，马上执行
        pingLoopHandler.postDelayed(pingTimeRunnable, 0);
    }

    @Override
    public void stopPing() {
        pingLoopHandler.removeCallbacks(pingTimeRunnable);
    }

    @Override
    public void report(String dwId) {
        videoChatApi.reportUser(accountInfo.getId(), dwId, "快捷举报").enqueue(new DwCallback<EntityHead>() {

            @Override
            public void getBody(Call call, EntityHead body) {
                if (body.isSuccess()) {
                    ToastUtil.showToast("举报成功");
                } else {
                    ToastUtil.showToast(body.getMsg());
                }
            }
        });
    }

    @Override
    public void like(final String likedId, final TextView ivLikeUser) {
//        ivLikeUser.setImageResource(R.drawable.action_icon_like_done_x3);
        videoChatApi.likeUser(accountInfo.getId(), likedId).enqueue(new DwCallback<EntityHead<BaseDisplayUserInfo>>() {
            @Override
            public void getBody(Call call, EntityHead<BaseDisplayUserInfo> body) {
                if (body.isSuccess()) {
                    //进行数据保存
                    BaseDisplayUserInfo info=body.getData();
                    if(info!=null&&info.getDwID()!=null) {
                        LinkMan man = info.toLinkMan();
                        man.setLike(true);
                        if (GlobalData.linkManMap.get(info.getDwID())!=null)
                            GlobalData.linkManMap.get(info.getDwID()).update(man);
                        else
                            man.save();
                        GlobalData.updateLinkMans();
                    }
                    ToastUtil.showToast("收藏成功！");
                    ivLikeUser.setText(LIKED);

                } else {
                    ToastUtil.showToast(body.getMsg());
                    ivLikeUser.setText(UNLIKED);
                }
            }

            @Override
            public void onFailure(Call<EntityHead<BaseDisplayUserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                ivLikeUser.setText(UNLIKED);
            }
        });
    }

    @Override
    public void like(final String likedId, final MenuItem item) {
//        先设为true 以免用户以为没点到
        item.setChecked(true);
        videoChatApi.likeUser(accountInfo.getId(), likedId).enqueue(new DwCallback<EntityHead<BaseDisplayUserInfo>>() {
            @Override
            public void getBody(Call call, EntityHead body) {
                if (body.isSuccess()) {
                    ToastUtil.showToast("收藏成功！");
                } else {
                    ToastUtil.showToast(body.getMsg());
                    item.setChecked(false);
                }
            }

            @Override
            public void onFailure(Call<EntityHead<BaseDisplayUserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                item.setChecked(false);
            }
        });
    }

    @Override
    public void setIsPrioritized(boolean isPrioritized) {
        this.isPrioritized = isPrioritized;
    }

//    private void matchWithResponseBody(boolean isPrioritized) {
//        LinkMan linkMan = accountInfo.toLinkMan();
//        Call<ResponseBody> call;
//        call = videoChatApi.matchLinkManV2WithResponseBody(linkMan.getDwID(), linkMan.getIcon(), linkMan.getName(), linkMan.getSign(), isPrioritized);
//        LogUtils.v(call);
//        call.enqueue(new DwCallback<ResponseBody>() {
//            @Override
//            public void getBody(Call call, ResponseBody body) {
//                try {
//                    String jsonString = body.string();
//                    LogUtils.v(jsonString);
//                    JSONObject object = JSON.parseObject(jsonString);
//                    if (object == null) {
//                        callback.onFailure("您的IP请求过于频繁，休息一下吧！");
//                        return;
//                    }
//                    if (object.getInteger("statusCode") == 10000) {
//                        JSONObject jsonObject = object.getJSONObject("data").getJSONObject("matchID");
//                        LinkMan linkMan = JSON.parseObject(jsonObject.toJSONString(), LinkMan.class);
//                        callback.onSuccess(linkMan);
//                    } else {
//                        String msg = object.getString("msg");
//                        setIsPrioritized(true);
//                        callback.onMatching();
//                        LogUtils.d(msg);
//                        if (!"搜索用户中...".equals(msg)) {
//                            ToastUtil.showToast(msg);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                callback.onFailure(t.getMessage());
//            }
//        });
//    }

    private void match(boolean isPrioritized) {
        LinkMan linkMan = accountInfo.toLinkMan();
        videoChatApi.matchLinkManV2(linkMan.getDwID(),
                linkMan.getIcon(),
                linkMan.getName(),
                linkMan.getSign(),
                linkMan.getTag(),
                isPrioritized)
                .enqueue(new DwCallback<EntityHead<MatchResultEntity>>() {
                    @Override
                    public void getBody(Call call, EntityHead<MatchResultEntity> body) {
                        if (body.isSuccess()) {
                            callback.onSuccess(body.getData().getMatchID());
                        } else {
                            callback.onMatching();
                            setIsPrioritized(true);
                            String msg = body.getMsg();
                            if (!"搜索用户中...".equals(msg)) {
                                ToastUtil.showToast(msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EntityHead<MatchResultEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        callback.onFailure(t.getMessage());
                    }
                });
    }

    private void startJoke() {
        videoChatApi.getJokes(requestJokeIndex).enqueue(new DwCallback<EntityHead<List<String>>>() {
            @Override
            public void getBody(Call call, EntityHead<List<String>> body) {
                if (body.isSuccess()) {
                    jokes.addAll(body.getData());
                    rvJokeList.setVisibility(View.VISIBLE);
                    showJoke();
                } else {
                    ToastUtil.showToast(body.getMsg());
                }
            }
        });
        requestJokeIndex++;
    }


    public void stopJoke() {
        if (jokeLoopHandler != null) {
            jokeLoopHandler.removeCallbacks(jokeTimeRunnable);
        }
        jokes.clear();
        jokeListAdapter.clear();
        jokeIndex = 0;
        rvJokeList.setVisibility(View.INVISIBLE);
    }

    private void showJoke() {
        if (jokeLoopHandler == null) {
            jokeLoopHandler = new Handler();
            jokeTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("showJoke ");
                    if (!inChatRoom) {
                        return;
                    }
                    if (jokeIndex < jokes.size()) {
                        jokeListAdapter.addJoke(jokes.get(jokeIndex));
                        rvJokeList.scrollToPosition(jokeListAdapter.getItemCount() - 1);
                        jokeIndex++;
                        jokeLoopHandler.postDelayed(this, AppConfig.INTERVAL_JOKE_SHOW);
                    } else {
                        startJoke();
                    }
                }
            };
        }
        jokeLoopHandler.removeCallbacks(jokeTimeRunnable);
//           启动计时器，马上执行
        jokeLoopHandler.postDelayed(jokeTimeRunnable, AppConfig.INTERVAL_JOKE_SHOW);
    }

    public interface MatchCallback {
        void onSuccess(LinkMan linkMan);

        void onMatching();

        void onFailure(String msgToUser);
    }
}
