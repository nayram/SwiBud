package com.dev.swibud.pojo;

import java.util.Calendar;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by nayrammensah on 12/1/17.
 */

public class Chat extends RealmObject {

    private int from_id;
    private int to_id;
    private long created_at, updated_at;
    private String message;

    private RealmList<Chat> allChats;

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RealmList<Chat> getAllChats() {
        return allChats;
    }

    public void setAllChats(RealmList<Chat> allChats) {
        this.allChats = allChats;
    }
}
