<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:import url="header.jsp"/>
	<section id="login">
	   	<form action="/login" method="POST" >
	   		<label>Adresa email</label>
	   		<input name="email"/><br>
	   		<label>Parola:</label><br>
	   		<input type="password" name="password"/><br>
	   		<p style="color: green; font-weight: bold">${msg}</p>
	   		<button type="submit">Întră în cont</button><br>
	   	</form>
	   	<form action="/recuperare" method="GET">
	   		<button type="submit">Recuperare parolă</button>
	   	</form>
   	</section>
   	<p></p>
<c:import url="footer.jsp"/>