/*
    Serwlet obslugujacy wylogowanie.
 */
package com.psi.project.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Szaman
 */

@WebServlet("/Logout")
public class Logout extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
     
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        //Zlikwidowanie sesji.
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        session.invalidate();
        request.setAttribute("responseMessage", "Wylogowano!");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}