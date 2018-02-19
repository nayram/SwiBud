package com.dev.swibud.pojo;

import java.util.ArrayList;

/**
 * Created by nayrammensah on 2/18/18.
 */

public class RootData {

    public Data data;
    public Body notification;
    public ArrayList<String> registration_id;

    public RootData(Data data, Body body,ArrayList<String> regTokens) {
        this.data = data;
        this.notification = body;
        this.registration_id=regTokens;
    }


}
