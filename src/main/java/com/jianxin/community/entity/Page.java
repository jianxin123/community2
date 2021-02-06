package com.jianxin.community.entity;
//封装分页相关的信息
public class Page {
    //页面中当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数(用于计算总页数)
    private int rows;
    //查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >=1 && limit <= 100 ) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >=0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页的起始行
    public int getOffset(){
        //current * limit - limit  比如第一页的起始为0
        return  (current-1)*limit;
    }

    /*获取页总数*/
    public int getTotal(){
        if(rows%limit==0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    /*获取显示的起始页码*/
    public int getFrom(){
        int from = current-2; //当前页的前2页
        return Math.max(from, 1); //小于1就显示第一页
    }

    /*获取显示的结束页码*/
    public int getTo(){
        int to = current+2;
        int total=getTotal();
        return Math.min(to, total); //超过最大页数就显示最后一页
    }


}
