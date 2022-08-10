<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!doctype html>
<html lang="ro">
    <head>
        <meta charset="utf-8">
        <title>Magazin acvaristică</title>
        <link rel="stylesheet" href="/resources/css/bootstrap.min.css">
        <link rel="stylesheet" href="/resources/css/style.css?v=1.0">
        <link rel="shortcut icon" href="/resources/img/fav.ico">
        <script src="/resources/js/jquery-3.3.1.min.js"></script>
        <script src="/resources/js/popper.min.js"></script>
        <script src="/resources/js/bootstrap.min.js"></script>
        <script src="/resources/js/script.js"></script>
        <c:if test="${article.name==null}">
        	<meta name="description" content="Magazin online cu pești de acvariu, hrană, accesorii și acvarii.">
        </c:if>
        <meta name="description" content="${article.name}">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
    <header>
    	<ul>
    		<li><a href="/">Pagina de start</a></li>
    		<li><a href="/inregistrare">Înregistrare</a></li>
    		<sec:authorize var="loggedIn" access="isAuthenticated()" />
    		<c:choose>
    			<c:when test="${loggedIn}">
    				<li><a href="/login">Utilizator: <sec:authentication property="principal.email" /></a>
    				<li>
                        <form method="POST" action="/logout">
                            <input type="submit" value="Logout"/>
                        </form>
    			</c:when>
    			<c:otherwise>
    				<li><a href="/login">Intră în cont</a></li>
    			</c:otherwise>
    		</c:choose>
    	</ul>
    	<ul id=headerRight>
    		<li><a href="/cos"><img id="cos" src="/resources/img/cos.png">Coș de cumpărături
    		<c:set var="i" value="0"/>
    		<c:forEach var="count" items="${products}">
    			<c:set var="i" value="${i+1}"/>
    		</c:forEach>	
    		(${i})
    			</a></li>
    		<li><img id="call" src="/resources/img/call.png">Comenzi telefonice: <p>0740.000.000</p></li>
    	</ul>
    	<img id="headerImg" src="/resources/img/header.png">
    	<h1>Magazin acvaristică</h1>
    </header>