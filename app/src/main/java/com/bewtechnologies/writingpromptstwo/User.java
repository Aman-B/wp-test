package com.bewtechnologies.writingpromptstwo;

import java.util.Map;

/**
 * Created by ab on 03/04/18.
 */

public class User {

    String userName;
    String userImageURL;
    String userEmail;



    Map<String, String> UserPrompts;

    //ArrayList <String> UserPrompts = new ArrayList<String>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /*public ArrayList<String> getUserPrompts() {
        return UserPrompts;
    }

    public void setUserPrompts(ArrayList<String> UserPrompts) {
        this.UserPrompts = UserPrompts;
    }
*/

    public Map<String, String> getUserPrompts() {
        return UserPrompts;
    }

    public void setUserPrompts(Map<String, String> userPrompts) {
        this.UserPrompts = userPrompts;
    }
}
