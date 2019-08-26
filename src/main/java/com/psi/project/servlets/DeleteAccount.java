/*
    Serwlet obslugujacy usuwanie konta.
 */
package com.psi.project.servlets;

import com.psi.project.connection.Connector;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

/**
*
* @author Szaman
*/

@WebServlet("/DeleteAccount")
public class DeleteAccount extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("text/html");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String hashedPassword = "";
        HttpSession session = request.getSession();
        try 
        {
            String errorMessage = ""; //Pusty string, do ktorego beda dodawane poszczegolne errory.
            connectionValidation = connector.testConnection();
            if(connectionValidation == false)
            {
                errorMessage += "Brak po³¹czenia z baz¹ danych!" + "\n";
            }
            else
            {
                System.out.println("DB Connected!");   
                if(password == null || password.length() == 0)
                {
                    errorMessage += "Proszê podaæ aktualne has³o!" + "\n";
                }
                else
                {
                    String storedPassword = connector.pullUserPassword((String)session.getAttribute("loggedEmail"));
                    hashedPassword = Connector.sha256Encryptor(password);
                    
                    System.out.println("Stored (z bazy): " + storedPassword);
                    System.out.println("Hashed (podane teraz): " + hashedPassword);
                    if(!storedPassword.equals(hashedPassword))
                    {
                        errorMessage += "Podano nieprawid³owe aktualne has³o!" + "\n";
                    }
                }
                
                if(confirmPassword == null || confirmPassword.length() == 0)
                {
                    errorMessage += "Proszê ponownie podaæ aktualne has³o!" + "\n";
                }
                else
                {
                    if(confirmPassword.length() != 0)
                    {
                        if(!confirmPassword.equals(password))
                        {
                            errorMessage += "Has³a sa niezgodne ze sob¹!" + "\n";
                        }
                    }
                }
                
            }
                
          //Gdy liczba bledow jest rowna zero:
            if (errorMessage.length() == 0) 
            {
                Connector.deleteAccount((String)session.getAttribute("loggedEmail"), hashedPassword);
                session.invalidate();
                request.setAttribute("responseMessage", "Konto zostalo usuniete!");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            } 

            //Gdy liczba bledow jest rozna od zera:
            else 
            {
                System.out.println(errorMessage);
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/deleteaccount.jsp").forward(request, response);
            }
        } 
        catch (NoSuchAlgorithmException  ex) 
        {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException  ex) 
        {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}