package com.bewtechnologies.writingpromptstwo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ab on 13/11/17.
 */

public class WritingPrompt  {




    public String title;
    public String content;


   /* public  WritingPrompt(String title, String content)
    {
        this.title=title;
        this.content=content;

    }*/

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title",title);
        result.put("content", content);

        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
