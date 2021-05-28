package fr.projetl3.deltapp;

import android.content.Context;
import android.os.Environment;

import com.example.projetl3.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Modules {
    private String moduleName;
    private String description;
    private Context context;


    public Modules(String moduleName, Context context) {
        this.moduleName = moduleName;
        this.context = context;
        description = "";
        setDesc();
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private void setDesc() {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        InputStream file = null;
        boolean find = false;

        try {
            // Le fichier d'entrée
            file = (context.getResources().openRawResource(R.raw.description_module));
            inputStreamReader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferedReader.readLine()) != null){
               if(line.equals("</" +moduleName +">")){
                    break;
                }
                if(line.equals("<" +moduleName +">"))
                    find = true;
                else if(find)
                    description = description + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (inputStreamReader != null)
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (file != null)
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public String getDescription() {
        return description;
    }
}