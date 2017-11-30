package com.dev.swibud.pojo;

import java.lang.reflect.Field;

/**
 * Created by nayrammensah on 11/24/17.
 */

public class UserDetails {

     int id,devless_user_id;
     String user_image;
     double longitude,latitude;

    public UserDetails(int id, int devless_user_id, String user_image, double longitude, double latitude) {
        this.id = id;
        this.devless_user_id = devless_user_id;
        this.user_image = user_image;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevless_user_id() {
        return devless_user_id;
    }

    public void setDevless_user_id(int devless_user_id) {
        this.devless_user_id = devless_user_id;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

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
