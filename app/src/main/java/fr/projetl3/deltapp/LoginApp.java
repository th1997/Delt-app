package fr.projetl3.deltapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetl3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginApp extends AppCompatActivity {

    private ImageButton retour;
    private EditText email, pwd;
    private Button bLogin;
    private TextView userRegisterPage;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_app);
        setupUIView();
        mAuth = FirebaseAuth.getInstance();
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), Accueil.class);
                startActivity(mainActivity);
                finish();
            }
        });

        userRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterApp.class));
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        String emailText = email.getText().toString().trim();
        String pwdText = pwd.getText().toString().trim();
        if(emailText.isEmpty()){
            email.setError("Veuillez remplir ce champ");
            email.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Email invalide");
            email.requestFocus();
        } else if(pwdText.isEmpty()){
            pwd.setError("Veuillez remplir ce champ");
            pwd.requestFocus();
        } else if(pwdText.length() < 8){
            pwd.setError("Le mot de passe doit contenir au moins 8 caractères");
        } else {
            mAuth.signInWithEmailAndPassword(emailText, pwdText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null && user.isEmailVerified()){
                            Toast.makeText(LoginApp.this, "Connexion à votre compte réussi!", Toast.LENGTH_LONG).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), Account.class));
                                }
                            },3000);
                        } else {
                            Toast.makeText(LoginApp.this, "Vous devez vérifier votre adresse mail, un email vous a été envoyé lors de votre inscription!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginApp.this, "Erreur lors de la connexion : " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void setupUIView() {
        this.retour = (ImageButton) findViewById(R.id.btn_go_to_main);
        this.userRegisterPage = (TextView) findViewById(R.id.tvRegister);
        this.email = (EditText) findViewById(R.id.emailLogin);
        this.pwd = (EditText) findViewById(R.id.pwdLogin);
        this.bLogin = (Button) findViewById(R.id.btnLogin);
    }
}