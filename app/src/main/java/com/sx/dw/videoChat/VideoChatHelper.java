package com.sx.dw.videoChat;

import android.view.MenuItem;
import android.widget.TextView;

/**
 * @Description: 视频聊天帮助类接口
 * @author: fanjie
 * @date: 2016/9/26
 */

public interface VideoChatHelper {
    void startMatch(boolean starJoke);
    void stopMatch();
    void removeMatch();

    void startPing();
    void stopPing();

    void report(String dwId);
    void like(String likedId, MenuItem item);
    void like(String likedId, TextView item);

    void setIsPrioritized (boolean isPrioritized);
}
