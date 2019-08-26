<!--JSP rejestracyjna-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Rejestracja</title>
    </head>
    <body>
        
        <br /><br />
        <h1><center>Tworzenie nowego konta</center></h1>
        
        <br />
        
        <form method="post" action="Register">    
            <table align="center">
                <tr>
                    <td>Email:</td>
                    <td><input type="text" name="login" /></td>    
                </tr>
                <tr>
                    <td>Hasło:</td>
                    <td><input type="password" name="password" /></td>
                </tr>
                <tr>
                    <td>Powtórz Hasło:</td>
                    <td><input type="password" name="confirmPassword" /></td>
                </tr>
                <tr>
                    <td><center><input type=submit value="Zarejestruj się"/></center></td> 
                </tr>
            </table>
        </form>
    
        <h2><center><div style="color: #FF0000;">${errorMessage}</div></center></h2>
        <br />
    
        <form action="index.jsp">
            <table align="center">
                <tr>
                    <td><input type="submit" value="Powrót" /></td>
                </tr>                
            </table>
        </form>
        
    </body>
</html>
