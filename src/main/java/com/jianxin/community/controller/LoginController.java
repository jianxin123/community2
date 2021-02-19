package com.jianxin.community.controller;
import com.google.code.kaptcha.Producer;
import com.jianxin.community.entity.User;
import com.jianxin.community.service.UserService;
import com.jianxin.community.util.CommunityConstant;
import com.jianxin.community.util.CommunityUtil;
import com.jianxin.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//实现接口 方便引用常量
@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    //作为cookie的有效域 这里为全局
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //注册是浏览器提交数据用post请求
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user) throws MessagingException {
        Map<String,Object> map = userService.register(user);
        if(map==null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    //@PathVariable 从路径中获取信息
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的帐号已经可以正常使用了！");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活过了！");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您提供的激活码不正确！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    //向浏览器输出的是图片不是一个字符串也不是一个网页 所以用response对象手动输出
    //生成验证码后服务端需要把他记住 不能存在浏览器端 容易盗取 所以用session
    @RequestMapping(path = "kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*,HttpSession session*/){
        //生成验证码  根据之前properties.setProperty里设置的生成
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        //session.setAttribute("kaptcha",text);

        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        //凭证要发给客户端 客户端用cookie保存
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);//有效时间60s
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);//有效时间60s

        //将图片输出给浏览器 告诉浏览器返回的是什么类型的数据 是image 格式为png
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }
    //路径可以相同 此时请求方式必须不同  这次需要把ticket等信息添加到数据库 所以用post
    //登录界面上传的信息 有用户名 密码 验证码 是否记住我那个打勾
    //验证码之前放到了session 参数需要session取出来
    //登录成功后把ticket发放到客户端保存 用cookie 需要response对象

    //从redis取验证码需要key  key又需要kaptchaOwner  kaptchaOwner从cookie中取  @CookieValue("kaptchaOwner")
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberme,
                        Model model, /*HttpSession session,*/HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner){
        //检查验证码 表现层直接处理
        //String kaptcha = (String)session.getAttribute("kaptcha"); //取出的是object对象 需要强转成string
        String kaptcha = null;
        if(StringUtils.isNoneBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)|| !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        //检查账号 密码 业务层处理
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());//map.get返回的是object 需要toString变为string
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);;
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));//如果不是名字有问题map.get返回null
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){ //取网页中字段为ticket的cookie 赋值给String ticket
        userService.logout(ticket);
        return "redirect:/login";  //有两个login 一个get 一个post重定向默认到get
    }
}
