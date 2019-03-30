# MiniMoon
![MiniMoon](images/logo.png)

## 项目简介
Java后端各种小工具的集合，简便开发。

## 部署步骤

#### jar包导入方式
下载jar包并自行导入，这是[下载地址](jar/minimoon-1.0.jar)。

#### maven依赖方式
（待发布）

## 使用说明

* [获取微信小程序OpenId及登录用户公开信息](#获取微信小程序OpenId及登录用户公开信息)

#### 获取微信小程序OpenId及登录用户公开信息
1. 前端需向后端传递``encryptedData``、``iv``、``code``。其中``encryptedData``为加密的用户信息，包括昵称、性别等；``iv``为加密算法的初始向量；``code``为用户登录的临时凭证，五分钟内有效。获取方法可查看微信小程序官方文档``wx.login()``部分。
2. 后端需导入相关包。主体类为``WeChatOpenId``，其提供了两个构造方法，为
    ```java
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
    ```
    其中，``encryptedData``、``iv``、``code``由前端传递，``wxAppid``、``wxSecret``分别为小程序唯一识别号和小程序密钥，可在自己的小程序后台看到。
    选择第一种构造方法的也可以使用setter方法完善参数：
    ```java
    public void setCode(String code) {
        this.code = code;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
    ```
3. 获取openID及用户信息，通过实例化``WeChatOpenId``并调用``getUserInfo``方法。
    ```java
    HashMap getUserInfo();
    ```
    返回格式为：
    ```
    {
        success : "1",      //1为成功，0位失败
        userInfo : {
            openId : "",
            nickName : "",
            gender : "",
            city : "",
            province : "",
            country : "",
            avatarUrl : "",
            unionId : ""        //此为 null
        }
    }
    ```
4. 使用示例：
    ```java
    import com.moon.WeChatOpenId;
    
    public class openController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String encryptedData = req.getParameter("encryptedData");
        String iv = req.getParameter("iv");
        String code = req.getParameter("code");

        String wxAppid = "xxxxxxxxxxxxx";
        String wxSecret = "yyyyyyyyyyyyyyyy";
        
        WeChatOpenId weChatOpenId = new WeChatOpenId(wxAppid, wxSecret, code, encryptedData, iv);
        HashMap m = weChatOpenId.getUserInfo();
        System.out.println(m);
    }
    ```
## 版本内容更新
* 2019-03-30  minimoon-1.0  内容：上线，微信小程序获取OpenID及登录用户公开信息，不包括UnionID

## 声明
该项目主要用于学习，我会坚持维护的，毕竟这是我的第一个打算长期维护下去的项目。

但我知识、技术水平仍然有限，所以有不足的地方，也欢迎大家指出，互相交流。

![Monhitul](images/monhitul.png)