package com.example.projetl3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.net.ssl.HttpsURLConnection;

public class Database {

    public Database(){

    }


    public Boolean emailTaken(String email) {
        Boolean emailTaken = false;
        try {
            URL url = new URL("http://constant-abraham.fr/application/isEmailTaken.php");
            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            URLConnection conn = url.openConnection();
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            reader.close();
            emailTaken = Boolean.valueOf(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailTaken;
    }
}
