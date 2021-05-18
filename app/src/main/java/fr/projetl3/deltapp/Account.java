package fr.projetl3.deltapp;

import androidx.appcompat.app.AppCompatActivity;
import com.example.projetl3.R;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Account extends AppCompatActivity {
    private ImageButton retour;
    private Button disconnect;
    private TextView email, prenom, nom;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mAuth = FirebaseAuth.getInstance();
        setupUI();
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
    }

    private void setupUI() {
        this.retour = (ImageButton) findViewById(R.id.btn_go_to_main);
        this.disconnect = (Button) findViewById(R.id.btnDisconnect);
        this.email = (TextView) findViewById(R.id.tvEmailAccount);
        this.prenom = (TextView) findViewById(R.id.tvPrenomAccount);
        this.nom = (TextView) findViewById(R.id.tvNomAccount);
    }

    private void logout(){
        mAuth.signOut();
        Toast.makeText(Account.this, "Déconnexion réussi!\nRedirection vers la page de connexion dans 3s", Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginApp.class));
            }
        },3000);

    }
}