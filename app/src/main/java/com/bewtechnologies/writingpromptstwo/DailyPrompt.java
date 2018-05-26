package com.bewtechnologies.writingpromptstwo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ab on 14/02/18.
 */

class DailyPrompt {

    public String content;

    public DailyPrompt()
    {

    }

    public DailyPrompt(String content)
    {
        this.content=content;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("content", content);

        return result;
    }

}
