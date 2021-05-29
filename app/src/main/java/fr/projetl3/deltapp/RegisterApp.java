package fr.projetl3.deltapp;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterApp extends AppCompatActivity {

    private ImageButton  retour;
    private EditText     email, nom, prenom, pwd1, pwd2;
    private Button       bRegister;
    private TextView     userLoginPage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register_app);
        //View view = this.getWindow().getDecorView(); view.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        setupUIViews();

        retour.setOnClickListener(v -> {
            Intent mainActivity = new Intent(getApplicationContext(), Accueil.class);
            startActivity(mainActivity);
            finish();
        });
        bRegister.setOnClickListener(v -> register());

        userLoginPage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginApp.class)));

    }

    private void setupUIViews(){
        retour        = findViewById(R.id.btn_go_to_main);
        email         = findViewById(R.id.emailRegister);
        nom           = findViewById(R.id.nomRegister);
        prenom        = findViewById(R.id.prenomRegister);
        pwd1          = findViewById(R.id.pwd1Register);
        pwd2          = findViewById(R.id.pwd2Register);
        bRegister     = findViewById(R.id.bRegister);
        userLoginPage = findViewById(R.id.tvLogin);
    }


    private void register() {

        String emailText  = email.getText().toString().trim();
        String nomText    = nom.getText().toString().trim();
        String prenomText = prenom.getText().toString().trim();
        String pwd1Text   = pwd1.getText().toString().trim();
        String pwd2Text   = pwd2.getText().toString().trim();

        if(emailText.isEmpty()){
            email.setError("Veuillez remplir ce champ");
            email.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Email invalide");
            email.requestFocus();
        } else if(nomText.isEmpty()){
            nom.setError("Veuillez remplir ce champ");
            nom.requestFocus();
        } else if(prenomText.isEmpty()){
            prenom.setError("Veuillez remplir ce champ");
            prenom.requestFocus();
        } else if(pwd1Text.isEmpty()){
            pwd1.setError("Veuillez remplir ce champ");
            pwd1.requestFocus();
        } else if(pwd1Text.length() < 8){
            pwd1.setError("Le mot de passe doit contenir au moins 8 caractères");
        } else if(pwd2Text.isEmpty()){
            pwd2.setError("Veuillez remplir ce champ");
            pwd2.requestFocus();
        } else if(!pwd1Text.equalsIgnoreCase(pwd2Text)){
            pwd2.setError("Les mot de passe doivent correspondre");
            pwd2.requestFocus();
        } else {
            try {
                mAuth.createUserWithEmailAndPassword(emailText, pwd1Text).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        User              user     = new User(emailText, nomText, prenomText);
                        DatabaseReference rootRef  = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference usersRef = rootRef.child("users");

                        usersRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification();
                                Toast.makeText(RegisterApp.this, "L'utilisateur à bien été enregistré !\nUn mail de vérification a été envoyé!", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                Handler handler = new Handler();
                                handler.postDelayed(() -> startActivity(new Intent(getApplicationContext(), LoginApp.class)),3000);
                            } else {
                                Toast.makeText(RegisterApp.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        Toast.makeText(RegisterApp.this, "Erreur lors de l'inscription, veuillez réessayer! " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e){
                Toast.makeText(RegisterApp.this, "User data creation failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}