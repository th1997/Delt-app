package fr.projetl3.deltapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetl3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class Account extends AppCompatActivity {
    private ImageButton  retour;
    private Button       disconnect;
    private TextView     email, prenom, nom;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String       user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        user  = mAuth.getCurrentUser();
        assert user != null;
        user_id = user.getUid();
        setupUI();
        loadUserProfile();
        disconnect.setOnClickListener(v -> logout());

        retour.setOnClickListener(v -> {
            Intent mainActivity = new Intent(getApplicationContext(), Accueil.class);
            startActivity(mainActivity);
            finish();
        });
    }

    private void loadUserProfile() {
        DatabaseReference rootRef  = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("users");

        usersRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String emailProfile  = userProfile.getEmail();
                    String prenomProfile = userProfile.getPrenom();
                    String nomProfile    = userProfile.getNom();

                    email.setText("Adresse mail: " + emailProfile);
                    prenom.setText("Prénom: " + prenomProfile);
                    nom.setText("Nom: " + nomProfile);
                } else
                    Toast.makeText(Account.this, "Couldn't load user class profile!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(Account.this, "Couldn't load profile! ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupUI() {
        this.retour     = (ImageButton) findViewById(R.id.btn_go_to_main);
        this.disconnect = (Button)      findViewById(R.id.btnDisconnect);
        this.email      = (TextView)    findViewById(R.id.tvEmailAccount);
        this.prenom     = (TextView)    findViewById(R.id.tvPrenomAccount);
        this.nom        = (TextView)    findViewById(R.id.tvNomAccount);
    }

    // Se déconnecter de votre compte.
    private void logout(){
        mAuth.signOut();
        Toast.makeText(Account.this, "Déconnexion réussi!\nRedirection vers la page de connexion dans 3s", Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> startActivity(new Intent(getApplicationContext(), LoginApp.class)),3000);
    }
}