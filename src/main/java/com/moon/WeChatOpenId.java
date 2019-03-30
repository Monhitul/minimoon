package com.moon;

import com.moon.util.AesCbcUtil;
import com.moon.util.HttpRequest;
import net.sf.json.JSONObject;

import java.util.HashMap;

/**
 * 获取OpenID入口
 * create by Monhitul on 2019/3/30
 */
public class WeChatOpenId {

    private String code;    //临时登录凭证

    private String url = "https://api.weixin.qq.com/sns/jscode2session";    //微信服务器接口地址

    private String wxAppid; //小程序唯一标识

    private String wxSecret;    //小程序的 app secret

    private String grant_type = "authorization_code";       //授权

    private String encryptedData;      //包含 openId，unionId 等用户敏感数据的加密数据

    private String iv;  //加密算法的初始向量

    public WeChatOpenId(String wxAppid, String wxSecret) {
        this.wxAppid = wxAppid;
        this.wxSecret = wxSecret;
    }

    public WeChatOpenId(String wxAppid, String wxSecret, String code, String encryptedData, String iv) {
        this.code = code;
        this.wxAppid = wxAppid;
        this.wxSecret = wxSecret;
        this.encryptedData = encryptedData;
        this.iv = iv;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    /**
     * 获取信息
     * @return
     */
    public HashMap getUserInfo() {

        HashMap map = new HashMap();
        /**
         * 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid
         */
        //请求参数
        String params = "appid=" + wxAppid + "&secret=" + wxSecret + "&js_code=" + code + "&grant_type=" + grant_type;
        //发送请求
        String sr = HttpRequest.sendGet(url, params);
        //解析相应内容（转换成json对象）
        JSONObject json = JSONObject.fromObject(sr);
        //获取会话密钥（session_key），用于解密EncryptedData
        String session_key = json.get("session_key").toString();
        //用户的唯一标识（openid）
        String openid = (String)json.get("openid");

        /**
         * 2、对encryptedData加密数据进行AES解密
         */
        try {
            String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
            if (null != result && result.length() > 0) {
                map.put("success", 1);

                JSONObject userInfoJSON = JSONObject.fromObject(result);
                HashMap userInfo = new HashMap();
                userInfo.put("openId", userInfoJSON.get("openId"));
                userInfo.put("nickName", userInfoJSON.get("nickName"));
                userInfo.put("gender", userInfoJSON.get("gender"));
                userInfo.put("city", userInfoJSON.get("city"));
                userInfo.put("province", userInfoJSON.get("province"));
                userInfo.put("country", userInfoJSON.get("country"));
                userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
                userInfo.put("unionId", userInfoJSON.get("unionId"));
                map.put("userInfo", userInfo);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("success", 0);
        return map;
    }
}


