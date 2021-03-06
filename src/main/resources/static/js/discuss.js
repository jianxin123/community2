$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});


/*
* btn是前面那个超链接
* CONTEXT_PATH + "/like",作用路径
* {"entityType":entityType,"entityId":entityId},  要传的参数
* function (data) {    处理传回的数据
  }
* data = $.parseJSON(data);  先将数据变为json格式
*$(btn).children("i") 获取a标签下的子标签i  text(data.likeCount)将里面的数据改为传回来的数据
*
*
* */

function like(btn,entityType,entityId,entityUserId,postId){

    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            }else {
                alert(data.msg);
            }
        }
    );
}

//置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#topBtn").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    );
}

//加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#wonderfulBtn").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    );
}


//删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                location.href = CONTEXT_PATH + "/index";
            }else {
                alert(data.msg);
            }
        }
    );
}






















