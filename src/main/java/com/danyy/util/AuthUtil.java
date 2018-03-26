package com.danyy.util;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/*
 * 接受Get请求返回参数的工具类
 */
public class AuthUtil {

    public static JSONObject doGet(String url) throws IOException{
        JSONObject jsonObject = null;
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if(entity!=null){
            String result = EntityUtils.toString(entity,"utf-8");
            jsonObject = JSONObject.fromObject(result);
        }
        httpGet.releaseConnection();
        return  jsonObject;
    }

}
