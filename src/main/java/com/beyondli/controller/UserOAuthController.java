package com.beyondli.controller;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by beyondLi
 * Date 2018/5/10 14:42
 * Desc .用户授权controller
 */
@RestController
@RequestMapping(value = "/wechat")
public class UserOAuthController {
    @Resource
    private WxMpService wxMpService;
    @RequestMapping(value = "/hello")
    public String hello() {
        return "hello";
    }

    /**
     * oauth2授权
     * 前置要求:1.订阅号无法实现次功能,需要服务号以上才有此权限
     *          2.需要在后台设置域名
     *              即：获取code的时候请求的url(https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect)中的redirect_uri值的域名
     * 流程描述:
     *          前端:
     *              1.生成url https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxaffc76cfda3d9eb5&redirect_uri=http%3a%2f%2f58.87.75.119%3a8080%2fwechat%2fget%2fuser%2finfo&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect
     *          后台:
     *              1.通过访问https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect来获取code,并将redirect_uri设置为对应后台接口来接收code值
     *              2.通过code值访问对应url来获取access_token(此access_token与开发者受限的access_token值不同,并不受次数限制)以及openId值
     *              3.通过access_token和openId获取用户信息
     * @param code
     * @param state
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "/get/user/info")
    public String getUserInfoByCode(@RequestParam(value = "code")String code, @RequestParam(value = "state")String state) throws WxErrorException {
        System.out.println(code);
        System.out.println(state);
        //通过code获取access_token
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        //通过access_token获取openId
        //String openId = wxMpOAuth2AccessToken.getOpenId();
        //通过openId获取用户信息
        WxMpUser userInfo = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
        return "openId:" + userInfo.getOpenId() + ",昵称:" + userInfo.getNickname()+ ",性别:" + userInfo.getSex()+ ",county:" + userInfo.getCountry()+ ",province" + userInfo.getProvince()+ ",city" + userInfo.getCity();
    }
}
