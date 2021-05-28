package fr.projetl3.deltapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetl3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginApp extends AppCompatActivity {

    private ImageButton retour;
    private EditText    email, pwd;
    private Button      bLogin;
    private TextView    userRegisterPage;
    private FirebaseAuth mAuth;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_app);

        setupUIView();
        mAuth = FirebaseAuth.getInstance();

        retour.setOnClickListener(v -> {
            Intent mainActivity = new Intent(getApplicationContext(), Accueil.class);
            startActivity(mainActivity);
            finish();
        });

        userRegisterPage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterApp.class)));

        bLogin.setOnClickListener(v -> login());

    }

    private void login() {
        String emailText = email.getText().toString().trim();
        String pwdText   = pwd.getText().toString().trim();

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
            mAuth.signInWithEmailAndPassword(emailText, pwdText).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null && user.isEmailVerified()){
                        Toast.makeText(LoginApp.this, "Connexion à votre compte réussi!", Toast.LENGTH_LONG).show();
                        Handler handler = new Handler();
                        handler.postDelayed(() -> startActivity(new Intent(getApplicationContext(), Account.class)),3000);
                    } else {
                        Toast.makeText(LoginApp.this, "Vous devez vérifier votre adresse mail, un email vous a été envoyé lors de votre inscription!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginApp.this, "Erreur lors de la connexion : " + task.getException(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setupUIView() {
        this.retour           = findViewById(R.id.btn_go_to_main);
        this.userRegisterPage = findViewById(R.id.tvRegister);
        this.email            = findViewById(R.id.emailLogin);
        this.pwd              = findViewById(R.id.pwdLogin);
        this.bLogin           = findViewById(R.id.btnLogin);
    }
}