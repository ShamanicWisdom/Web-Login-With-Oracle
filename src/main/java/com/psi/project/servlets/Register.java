/*
Serwlet obslugujacy rejestracje.
*/

package com.psi.project.servlets;

import java.io.*;
import javax.servlet.http.*;

import com.psi.project.connection.Connector;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*
* @author Szaman
*/

@WebServlet("/Register")
public class Register extends HttpServlet 
{
	
	private static final long serialVersionUID = 1L;
	
	Boolean connectionValidation = false;
	
	Connector connector = new Connector();
	
	public Register() 
	{
        super();
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	    response.setContentType("text/html;charset=UTF-8");
	    String email = request.getParameter("login");
	    String password = request.getParameter("password");
	    String confirmPassword = request.getParameter("confirmPassword");
	    
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
	            
	            
	            if(email == null || email.length() == 0)
	            {
	                errorMessage += "Proszê podaæ adres mailowy!" + "\n";
	            }
	            else
	            {
	                Pattern validatedEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); //Wzorzec adresu email.
	                Matcher matchEmail = validatedEmail.matcher(email); //Matcher pobiera podanego maila i porownuje, czy zgadza sie ze wzorcem.
	                Boolean emailValidated = matchEmail.matches(); //Logika dla matchera od adresu email. 
	                
	                if (!(emailValidated))
	                {
	                    errorMessage += "Niepoprawny adres mailowy!" + "\n";
	                }
	
	                if(connector.isEmailExists(email))
	                {
	                    errorMessage += "Email jest ju¿ w u¿yciu!" + "\n";
	                }
	            }
	            
	            if(password == null || password.length() == 0)
	            {
	                errorMessage += "Proszê podaæ has³o!" + "\n";
	            }
	            else
	            {
	                if (password.length() < 6) 
	                {
	                    errorMessage += "Proszê podaæ minimum 6 znakowe has³o!" + "\n"; 
	                }
	            }
	            
	            if(confirmPassword == null || confirmPassword.length() == 0)
	            {
	                errorMessage += "Proszê ponownie podaæ has³o!" + "\n";
	            }
	            else
	            {
	                if(password.length() != 0)
	                {
	                    if(!password.equals(confirmPassword))
	                    {
	                        errorMessage += "Podane has³a s¹ niezgodne ze sob¹!" + "\n";
	                    }
	                }
	            }
	        }
	            
	        //Gdy liczba bledow jest rowna zero:
	        if (errorMessage.length() == 0) 
	        {
	            Connector.addAccount(email, password);
	            request.setAttribute("responseMessage", "Konto zostalo utworzone! Mo¿esz siê teraz zalogowaæ.");
	            request.getRequestDispatcher("/index.jsp").forward(request, response);
	        } 
	
	        //Gdy liczba bledow jest rozna od zera:
	        else 
	        {
	            System.out.println(errorMessage);
	            request.setAttribute("errorMessage", errorMessage);
	            request.getRequestDispatcher("/register.jsp").forward(request, response);
	        }
	    } 
	    
	    catch (SQLException ex) 
	    {
	        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
	    }
	    
	    catch (ClassNotFoundException ex) 
	    {
	        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
}
