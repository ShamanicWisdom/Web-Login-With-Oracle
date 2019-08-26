/*
    Serwlet obslugujacy logowanie.
 */

package com.psi.project.servlets;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.*;

import com.psi.project.connection.Connector;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Szaman
 */

@WebServlet("/Login")
public class Login extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    
    Boolean connectionValidation = false;
    
    Connector connector = new Connector();
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        int accountValidation = 0;
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        try 
        {
            connectionValidation = connector.testConnection();
            if(connectionValidation == true)
            {
                System.out.println("DB Connected!");
                if(email == "" && email.length() == 0) //nie podano loginu
                {
                    request.setAttribute("responseMessage", "Proszê podaæ email!");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
                if(password == "" && password.length() == 0) //nie podano hasla
                {
                    request.setAttribute("responseMessage", "Proszê podaæ has³o!");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else
                {
                    accountValidation = connector.accountValidation(email, password);
                    if(accountValidation == 0) //konto nie istnieje.
                    {
                        request.setAttribute("responseMessage", "Konto nie istnieje!");
                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                    }
                    else
                    {
                    	int userID = Connector.pullUserID(email);
                        if(accountValidation == 1) //bledne dane.
                        {
                        	Connector.addEntry(userID, 0);
                            request.setAttribute("responseMessage", "Niepoprawne dane...");
                            request.getRequestDispatcher("/index.jsp").forward(request, response);
                        }
                        else
                        {
                            if(accountValidation == 2) //dobre logowanie.
                            {
                                //Stworzenie sesji, przypisanie do niej maila i  do dalszej manipulacji danymi.
                            	Connector.addEntry(userID, 1);
                                HttpSession session = request.getSession();
                                session.setAttribute("loggedEmail", email);
                                response.sendRedirect("welcome.jsp");
                            }
                            else
                            {
                                if(accountValidation == 4) //zablokowanie konta
                                {
                                	Connector.addEntry(userID, 0);
                                    request.setAttribute("responseMessage", "Przekroczono limit b³êdnych prób logowania! Nastêpuje zablokowanie konta.");
                                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                                }
                                else //konto zablokowane.
                                {
                                	int isBlockadeExpired = Connector.isBlockadeExpired(userID); //jesli juz minal ban, to pozwolenie na dobre logowanie.
                                	if(isBlockadeExpired == 1)
                                	{
                                		Connector.addEntry(userID, 1);
                                		HttpSession session = request.getSession();
                                        session.setAttribute("loggedEmail", email);
                                        response.sendRedirect("welcome.jsp");
                                	}
                                	
                                    else
                                    {
                                    	Connector.addEntry(userID, 0);
                                        request.setAttribute("responseMessage", "Konto jest zablokowane!");
                                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                                    }
                                }
                            }
                        }
                    }
                }       
            }
            else
            {
                out.println("DB connection could not been established!");
            }
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
