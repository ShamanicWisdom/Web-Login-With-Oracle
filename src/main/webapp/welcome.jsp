<!--JSP zalogowana-->
<%@page import="com.psi.project.connection.Connector"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" session="true" content="text/html; charset=UTF-8">
        <title>Zalogowano!</title>
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
                    request.setAttribute("responseMessage", "Brak połączenia z bazą danych!");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
            
            List credentials = connector.pullAccountCredentials((String)session.getAttribute("loggedEmail"));
            System.out.println(credentials);
        %>
        
        <br /><br /><br />
        <h1><center>Witaj na swoim koncie!</center></h1><br /><br /><br />
        <h1><center>Twoje dane: </center></h1>
        <h2><center>Email: ${loggedEmail}</center></h2> 
        <h2><center>ID: <%= credentials.get(0) %></center></h2> 
    
        <br />
        <h2><div style="color: #FF0000;">${responseMessage}</div></h2>
    
        <br />
        
        <form method="post" action="manageaccount.jsp">    
            <table align="center">
                <td><input type="submit" value="Zarządzaj Kontem"/></td>
            </table>
        </form>
        
        <form method="post" action="Logout">    
            <table align="center">
                <td><input type="submit" value="Wylogowanie"/></td>
            </table>
        </form>
    </body>
</html>