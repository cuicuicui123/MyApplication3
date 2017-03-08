package com.cui.myapplication;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cui on 2016/8/1.
 */
public class JudgeIsJsonArray {
    public static void judge(JSONObject jsonObject, String key, OnJudged onJudged) throws JSONException {
        if (!jsonObject.isNull(key)){
            if (jsonObject.getString(key).charAt(0) == '[') {//是json数组
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                for (int i = 0;i < jsonArray.length();i ++) {
                    onJudged.judged(jsonArray.getJSONObject(i));
                }
            } else {
                onJudged.judged(jsonObject.getJSONObject(key));
            }
        }
    }

    public interface OnJudged{
        void judged(JSONObject jsonObject) throws JSONException;
    }
}
