<!--JSP startowa-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" session="true" content="text/html; charset=UTF-8">
        <title>Logowanie</title>
    </head>
    <body>
        
        <br /><br />
        <h1><center>Aplikacja Webowa - Logowanie</center></h1>
        
        <br />
        
        <form method="post" action="Login">       
            <table align="center">
                <tr>
                    <td>Email : <input type=text name=email /></td>
                </tr>
                <tr>
                    <td>Hasło : <input type=password name=password /></td>
                </tr>
                <tr>
                    <td><center><input type=submit value="Zaloguj się" /></center></td>
                </tr>
            </table>            
        </form>
        
        <h2><center><div style="color: #FF0000;">${responseMessage}</div></center></h2>
        
        <br /><br /><br />
        
        <form action="register.jsp">
            <table align="center">
                <tr>
                    <td><input type="submit" value="Zarejestruj się" /></td>
                </tr>
            </table> 
        </form>
        
    </body>
</html>
