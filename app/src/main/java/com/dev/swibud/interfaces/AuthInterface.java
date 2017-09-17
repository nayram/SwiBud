package com.dev.swibud.interfaces;

/**
 * Created by nayrammensah on 8/2/17.
 */

public interface AuthInterface {
    void addFragment(String uri);
    void removeFragment();
    void addFirstLastName(String fname,String lname);
    void addPhoneNumber(String phone);
    void addPassword(String password);
    void registerCredentials();

}
