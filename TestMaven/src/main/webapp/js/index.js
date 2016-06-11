/**
 * Created by matto on 16-6-11.
 */
var basePath = $("#basePath").attr("value");

$(document).ready(function() {

    var userInfo = $.cookie('user');
    userInfo = eval("(" + userInfo + ")");

    $.ajax({
        type: "POST",
        url: basePath + "user/login/",   //绝对路径:请求controller位置/方法
        data: {
            userId: userInfo.id,
            password: userInfo.id
        },
        async: false,
        success: function (data) {
            alert("Success")
        },
        error: function (data) {
            alert("Error")
        },
        dataType: "text"
    });

    $.ajax({
        type: "GET",
        url: basePath + "user/getMethod/" + userId + '/' + 'shop',
        data: {},
        async: false,
        success: function (data) {
            var stateAmount = eval(data);
            var itemHtml =
                '<li>待付款<span id="1"  onclick="searchOrderHead(this)">('+stateAmount.wait_pay+')</span></li>'+
                '<li>已付款<span id="2"  onclick="searchOrderHead(this)">('+stateAmount.wait_send+')</span></li>'+
                '<li>待收货<span id="4"  onclick="searchOrderHead(this)">('+stateAmount.wait_receive+')</span></li>'+
                '<li>待评价<span id="5"  onclick="searchOrderHead(this)">('+stateAmount.wait_appraise+')</span></li>'
            $("#existUser").append(itemHtml);

            window.location.href=basePath+"search.jsp?pId="+pId;   //当前页面打开URL页面
            top.location.href="/url"                               //在顶层页面打开新页面
            parent.location.href="/url"                            //在父页面打开新页面
        },
        dataType : "json"
    });

});

function getName(ele){

    var name = ele.id       //取得组件的id

}

function getPassWord(){

}
/**
 * 例如路径QueryStringDemo.html?id=5&type=1&flag=0,调用后返回["id=5", "type=1", "flag=0"]
 * @param name
 * @returns {null}
 */
function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}