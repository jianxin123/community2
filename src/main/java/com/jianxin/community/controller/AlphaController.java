package com.jianxin.community.controller;

import com.jianxin.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//controller处理请求->访问业务层service->访问数据库dao->DB
//先写数据访问层DAO 定义实体类entity 各个成员属性 get set tostring  封装表里的数据
// 然后到dao写mapper接口
//然后到resources里写mapper.xml实现
//然后转到业务层service
//然后基于service写视图层 controller
//最后转到html
//debug   f8从断点处逐行运行   f7跳到当前行的内部调用的方法  f9 跳到下一个断点处


//http是无状态的 即同一个浏览器向服务器发送几个请求 服务器认不出来 当作不相关的几个请求处理
//cookie解决这个问题 浏览器向服务器第一次发送请求时 服务器向浏览器发送cookie 浏览器会保存在本地
//之后发送的请求中都会在请求头携带cookie  服务器以此来识别
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }



    //cookie示例  cookie放在response中
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie 一个cookie存放一对键值对
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效范围 在哪些路径下有效  这么设置路径就是/community/alpha/cookie/set
        cookie.setPath("/community/alpha");
        //cookie默认存在内存里 关掉即消失 设置生效时间会存在硬盘里 长期有效  单位秒
        cookie.setMaxAge(60 * 10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    //cookie返回的时候在request里  如果在参数里声明一个request对象调用getcookie方法 返回的是一个cookie数组
    //获得想要的那个cookie还要遍历
    //用CookieValue  取key为code的cookie赋值给后面的String
    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    /**
     * cookie存在客户端 不是很安全 不能存放敏感信息比如密码 另外每次都发送cookie给服务器会增加数据量 对性能产生影响
     * session应运而生 在服务端记录客户端信息  但是对服务端内存压力大 所以敏感隐私存放在服务端 一般的存在客户端
     * 客户端第一次访问服务端，服务端生成session 但是客户端和服务端是多对一关系 服务端通过cookie辨别保存的sessio是哪个客户端的
     * 即服务端生成sessionId 通过cookie发送给客户端 客户端将cookie保存在本地 下次发送cookie是就携带了sessionId
     * session用于分布式部署的服务器时有些问题 比如浏览器访问代理时（比如nginx）由于负载均衡请求给了服务器A 在A中生成sessionId发给浏览器
     * 但在浏览器第二次发送请求时 由于负载均衡 发送给了服务器C C中此时没有sessionId只能重新建一个发给浏览器
     * 解决办法 改变负载均衡策略 或者设置成黏性session  一个ip发送的请求只分配给一个服务器 但很难保证服务器负载均衡
     * 第二个方法 同步session 某个服务器有了session以后同步给其他服务器 同步对服务器性能产生影响 服务器之间会产生耦合 没那么独立了
     * 第三个方法 共享session 单独设置一个服务器 放session 但是此时其他服务器又依赖于这一个服务器 这台服务器挂了就凉了 分布式变得没有意义
     * 现在大多数还是尽量存在cookie 实在敏感数据存在数据库里  数据库可以做集群备份 但是关系型数据库数据存放在硬盘里 性能不如存在内存里好
     * 现在nosql数据库也已经比较完善了  像这样的数据就可以存放到redis里
     */

    //session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax示例 异步请求不返回网页而是字符串   浏览器提交一个name 一个age
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

    /**
     * 事务管理
     * 第一类丢失更新：某一个事务的回滚，导致另外一个事务已更新的数据丢失
     * 第二类丢失更新：某一个事务的提交，导致另外一个事务已更新的数据丢失
     * 脏读：某一个事务读取了另外一个事务未提交的数据
     * 不可重复读：某一个事务对同一个数据前后读取的结果不一致
     * （事务2在读取一个数据时，事务1对这个数据修改并提交，此时事务2读到的数据与之前不一致）
     * 幻读：某一个事务对同一个表前后查询到的行数不一致
     * （事务2在读取一个表时，事务1对这个表修改（新增数据或删除数据）并提交，此时事务2读到的表与之前不一致）
     *
     * 事务隔离级别（Y表示该级别下会出现该问题）
     * 隔离级别            第一类丢失更新       脏读        第二类丢失更新     不可重复读         幻读
     *
     * Read Uncommitted         Y               Y               Y               Y               Y
     * Read Committed           N               N               Y               Y               Y
     * Repeatable Read          N               N               N               N               Y
     * Serializable             N               N               N               N               N
     *
     * 实现机制
     * 悲观锁（数据库）
     *       -共享锁（S锁）
     *        事务A对某数据加了共享锁后。其他事务只能对该数据加共享锁，但不能加排他锁
     *       -排他锁（X锁）
     *        事务A对某数据加了排他锁后，其他事务对该数据既不能加共享锁，也不能加排他锁
     *乐观锁（自定义）
     *      -版本号、时间戳等
     *       在更新数据前，检查版本号是否发生变化。若变化则取消本次更新，否则就更新数据（版本号+1）
     *
     * Spring事务管理
     *  声明式事务
     *      -通过XML配置，声明某方法的事务特征
     *      -通过注解，声明某方法的事务特征
     *  编程式事务
     *      -通过TransactionTemplate管理事务，并通过它执行数据库的操作
     *      （假设一个方法需要访问数据据好多次 但只有一次需要用到事务管理，用注解方式就会全部被管理，此时用到编程式事务 局部管理）
     */

}

