/*
Klasa zawierajaca wszystkie operacje laczenia z baza danych
*/

package com.psi.project.connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Szaman
 */

public class Connector 
{
    //String przechowujacy dane niezbedne do dostepu do bazy danych.
    private static String connectionDataString = "jdbc:oracle:thin:@localhost:1521:orcl";  //Adres, gdzie baza sie znajduje.
    private static String connectionUserName = "Krzysiek";
    private static String connectionUserPassword = "krzysiek";
    
    //Metoda odpowiedzialna za sprawdzenie istnienia maila w DB (zabezpieczenie przed posiadaniem dwoch kont zarejestrowanych na tego samego maila).
    public boolean testConnection() throws SQLException
    {
        try 
        {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println("Brak sterownika JDBC!");
            e.printStackTrace();
            return false;

        }
        
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            System.out.println("DB Connection established!");
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            return false;
        }     
        try 
        {
            connection.close(); //Zamykanie polaczenia.
            System.out.println("DB Connection closed!");
        } 
        catch (SQLException e) 
        {
            return false;
        }
        return true;
    }
    
    public boolean isEmailExists(String email) throws SQLException, ClassNotFoundException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        String isExists = null;
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select Login from Logins where Login = '" + email + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                isExists = result.getString(1); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
            }    
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
        if(isExists != null) //Jesli nie ma wyniku (czyli nie istnieje w DB podany mail) zwracana jest falsz (czyli brak maila).
        {
            System.out.println("Email jest juz uzywany. Wynik: " + isExists);
            return true;
        }  
        else
        {
            System.out.println("Email nie jest uzywany. Wynik: " + isExists);
            return false;
        }
    }
    
    //Rejestracja wczesniej zweryfikowanych danych i zapis do DB.
    public static void addAccount(String userEmail, String password) throws SQLException, ClassNotFoundException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        CallableStatement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            //statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            String encryptedPassword = sha256Encryptor(password);
            SQLStatement = "{CALL AddLogin('" + userEmail + "', '" + encryptedPassword + "')}";
            statement = connection.prepareCall(SQLStatement);
            System.out.println(SQLStatement);
            statement.execute(); //Execute Query
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (Exception e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                statement.close(); //Zamykanie polaczenia.
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    //Metoda odpowiedzialna za sprawdzenie istnienia maila w DB (zabezpieczenie przed posiadaniem dwoch kont zarejestrowanych na tego samego maila).
    public int accountValidation(String login, String password) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException 
    {
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        String SQLStatement = "";
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        String userEmail = null;
        String userPassword = null;
        int accBlockade = 0;
        int loginFailureCount = 0;
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            SQLStatement = "select Login, Password, isBlocked, LoginFailures from Logins where Login = '" + login + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                userEmail = result.getString(1); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
                userPassword = result.getString(2);
                accBlockade = Integer.parseInt(result.getString(3));
                loginFailureCount = Integer.parseInt(result.getString(4));
            }
            System.out.println("Wynik: " + userEmail + " " + accBlockade + " fail count: " + loginFailureCount);
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                statement.close();
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        if(userEmail == null) //Jesli nie ma wyniku (czyli nie istnieje w DB podany mail) zwracana jest prawda (czyli brak maila).
        {
            return 0; 
        }  
        else
        {
        	int isArchival = isAccountArchival(userEmail);
        	if(isArchival == 1)
        	{
        		return 0;
        	}
        	else
        	{
        		String encryptedPassword = sha256Encryptor(password); //hashowanie hasla
                if(accBlockade == 1) //jesli accBlock jest 1, to zwracamy odpowiedz nr 3 - konto zablokowane.
                {
                    return 3;
                }
                else
                {
                    if(!(userPassword.equals(encryptedPassword))) //Jesli haslo jest bledne.
                    {
                        loginFailureCount++;
                        try
                        {
                            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
                            statement = connection.createStatement(); //proba polaczenia sie z DB.
                            System.out.println("DB Connected again!");
                            if(loginFailureCount >= 3) //jesli mamy juz 3 nieudana probe, to blokujemy konto.
                            {
                                SQLStatement = "ACCOUNT_BLOCKED";
                            }
                            else
                            {
                                SQLStatement = "LOGIN_FAILURE";
                            }

                            System.out.println(SQLStatement);
                        }
                        //W przypadku braku polaczenia wyrzuci SQLException
                        catch (SQLException e) 
                        {
                            System.out.println("DB Connection could not been established!");
                            e.printStackTrace();
                        }        
                        finally 
                        {
                            try 
                            {
                                statement.close();
                                connection.close(); //Zamykanie polaczenia.
                                System.out.println("DB Connection closed!");
                            } 
                            catch (Exception e) 
                            {
                                e.printStackTrace();
                            }
                        }  
                        if(SQLStatement.contains("ACCOUNT_BLOCKED"))
                        {
                            return 4;
                        }
                        return 1;
                    }
                    else //Jesli wszystko jest w porzadku.
                    {
                        return 2;
                    }
                }
        	}
        }
    }
    
    public List<String> pullAccountCredentials(String userEmail) 
    {
        List<String> credentialsList = new ArrayList<String>();
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select ID, Login, loginFailures from Logins where Login = '" + userEmail + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while (result.next()) //Wypisanie wszystkich wynikow.
            {
                credentialsList.add(result.getString(1)); //Argument w getString mowi, z ktorej kolumny czytamy informacje.
                credentialsList.add(result.getString(2));
                credentialsList.add(result.getString(3));
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return credentialsList;
    }
        
    //Pull aktualnego IP.
    public static String pullCurrentIP()
    {
    	String currentIP = ""; 
        try
        { 
            URL botURL = new URL("http://bot.whatismyipaddress.com"); 

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(botURL.openStream())); 

            // reads system IPAddress 
            currentIP = bufferedReader.readLine().trim(); 
        } 
        catch (Exception e) 
        { 
        	currentIP = "Error"; 
        } 
        System.out.println("Public IP Address: " + currentIP +"\n"); 
        return currentIP;
    }
    
    //pull ID uzytkownika.
    public static int pullUserID(String userEmail)
    {
        int userID = 0;
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select ID from Logins where Login = '" + userEmail + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next())
            {
                userID = result.getInt(1); //Argument w getInt mowi, z ktorej kolumny czytamy informacje.
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return userID;
    }
    
	//Wpisanie proby logowania.
	public static void addEntry(int userID, int credentialsCorrectness) throws SQLException, ClassNotFoundException 
	{
		Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
		Statement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
		String currentIP = pullCurrentIP();
		try
		{
			connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
			statement = connection.createStatement(); //proba polaczenia sie z DB.
			System.out.println("DB Connected!");
			String SQLStatement;
            //zapytanie SQLowskie
            SQLStatement = "INSERT INTO Logs (ID, IP, IDCorrect) VALUES (" + userID + ", '" + currentIP + "', " + credentialsCorrectness + ")";
            System.out.println(SQLStatement);
            statement.execute(SQLStatement); //Execute Query
		}
		//W przypadku braku polaczenia wyrzuci SQLException
		catch (Exception e) 
		{
			System.out.println("DB Connection could not been established!");
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				statement.close(); //Zamykanie polaczenia.
				connection.close(); //Zamykanie polaczenia.
				System.out.println("DB Connection closed!");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	//Sprawdzenie, czy konto nie jest zarchiwizowane (usuniete, niedostepne dla uzytkownika).
	public static int isAccountArchival(String userEmail)
	{
		Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
		Statement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        int isArchived = 0;
		try
		{
			connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "SELECT ISARCHIVAL FROM LOGINS WHERE LOGIN = '" + userEmail + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next())
            {
            	isArchived = result.getInt(1); //Argument w getInt mowi, z ktorej kolumny czytamy informacje.
            } 
		}
		//W przypadku braku polaczenia wyrzuci SQLException
		catch (Exception e) 
		{
			System.out.println("DB Connection could not been established!");
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				statement.close(); //Zamykanie polaczenia.
				connection.close(); //Zamykanie polaczenia.
				System.out.println("DB Connection closed!");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return isArchived;
	}
	
	//Wpisanie proby logowania.
	public static int isBlockadeExpired(int userID) throws SQLException, ClassNotFoundException 
	{
		Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
		Statement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        String datelog = "";
        int isExpired = 0;
		try
		{
			connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "SELECT DATELOG FROM (SELECT * FROM Logs WHERE ID = " + userID + " AND IDCorrect = 0 ORDER BY DATELOG DESC) WHERE ROWNUM = 1"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next())
            {
            	datelog = result.getString(1); //Argument w getInt mowi, z ktorej kolumny czytamy informacje.
            } 
            Date currentDate = new Date(); //aktualny czas
            Date convertedDatelog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datelog);  
            System.out.println("Date:  " + convertedDatelog);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertedDatelog);
            calendar.add(Calendar.MINUTE, 5);
            convertedDatelog = calendar.getTime();
            System.out.println("Converted date: " + convertedDatelog);
            if(currentDate.before(convertedDatelog)) //czy aktualna data systemowa jest wczesniejsza niz data wygasniecia blokady. jak tak, to blokada nie minela (=0), jak nie, to blokada przedawniona
            {
            	isExpired = 0;
            }
            else
            {
            	isExpired = 1;
            }
		}
		//W przypadku braku polaczenia wyrzuci SQLException
		catch (Exception e) 
		{
			System.out.println("DB Connection could not been established!");
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				statement.close(); //Zamykanie polaczenia.
				connection.close(); //Zamykanie polaczenia.
				System.out.println("DB Connection closed!");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return isExpired;
	}
	
	//Pobranie aktualnego hasla uzytkownika.
    public String pullUserPassword(String userEmail)
    {
    	String userPassword = "";
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "select Password from Logins where Login = '" + userEmail + "'"; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next())
            {
            	userPassword = result.getString(1); //Argument w getInt mowi, z ktorej kolumny czytamy informacje.
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return userPassword;
    }
    
    //Pull wszystkich uzytych hasel uzytkownika.
    public List<String> pullUserStoredPasswords(String userEmail)
    {
        List<String> passwordsList = new ArrayList<String>();
        
        int userID = pullUserID(userEmail); //pobranie ID uzytkownika.
        System.out.println("UserID: " + userID);
        Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        Statement statement = null;  //Obiekt odpowiedzialny za zapytania SQLowskie.
        ResultSet result = null; //Obiekt odpowiedzialny za pobieranie wynikow zapytan SQLowych.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "SELECT PASSWORD FROM PASSWORDS WHERE ID = " + userID ; //zapytanie SQLowskie
            System.out.println(SQLStatement);
            result = statement.executeQuery(SQLStatement); //Execute Query
            while(result.next()) //Wypisanie wszystkich wynikow.
            {
                passwordsList.add(result.getString(1));
            }
        }
        catch (SQLException e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }        
        finally 
        {
            try 
            {
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }   
        return passwordsList;
    }
        
    //Zmiana hasla.
  	public static void updatePassword(String userEmail, String oldPassword, String newPassword) throws SQLException
  	{
  		Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        CallableStatement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            //statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "{CALL UpdatePassword('" + userEmail + "', '" + oldPassword + "', '" + newPassword + "')}";
            statement = connection.prepareCall(SQLStatement);
            System.out.println(SQLStatement);
            statement.execute(); //Execute Query
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (Exception e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                statement.close(); //Zamykanie polaczenia.
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
  	}
  	
  	
    //Zmiana hasla.
  	public static void deleteAccount(String userEmail, String password) throws SQLException
  	{
  		Connection connection = null; //Obiekt odpowiedzialny za laczenie z DB.
        CallableStatement statement = null; //Obiekt odpowiedzialny za zapytania SQLowskie.
        try
        {
            connection = DriverManager.getConnection(connectionDataString, connectionUserName, connectionUserPassword); //wskazanie miejsca zaalokowania DB.
            //statement = connection.createStatement(); //proba polaczenia sie z DB.
            System.out.println("DB Connected!");
            String SQLStatement;
            SQLStatement = "{CALL RemoveLogin('" + userEmail + "', '" + password + "')}";
            statement = connection.prepareCall(SQLStatement);
            System.out.println(SQLStatement);
            statement.execute(); //Execute Query
        }
        //W przypadku braku polaczenia wyrzuci SQLException
        catch (Exception e) 
        {
            System.out.println("DB Connection could not been established!");
            e.printStackTrace();
        }
        finally 
        {
            try 
            {
                statement.close(); //Zamykanie polaczenia.
                connection.close(); //Zamykanie polaczenia.
                System.out.println("DB Connection closed!");
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
  	}
	
    //Szyfrowanie has³a przy pomocy SHA-256
    public static String sha256Encryptor(String password) throws NoSuchAlgorithmException 
    {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = messageDigest.digest(password.getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < result.length; i++) 
        {
            stringBuilder.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }         
        return stringBuilder.toString();
    }
}
