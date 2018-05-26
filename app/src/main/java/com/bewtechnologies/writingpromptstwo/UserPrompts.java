package com.bewtechnologies.writingpromptstwo;

import android.text.format.DateUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ab on 06/04/18.
 */

public class UserPrompts {

    int upvotes;
    HashMap<String,Object> time;


    String userName, userID, userPrompt,userImageURL;

    Object genre;

    boolean isDeleted,isPending,isApproved,isReported;



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("upvotes",upvotes);
        result.put("userName", userName);
        result.put("userID", userID);
        result.put("userPrompt", userPrompt);
        result.put("genre", genre);
        result.put("time",time);
        result.put("isDeleted", isDeleted);
        result.put("isPending", isPending);
        result.put("isApproved", isApproved);
        result.put("isReported", isReported);
        result.put("userImageURL",userImageURL);




        return result;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public Object getGenre() {
        return genre;
    }

    public void setGenre(Object genre) {
        this.genre = genre;
    }

    public boolean getisReported() {
        return isReported;
    }

    public void setisReported(boolean reported) {
        isReported = reported;
    }



    public boolean getisDeleted() {
        return isDeleted;
    }

    public void setisDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    public HashMap<String, Object> getTime() {
        return time;
    }

    public void setTime(HashMap<String, Object> time) {
        this.time = time;
    }


    public boolean getisApproved() {
        return isApproved;
    }

    public void setisApproved(boolean approved) {
        isApproved = approved;
    }

   /* public boolean setisApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }*/

    public boolean getisPending() {
        return isPending;
    }

    public void setisPending(boolean pending) {
        isPending = pending;
    }





    @Exclude
    public long getTimeValue(){
        return (long)time.get("time");
    }

    @Exclude
    public String getTimeDifference(UserPrompts userPrompt){
        /*SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date= sfd.format(new Date(userPrompt.getTimeValue()));*/

        long promptTimeConvertedToPositive = (long)-1 * (long)userPrompt.getTimeValue();

        String difference= DateUtils.getRelativeTimeSpanString(promptTimeConvertedToPositive,System.currentTimeMillis(), 0).toString();

        if(!difference.equals(""))
        {
            return difference;
        }
        else
        {
            difference= "some time ago";
            return difference;
        }

    }
}
