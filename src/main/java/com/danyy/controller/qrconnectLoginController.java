package com.danyy.controller;

import com.alibaba.fastjson.JSON;
import com.danyy.pojo.ResponseObject;
import com.danyy.pojo.WeixinQRCode;
import com.danyy.thread.TokenThread;
import com.danyy.util.SceneCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/qrconnect")
public class qrconnectLoginController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.secret}")
    private String secret;
    @Value("${weixin.login}")
    private String weixinLogin;

    //请求code接口，将响应直接返回给客户端，客户端会变成授权页面
    private String weixinUrl = "https://open.weixin.qq.com/connect/qrconnect?" +
            "appid=APPID" +
            "&redirect_uri=REDIRECT_URI" +
            "&response_type=code" +
            "&scope=SCOPE" +
            "&state=STATE#wechat_redirect";

    /*
     * 1.获取授权页面
     */
    @RequestMapping("/auth.do")
    public void auth(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException,IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        //回调地址
        //@SuppressWarnings("deprecation")
        //String codeCallBack = URLEncoder.encode(callBack);
        String qrConnentUrl = weixinUrl.replaceAll("APPID",appid)
                .replaceAll("REDIRECT_URI",weixinLogin)
                .replaceAll("SCOPE","snsapi_userinfo")
                .replaceAll("STATE","3d6be0a4035d839573b04816624a415e");

        System.out.println(qrConnentUrl);
        //通过连接重定向请求code，即授权
        resp.sendRedirect(qrConnentUrl);
    }



}
