package com.sx.dw.im.entity;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.GlobalData;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/1 15:54
 */

@Table(name = "Chats")
public class Chat extends Model implements Serializable, Comparable<Chat> {

    private LinkMan linkMan;

    @Column(name = "linkManId")
    private String linkManId;

    private long time;

    private List<ChatMsg> chatMsgs;

    public LinkMan getLinkMan() {
        if (linkMan == null) {
            if (getLinkManId() != null) {
                linkMan = GlobalData.linkManMap.get(getLinkManId());
            }
        }
        if (linkMan == null) {
            LogUtils.e(this);//这为空就不正常
        }
        return linkMan;
    }

    public void setLinkMan(LinkMan linkMan) {
        this.linkMan = linkMan;
    }

    public String getLinkManId() {
        return linkManId;
    }

    public String getLinkManIdForSql(){
        return "\""+ getLinkManId()+"\"";
    }

    public void setLinkManId(String linkManId) {
        this.linkManId = linkManId;
    }

    public long getTime() {
        if (getLastMsg() == null) {
            return 0;
        }
        return getLastMsg().getTime();
    }

    public ChatMsg getLastMsg() {
        ChatMsg msg = new Select()
                .from(ChatMsg.class)
                .where("fromID = " + getLinkManIdForSql())
                .or("toID = " + getLinkManIdForSql())
                .orderBy("time desc")
                .executeSingle();
        return msg;
    }

    public boolean isGetAllData() {
        if (chatMsgs != null) {
            int count = new Select().from(ChatMsg.class)
                    .where("fromID = " +getLinkManIdForSql())
                    .or("toID = " +getLinkManIdForSql())
                    .count();
            if (chatMsgs.size() == count) {
                return true;
            }
        }
        return false;
    }



    public List<ChatMsg> getChatMsgs(int index) {
        chatMsgs = new Select()
                .from(ChatMsg.class)
                .where("fromID = " +getLinkManIdForSql())
                .or("toID = " +getLinkManIdForSql())
                .orderBy("time desc")
                .limit(10 * index)
                .offset(0)
                .execute();
        return chatMsgs;
    }

    public List<ChatMsg> getChatMsgs() {
        if (chatMsgs == null && !TextUtils.isEmpty(getLinkManId())) {
            chatMsgs = new Select()
                    .from(ChatMsg.class)
                    .where("fromID = " +getLinkManIdForSql())
                    .or("toID = " +getLinkManIdForSql())
                    .orderBy("time")
                    .execute();
        }
        // FIXME: 2016/10/26 调试下某些机型通过会话列表查不到消息
        if (chatMsgs == null || chatMsgs.size() < 1) {

            LogUtils.d(new Select()
                    .from(ChatMsg.class)
                    .where("fromID = " +getLinkManIdForSql())
                    .or("toID = " +getLinkManIdForSql())
                    .orderBy("time").toSql());
            LogUtils.d(this);
            List<ChatMsg> msgs = new Select().from(ChatMsg.class).execute();
            for (ChatMsg c : msgs) {
                LogUtils.d(c);
            }
        }
        return chatMsgs;
    }

    public void setChatMsgs(List<ChatMsg> chatMsgs) {
        this.chatMsgs = chatMsgs;
    }

    @Override
    public int compareTo(Chat chat) {
        return (int) (this.getTime() - chat.getTime());
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id =" + getId() +
                ", linkManId='" + linkManId + '\'' +
                ", time=" + time +
                ", linkMan=" + linkMan +
                ", chatMsgs=" + chatMsgs +
                '}';
    }

    public String getTimeToShow() {
        long time=getTime();
        if (time == 0) {
            return "";
        }
        Calendar msgTime = Calendar.getInstance();
        msgTime.setTimeInMillis(time);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        String timeToShow;
        String year = DateFormat.format("yyyy-", time).toString();
        String monthDay = DateFormat.format("MM-dd", time).toString();
        String hourMinute = DateFormat.format("HH:mm",time).toString();
        switch (now.get(Calendar.DAY_OF_YEAR) - msgTime.get(Calendar.DAY_OF_YEAR)) {
            case 0: {
                timeToShow = hourMinute;
                break;
            }
            case 1: {
                timeToShow = "昨天 " + hourMinute;
                break;
            }
            case 2: {
                timeToShow = "前天 " + hourMinute;
                break;
            }
            default: {
                if (now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)) {
                    timeToShow = monthDay + " " + hourMinute;
                } else {
                    timeToShow = year + monthDay + " " + hourMinute;
                }
            }
        }
        return timeToShow;
    }

    public void cleanUnread(){
        new Update(ChatMsg.class).set("unreadMsg = ?",false).where("fromID = " +getLinkManIdForSql()).execute();
        new Update(ChatMsg.class).set("unreadMsg = ?",false).where("toID = " +getLinkManIdForSql()).execute();
    }

    public String getUnreadMsgCount() {
        int count = new Select().from(ChatMsg.class)
                .where("fromID = " +getLinkManIdForSql())
                .and("unreadMsg = " + 1)
                .count();
        LogUtils.v(count);
        if (count == 0) {
            return null;
        } else if (count > 99) {
            return "99+";
        } else {
            return String.valueOf(count);
        }

    }
}
