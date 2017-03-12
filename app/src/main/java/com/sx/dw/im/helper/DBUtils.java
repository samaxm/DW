package com.sx.dw.im.helper;

import com.activeandroid.query.Select;
import com.sx.dw.im.entity.Chat;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.entity.LinkMan;

import java.util.List;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/13 9:54
 */

public class DBUtils {

    private static DBUtils dbUtils;

    public static DBUtils getInstance() {
        if (dbUtils == null) {
            dbUtils = new DBUtils();
        }
        return dbUtils;
    }

    public List<Chat> getChats() {
        return new Select().from(Chat.class).execute();
    }

    public List<LinkMan> getLinkMans() {
        return new Select().from(LinkMan.class).execute();
    }

    public long getMaxMid() {
        ChatMsg msg = new Select().from(ChatMsg.class).orderBy("mid desc").executeSingle();
        if(msg!=null){
            return msg.getMid();
        }else {
            return 0;
        }

    }
}
