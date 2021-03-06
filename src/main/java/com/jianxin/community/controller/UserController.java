package com.jianxin.community.controller;

import com.jianxin.community.annotation.LoginRequired;
import com.jianxin.community.entity.User;
import com.jianxin.community.service.FollowService;
import com.jianxin.community.service.LikeService;
import com.jianxin.community.service.UserService;
import com.jianxin.community.util.CommunityConstant;
import com.jianxin.community.util.CommunityUtil;
import com.jianxin.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    //上传路径
    @Value("${community.path.upload}")
    private String uploadPath;
    //域名
    @Value("${community.path.domain}")
    private String domain;
    //项目访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;
    //取当前用户
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/reset",method = RequestMethod.POST)
    public String changePassword(String oldPassword,String newPassword,String newPassword2,Model model){
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!user.getPassword().equals(oldPassword)){
            model.addAttribute("error1","原密码不正确！");
            return "/site/setting";
        }
        if(newPassword == null){
            model.addAttribute("error2","请输入新密码！");
            return "/site/setting";
        }
        if(!newPassword.equals(newPassword2)){
            model.addAttribute("error3","两次输入的密码不一致！");
            return "/site/setting";
        }
        newPassword=CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(),newPassword);
        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();//获取上传图片的名字 从名字中截取后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".")); //名字的最后一个.开始截取 得到后缀
            if(StringUtils.isBlank(suffix)){
                model.addAttribute("error","文件格式不正确！");
                return "/site/setting";
            }
            //生成随机文件名
            fileName = CommunityUtil.generateUUID() + suffix;
            //确定文件存放路径
            File dest = new File(uploadPath + "/" +fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败： " + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }
        //更新当前用户头像的路径(web访问路径)
        //http://localhost:8080/jianxin/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try(    //这个小括号里的变量最后会自动加到finally close  前提有close方法
                FileInputStream fis = new FileInputStream(fileName);//文件输入流接收
                OutputStream os = response.getOutputStream();//图片是二进制文件 获取字节输出流
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;//游标
            while((b=fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        //用户基本信息
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!= null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }



}
