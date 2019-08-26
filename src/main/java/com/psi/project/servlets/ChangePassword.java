/*
    Serwlet obslugujacy zmiane hasla.
 */

package com.psi.project.servlets;

// Laczenie z reszta wlasnych klas //

import com.psi.project.connection.Connector;

// Reszta klas //

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

/**
*
* @author Szaman
*/

@WebServlet("/ChangePassword")
public class ChangePassword extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("text/html");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");
        String hashedNewPassword = "";
        String hashedOldPassword = "";
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
                if(oldPassword == null || oldPassword.length() == 0)
                {
                    errorMessage += "Proszê podaæ aktualne has³o!" + "\n";
                }
                else
                {
                    String storedOldPassword = connector.pullUserPassword((String)session.getAttribute("loggedEmail"));
                    hashedOldPassword = Connector.sha256Encryptor(oldPassword);
                    if(!storedOldPassword.equals(hashedOldPassword))
                    {
                        errorMessage += "Podano nieprawid³owe aktualne has³o!" + "\n";
                    }
                }
                
                if(newPassword == null || newPassword.length() == 0)
                {
                    errorMessage += "Proszê podaæ nowe has³o!" + "\n";
                }
                else
                {
                    if (newPassword.length() < 6) 
                    {
                        errorMessage += "Proszê podaæ minimum 6 znakowe has³o!" + "\n"; 
                    }
                    else
                    {
                    	hashedNewPassword = Connector.sha256Encryptor(newPassword);
                    	List<String>userStoredPasswords = connector.pullUserStoredPasswords((String)session.getAttribute("loggedEmail"));
                        for(String storedPassword : userStoredPasswords)
                        {
                        	if(hashedNewPassword.equals(storedPassword))
                        	{
                        		errorMessage += "Podane has³o by³o ju¿ w u¿yciu!" + "\n"; 
                        	}
                        }
                    }
                }
                
                if(confirmNewPassword == null || confirmNewPassword.length() == 0)
                {
                    errorMessage += "Proszê ponownie podaæ nowe has³o!" + "\n";
                }
                else
                {
                    if(newPassword.length() != 0)
                    {
                        if(!newPassword.equals(confirmNewPassword))
                        {
                            errorMessage += "Nowe has³a sa niezgodne ze sob¹!" + "\n";
                        }
                    }
                }
                
            }
                
            //Gdy liczba bledow jest rowna zero:
            if (errorMessage.length() == 0) 
            {
                Connector.updatePassword((String)session.getAttribute("loggedEmail"), hashedOldPassword, hashedNewPassword);
                request.setAttribute("responseMessage", "Has³o zostalo zmienione!");
                request.getRequestDispatcher("/manageaccount.jsp").forward(request, response);
            } 

            //Gdy liczba bledow jest rozna od zera:
            else 
            {
                System.out.println(errorMessage);
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/changepassword.jsp").forward(request, response);
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