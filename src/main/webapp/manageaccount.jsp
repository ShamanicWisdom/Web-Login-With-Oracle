<!--JSP zarzadzania kontem-->
<%@page import="java.util.List"%>
<%@page import="com.psi.project.connection.Connector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" session="true" content="text/html; charset=UTF-8">
        <title>Zarządzanie Kontem</title>
    </head>
    <body>
        
        <%
            response.setHeader("Cache-Control","no-cache"); //Wymuszenie na przegladarce, by pobrala nowa kopie strony z serwera.
            response.setHeader("Cache-Control","no-store"); //Wymuszenie na przegladarce, by nie zapisywala w cache zadnej strony.
            response.setDateHeader("Expires", 0); //Wymuszenie odswiezania strony.
            response.setHeader("Pragma","no-cache"); //Kompatybilnosc wstecz dla HTML 1.0
            
            if(session.getAttribute("loggedEmail") == null)
            {
                request.setAttribute("responseMessage", "Proszę się zalogować!");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            else 
            {
                Connector connector = new Connector();
                if(!connector.testConnection())
                {
                    session.invalidate();
                    request.setAttribute("responseMessage", "Brak połączenia z bazą danych! Następuje wylogowanie.");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
        %>
        
        <br /><br />
        <h1><center>Zarządzanie Kontem</center></h1><br /><br />
        
        
        <h2><center><div style="color: #FF0000;">${responseMessage}</div></center></h2>
    
        <form method="post" action="changepassword.jsp">   
            <table align="center">
                <td><input type="submit" value="Zmień Hasło"/></td>
            </table>
        </form>
                
        <form method="post" action="deleteaccount.jsp">   
            <table align="center">
                <td><input type="submit" value="Usuń Konto"/></td>
            </table>
        </form>
        
        
        <br /><br />
        
        <form method="post" action="welcome.jsp">   
            <table align="center">
                <td><input type="submit" value="Powrót"/></td>
            </table>
        </form>
        
    </body>
</html>