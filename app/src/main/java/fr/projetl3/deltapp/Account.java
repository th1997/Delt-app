package fr.projetl3.deltapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetl3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

        Button buttonLoadImage = findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(arg0 -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 11);
        });

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
                    Uri    photoProfile  = userProfile.getProfilpics();
                    importPics(photoProfile);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = findViewById(R.id.profilpics);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            savePics(selectedImage);
        }
    }

    private void savePics(Uri picturePath){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("Test.jpg");
        StorageReference mountainImagesRef = storageRef.child("PhotoProfile/Test.jpg");
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        mountainImagesRef.putFile(picturePath);

    }

    private void importPics(Uri picturePath){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://deltapp-800f8.appspot.com/PhotoProfile/Test.jpg");

        final long ONE_MEGABYTE = 5000 * 5000;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }
}