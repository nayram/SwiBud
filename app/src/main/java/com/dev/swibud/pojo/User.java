package com.dev.swibud.pojo;

import java.lang.reflect.Field;

/**
 * Created by nayrammensah on 12/4/17.
 */

public class User {

    public String user_id,nickname,profile_url="",access_token;
    public boolean issue_access_token,is_active,is_online;
    public long last_seen_at;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = "";

        result.append(((Object) this).getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = ((Object) this).getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append(" ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }

        result.append("}");

        return result.toString();
    }

}
