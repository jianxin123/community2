package com.jianxin.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

//敏感词过滤
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换成***
    private static final String REPLACEMENT = "铁帅哥";

    //根节点
    private TrieNode rootNode = new TrieNode();
    // @PostConstruct当容器实例化SensitiveFilter这个bean以后 调用它的构造器后 这个方法会自动调用
    @PostConstruct
    public void init(){
        try (
                InputStream is =  this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));//将字节流转为缓冲流
        ){
            String keyword;
            while((keyword=reader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败： "+ e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);//获得值为c的临时节点的子节点
            if(subNode == null){
                //如果为空，初始化子节点
                subNode = new TrieNode();//新建一个节点
                tempNode.addSubNode(c,subNode);//添加子节点
            }
            tempNode = subNode;//遍历
            //设置结束标识
            if(i==keyword.length()-1){
                //i成功遍历到最后 说明这是一个敏感词
                tempNode.setKeywordEnd(true);
            }
        }
    }
    //参数是待过滤的文本 返回过滤后的文本
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1 指向前缀树 一开始指向根节点
        TrieNode tempNode = rootNode;
        //指针2 int类型 是个索引 和指针3遍历待过滤文本
        int begin = 0;
        //指针3
        int position = 0;
        //记录过滤结果 由于会不断添加*** 属于变长 不用String类型
        StringBuilder sb = new StringBuilder();

        while (position < text.length()){
            char c = text.charAt(position);
            //跳过特殊符号  比如#赌#博# 过滤掉#
            if(isSymbol(c)){
                //若指针1处于根节点 则此符号应加到过滤后的文本中不做处理 指针2向下走一步
                // 如#赌#博# 过滤完是#***#  第一个#不做过滤
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论特殊符号在开头还是中间指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode==null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词 将begin 到position字符串替换
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }
        //position遍历到结尾 begin没到结尾时 将最后几个字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }


    //判断是否为符号 CharUtils.isAsciiAlphanumeric(c);如果c为abc...正常字符则返回true  @#￥等特殊字符返回false
    // 前面取反则c为特殊字符就返回true  0x2E80-0x9FFF是东亚文字范围包括中文韩文日文
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) &&(c < 0x2E80 || c>0x9FFF);
    }


    //定义前缀树
    private class TrieNode{
        //敏感词结束标志
        private boolean isKeywordEnd = false;
        //子节点 key是字符 value是下级节点类似于指针
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c ,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
