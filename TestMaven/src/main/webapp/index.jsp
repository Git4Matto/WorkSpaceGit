<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "
http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head lang="en">
    <basePath value="<%=basePath%>" id="basePath"></basePath>
    <meta charset="UTF-8">
    <title>My TestMaven</title>
    <link rel="stylesheet" href="<%=basePath %>css/index.css"/>

</head>
<body>
<jsp:include page="headers/header.jsp"/>
Hello World !!!
<c:forEach var="comm" items="${orderMsgBean.orderInfoBeansList }">
    <a href="<%=basePath %>item.jsp?item=${ comm.commodity_id}" target="_Blank"/>
    <c:if test="${comm.state=='1' }">
        <td class="status">待付款</td>
    </c:if>
</c:forEach>
<jsp:include page="headers/footer.jsp"/>
<script src="<%=basePath %>js/index.js" charset="UTF-8"></script>
</body>
</html>