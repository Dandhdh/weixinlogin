package com.danyy.controller;

import com.danyy.util.*;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/Weixin")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.secret}")
    private String secret;
    @Value("${weixin.login}")
    private String weixinLogin;

    private static String accesstoken ;

    //请求code接口，将响应直接返回给客户端，客户端会变成授权页面
    private String weixinUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
    		+ "appid=%s&"
    		+ "redirect_uri=%s&"
    		+ "response_type=code&"
    		+ "scope=snsapi_base"
    		+ "&state=abc&"
    		+ "connect_redirect=1#wechat_redirect";


    /*
     * 1.获取授权页面
     */
    @RequestMapping("/auth.do")
    public void auth(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException,IOException{

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        System.out.println("auth.do");

        //回调地址
        String callBack = "http://17656f9g43.iok.la/Weixin/wx.do";
        //@SuppressWarnings("deprecation")
        //String codeCallBack = URLEncoder.encode(callBack);
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid="+appid +
                "&redirect_uri="+callBack +
                "&response_type=snsapi_userinfo"+
                "&scope=snsapi_userinfo" +
                "&state=STATE#wechat_redirect";


        //通过连接重定向请求code，即授权
        resp.sendRedirect(url);
    }


    /**
     * 2.回调页面
     * 通过授权页面后，客户端直接发送请求给微信服务器，
     * 此时微信服务器将响应返回给回调地址，获取到相应的code
     */
    //回调地址，获取access_token
    @RequestMapping("/wx.do")
    public void getCode(HttpServletRequest request,HttpServletResponse response) throws IOException {

        System.out.println("wx.do");
        String code = request.getParameter("code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                + "appid="+appid
                + "&secret="+secret
                + "&code="+code
                + "&grant_type=authorization_code";

        //直接发送请求给微信服务器
        //response.sendRedirect(url);

        //通过工具类向微信发送get请求，并返回result，获取其中的access_token
        JSONObject result = AuthUtil.doGet(url);
        System.out.println("result:"+result);

        /*
         * 遍历JSONObject
         */
        Iterator<String> keys = result.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = result.getString(key);
            System.out.println(key+" : "+value);
        }

        response.setHeader("access_token",(String)result.get("access_token"));
        response.setHeader("openid",(String) result.get("openid"));
        accesstoken = (String)result.get("access_token");
        //redirect(request,response,null);
    }


    //3.获取用户信息
    @RequestMapping("/userInfo.do")
    public void getUserInfo(HttpServletRequest request,HttpServletResponse response) throws IOException {

        System.out.println("/userInfo");

        String access_token = request.getHeader("access_token");
        //String openid = request.getHeader("openid");

        System.out.println(access_token);
        System.out.println(accesstoken);
        access_token = accesstoken;

//        String access_token = "8_YG75sevIRc8LEZY6VvVyXcuTDWDIGcXUsj00D7sjACkw30qM9B4c2KzFUkJHJhfMpGv8McBfYDdphU_0FdutcAqWvuGxhx9OaVaiDC7ouH-AkFuBazd5T_SedYINKFjAIAFCX";
//        String openid = "oz1_Y09DZhbnveqAAvFUoeYBhKzw";

        String url = "https://api.weixin.qq.com/sns/userinfo?"
                + "access_token="+access_token
                + "&openid=oz1_Y09DZhbnveqAAvFUoeYBhKzw"
                + "&lang=zh_CN";

        //response.sendRedirect(url);
        //JSONObject result = AuthUtil.doGet(url);
        JSONObject result = CommonUtil.httpsRequest(url,"GET",null);
        System.out.println("result:"+result);

        /*
         * 遍历JSONObject
         */
        Iterator<String> keys = result.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = result.getString(key);
            System.out.println(key+" : "+value);
        }
    }

    //获取全局token
    @RequestMapping("/token.do")
    public void token(HttpServletRequest request,HttpServletResponse response, String redirect) throws IOException {

        String url = "https://api.weixin.qq.com/cgi-bin/token?" +
                "grant_type=client_credential" +
                "&appid=" +appid+
                "&secret="+secret;

        JSONObject result = AuthUtil.doGet(url);

        System.out.println("全局token："+result.get("access_token"));
        System.out.println("有效时间（s）："+result.get("expires_in"));
    }


    //4.刷新token
    @RequestMapping("/refreshToken.do")
    public void refresh_token(HttpServletRequest request,HttpServletResponse response, String redirect) throws IOException {
        //填写通过access_token获取到的refresh_token参数
        String REFRESH_TOKEN = "8_WIH3jOCVVtqtkU4_OFB-Oul-ahlMdzKgaEy9RIBI0N7KpjQFExW0CkRZb_x95BloxE99JOTfi1_wSQUV-FJfYXXFysWGk2bJsnQnwGITlDQ";
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                "appid=" +appid+
                "&grant_type=refresh_token" +
                "&refresh_token="+REFRESH_TOKEN;

        JSONObject result = AuthUtil.doGet(url);

        System.out.println("新的token："+result.get("access_token"));
        System.out.println("有效时间（s）："+result.get("expires_in"));
        System.out.println("刷新refresh_token："+result.get("refresh_token"));
        System.out.println("用户唯一标识："+result.get("openid"));
    }

    @RequestMapping("/login.do")
    public void login(HttpServletRequest request,HttpServletResponse response, String redirect) throws IOException {
        redirect = new String(Base64Utils.decodeFromUrlSafeString(redirect),"utf-8");
        request.getSession().setAttribute("weixin_redirect",redirect);
        response.sendRedirect(String.format(weixinUrl,appid, URLEncoder.encode(weixinLogin,"utf-8")));
    }
    
    /*
     * 获取公众号的acess_token
     */
    @RequestMapping("/aouth.do")
    public void aouth(HttpServletRequest request,HttpServletResponse response, String redirect) throws IOException {
        String weixinAouth = "https://api.weixin.qq.com/cgi-bin/token?"
        		+ "grant_type=client_credential&"
        		+ "appid=wxfa138f6ffa650fce&"
        		+ "secret=ca342205c7b9cbf873e7a04230c85626";
        //response.sendRedirect(weixinAouth);
        JSONObject result = AuthUtil.doGet(weixinAouth);
        System.out.println(result);
        
        String access_token = result.getString("access_token");
        String expires_in = result.getString("expires_in");
        System.out.println("access_token："+access_token);
        System.out.println("expires_in："+expires_in);
        
        /*
         * 获取tolen后，查看公众号的用户列表
         */
        String weixinGetUser = "https://api.weixin.qq.com/cgi-bin/user/get?"
        		+ "access_token=ACCESS_TOKEN&"
        		+ "next_openid=NEXT_OPENID";
        
        String weixinGetUser2 = weixinGetUser.replace("ACCESS_TOKEN", access_token);
        JSONObject userInfo = AuthUtil.doGet(weixinGetUser2);
        System.out.println("userInfo:"+userInfo);
    }


    @RequestMapping("/aauth.do")
    public void aauth(HttpServletRequest request,HttpServletResponse response) throws IOException {
    	String authorization = "https://open.weixin.qq.com/connect/oauth2/authorize?"
    			+ "appid=wx633e4285fbacd3df&"
    			+ "redirect_uri=http://17656f9g43.iok.la/Weixin/wx.do&"
    			+ "response_type=code&"
    			+ "scope=snsapi_base&"
    			+ "state=ok#wechat_redirect";
    	//response.sendRedirect(authorization);
    	/*
    	 * snsapi_base 不需要用户点同意，直接跳转到授权后的页面，只能用于获取openid，不能获取用户基本信息
           snsapi_userinfo 会征求用户同意，授权后，可以获取用户基本信息
    	 */
    	JSONObject result = AuthUtil.doGet(authorization);
        System.out.println(result);
    }
  

    @RequestMapping("/redirect.do")
    public void redirect(HttpServletRequest request, HttpServletResponse response, @RequestParam String code) throws IOException {
        SnsToken snsToken = SnsAPI.oauth2AccessToken(appid,secret,code);
        logger.info("login>>snsToken:" + snsToken.getAccess_token());
        logger.info("login>>openid:" + snsToken.getOpenid());
        String redirect = (String) request.getSession().getAttribute("weixin_redirect");
        logger.info("login>>redirect:" + redirect);
        StringBuilder sb = new StringBuilder(redirect);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openid",snsToken.getOpenid());
        jsonObject.put("accessToken",snsToken.getAccess_token());
        String weixinData = AESUtil.encrypt(jsonObject.toString());

        if(redirect.contains("?")) {
            sb.append("&weixinData=");
            sb.append(weixinData);
        } else {
            sb.append("?weixinData=");
            sb.append(weixinData);
        }
        logger.info("login>>redirect:" + jsonObject.toString());
        response.sendRedirect(sb.toString());
    }

}
