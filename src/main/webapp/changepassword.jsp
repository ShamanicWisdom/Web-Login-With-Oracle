<%@page import="com.psi.project.connection.Connector"%>
<!--JSP pozwalajaca zmienic haslo-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Zmiana Hasła</title>
    </head>
    <body>
        
        <%
            response.setHeader("Cache-Control","no-cache"); //Wymuszenie na przegladarce, by pobrala nowa kopie strony z serwera.
            response.setHeader("Cache-Control","no-store"); //Wymuszenie na przegladarce, by nie zapisywala w cache zadnej strony.
            response.setDateHeader("Expires", 0); //Wymuszenie odswiezania strony.
            response.setHeader("Pragma","no-cache"); //Kompatybilnosc wstecz dla HTML 1.0
            
            Connector connector = new Connector();
                            
            if(session.getAttribute("loggedEmail") == null)
            {
                request.setAttribute("responseMessage", "Proszę się zalogować!");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            else 
            {
                if(!connector.testConnection())
                {
                    session.invalidate();
                    request.setAttribute("responseMessage", "Brak połączenia z baza danych! Następuje wylogowanie.");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
        %>
        
        <br /><br />
        <h1><center>Zmiana Hasła</center></h1><br /><br />
        
        <h2><center>Ostrzeżenie! Wszystkie wcześniej używane hasła nie mogą być ponownie użyte!</center></h2><br />
        
        <form method="post" action="ChangePassword">  
            <center>Stare Hasło :<input type="password" name="oldPassword" /></center><br />
            <center>Nowe Hasło :<input type="password" name="newPassword" /></center><br />
            <center>Powtórz Nowe Hasło :<input type="password" name="confirmNewPassword" /></center><br />
            <center><input id="button" type="submit" name="button" value="Zatwierdź"/></center><br />
        </form>
        
         <center><div style="color: #FF0000;">${errorMessage}</div></center><br /><br />
    
    	<form method="post" action="manageaccount.jsp">   
            <table align="center">
                <td><input type="submit" value="Powrót"/></td>
            </table>
        </form>
        
        <br />
        
    </body>
</html>
