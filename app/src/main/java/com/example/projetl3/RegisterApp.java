package com.example.projetl3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterApp extends AppCompatActivity {

    private ImageButton retour;
    private EditText email, nom, prenom, pwd1, pwd2;
    private Button bRegister;
    private TextView userLoginPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_app);
        setupUIViews();
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Toast.makeText(RegisterApp.this, "Création du compte!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginApp.class));
            }
        });

    }

    private void setupUIViews(){
        retour = (ImageButton) findViewById(R.id.btn_go_to_main);
        email = (EditText)findViewById(R.id.emailRegister);
        nom = (EditText)findViewById(R.id.nomRegister);
        prenom = (EditText)findViewById(R.id.prenomRegister);
        pwd1 = (EditText)findViewById(R.id.pwd1Register);
        pwd2 = (EditText)findViewById(R.id.pwd2Register);
        bRegister = (Button)findViewById(R.id.bRegister);
        userLoginPage = (TextView)findViewById(R.id.tvLogin);
    }
    private Boolean validate(){
        Boolean result = true;

        String emailText = email.getText().toString();
        String nomText = nom.getText().toString();
        String prenomText = prenom.getText().toString();
        String pwd1Text = pwd1.getText().toString();
        String pwd2Text = pwd2.getText().toString();

        if(emailText.isEmpty() || nomText.isEmpty() || prenomText.isEmpty() ||  pwd1Text.isEmpty() || pwd2Text.isEmpty()){
            Toast.makeText(this, "Veuillez renseignez toutes les cases pour vous inscrire !", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if(!pwd1Text.equalsIgnoreCase(pwd2Text)){
            Toast.makeText(this, "Les mots de passes ne correspondent pas !", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }

}