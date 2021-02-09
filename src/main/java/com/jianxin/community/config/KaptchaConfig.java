package com.jianxin.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

//配置类  由于Kaptcha没有和spring整合 需要自己写配置类   注解表明这是配置类
@Configuration
public class KaptchaConfig {
    //声明一个bean 这个bean被spring容器管理 装配
    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0,");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length","4");
       //噪声 即验证码的干扰项  点啊线啊啥的
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
        DefaultKaptcha  kaptcha = new DefaultKaptcha();
        //所有的配置项都是通过config配的  而config需要依赖于properties
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
