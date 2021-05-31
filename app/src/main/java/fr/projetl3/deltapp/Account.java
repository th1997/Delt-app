package fr.projetl3.deltapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetl3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import fr.projetl3.deltapp.maths.CalculBasique;
import fr.projetl3.deltapp.maths.Derive;
import fr.projetl3.deltapp.maths.Equation2Degre;
import fr.projetl3.deltapp.maths.Integrale;
import fr.projetl3.deltapp.maths.SyntaxException;
import fr.projetl3.deltapp.recyclerViews.LastCalcAdapter;

public class Account extends AppCompatActivity {
    private ImageButton  retour;
    private Button       disconnect;
    private TextView     email, prenom, nom;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String       user_id,emailStr;
    private ImageView    profilpics;
    private RecyclerView recyclerView;
    private Spinner      spinner;
    private ArrayList<HashMap<String, String>> last10;
    private boolean mSpinnerInitialized;

    private static final String SHARED_PREF = "fr.projetl3.deltapp.shared_pref";
    private static final String THEMES = "fr.projetl3.deltapp.themes";
    private boolean IS_DARK;


    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        IS_DARK = preferences.getBoolean(THEMES, false);
        if(IS_DARK){ theme.applyStyle(R.style.daynight, true); } else { theme.applyStyle(R.style.light, true); }
        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSpinnerInitialized = false;
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences preferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                IS_DARK = preferences.getBoolean(THEMES, false);
                if (!mSpinnerInitialized) {
                    mSpinnerInitialized = true;
                    return;
                }
                String text = spinner.getSelectedItem().toString();
                if(text.equalsIgnoreCase("clair")){
                    if(IS_DARK){
                        setTheme(R.style.light);
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit();
                        editor.putBoolean(THEMES, false);
                        //Toast.makeText(Account.this, "Mode clair activé.", Toast.LENGTH_SHORT).show();
                        editor.apply();
                        reset();
                    }
                    return;
                } else if(text.equalsIgnoreCase("sombre")){
                    if(!IS_DARK){
                        setTheme(R.style.daynight);
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit();
                        editor.putBoolean(THEMES, true);
                        editor.apply();
                        IS_DARK = preferences.getBoolean(THEMES, false);
                        //Toast.makeText(Account.this, "Mode sombre activé. (ISDARK="+IS_DARK+")", Toast.LENGTH_SHORT).show();
                        reset();
                    }
                    return;
                }
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });


    }

    private void reset() {
        Intent intent = new Intent(getApplicationContext(), Account.class);
        startActivity(intent);
        finish();
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
                    emailStr = userProfile.getEmail();
                    String prenomProfile = userProfile.getPrenom();
                    String nomProfile    = userProfile.getNom();
                    importPics(emailStr);

                    email.setText("Adresse mail: " + emailStr);
                    prenom.setText("Prénom: " + prenomProfile);
                    nom.setText("Nom: " + nomProfile);
                    loadlast10();
                    loadSpinner();
                } else
                    Toast.makeText(Account.this, "Couldn't load user class profile!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(Account.this, "Couldn't load profile! ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSpinner() {
        if(IS_DARK){
            String[] arraySpinner = {"Thèmes appliqué : Sombre", "Clair"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } else {
            String[] arraySpinner = {"Thèmes appliqué : Clair", "Sombre"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

    }

    private void setupUI() {
        try {
            this.retour       = findViewById(R.id.btn_go_to_main);
            this.disconnect   = findViewById(R.id.btnDisconnect);
            this.email        = findViewById(R.id.tvEmailAccount);
            this.prenom       = findViewById(R.id.tvPrenomAccount);
            this.nom          = findViewById(R.id.tvNomAccount);
            this.profilpics   = findViewById(R.id.profilpics);
            this.recyclerView = findViewById(R.id.rv10Last);
            this.spinner      = findViewById(R.id.themes);
        } catch (Exception e){
            Toast.makeText(Account.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


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
            savePics(selectedImage,emailStr);
        }
    }

    private void savePics(Uri u, String email){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ImagesRef = storageRef.child("PhotoProfile/" +email +".jpg");
        ImagesRef.getName().equals(ImagesRef.getName());    // true
        ImagesRef.getPath().equals(ImagesRef.getPath());    // false
        ImagesRef.putFile(u);

    }

    private void importPics(String email){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://deltapp-800f8.appspot.com/PhotoProfile/" +email +".jpg");

        final long ONE_MEGABYTE = 5000 * 5000;

        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            profilpics.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));//(gsReference.getPath()));
        }).addOnFailureListener(exception -> {
        });

    }

    private void loadlast10(){
        try {
            if(user != null){
                last10 = new ArrayList<>();
                String user_id = user.getUid();
                DatabaseReference rootRef  = FirebaseDatabase.getInstance().getReference();
                DatabaseReference usersRef = rootRef.child("last10");

                usersRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<HashMap<String, String>>> al = new GenericTypeIndicator<ArrayList<HashMap<String, String>>>(){};
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        HashMap<String, String> hashMap;
                        HashMap<String, String> hashMap2;

                        String moduleName, formule, resultat;
                        last10 = snapshot.getValue(al);
                        try {
                            if (last10 != null) {
                                for(int i = 0; i < last10.size(); i++){
                                    hashMap = last10.get(i);
                                    hashMap2 = new HashMap<>();
                                    if(hashMap.containsKey("Calculs basique")){
                                        moduleName = "Calculs basique";
                                        formule = hashMap.get(moduleName);
                                        CalculBasique cb = new CalculBasique(formule);
                                        resultat = String.valueOf(cb.getResult());
                                    } else if(hashMap.containsKey("Equation 2nd degre")){
                                        moduleName = "Equation 2nd degre";
                                        formule = hashMap.get(moduleName);
                                        Equation2Degre eq = new Equation2Degre(formule);
                                        resultat = eq.getResult();
                                    } else if(hashMap.containsKey("Derivation")){
                                        moduleName = "Derivation";
                                        formule = hashMap.get(moduleName);
                                        Derive derive = new Derive(formule);
                                        assert formule != null;
                                        formule = "f(" + derive.getSymbole() + ") = ".concat(formule);
                                        resultat = "f'(" + derive.getSymbole() + ") = ".concat(derive.getResult());
                                    } else if(hashMap.containsKey("Integrale")){
                                        moduleName = "Integrale";
                                        formule = hashMap.get(moduleName);
                                        try {
                                            Integrale integrale = new Integrale(formule);
                                            resultat = String.valueOf(integrale.getResult());
                                        } catch (SyntaxException e) {
                                            moduleName = "";
                                            formule = "";
                                            resultat = "";
                                        }

                                    } else {
                                        moduleName = "";
                                        formule = "";
                                        resultat = "";
                                    }
                                    hashMap2.put("moduleName", moduleName);
                                    hashMap2.put("formule", formule);
                                    hashMap2.put("resultat", resultat);
                                    arrayList.add(hashMap2);
                                }
                                recyclerView.setAdapter(new LastCalcAdapter(arrayList, Account.this));

                                // RecyclerView scroll vertical
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Account.this, LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(linearLayoutManager);

                            } else {
                                Toast.makeText(Account.this, "Vous n'avez réalisé aucun calculs.", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(Account.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Account.this, "Impossible de charger vos 10 dernier calculs!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e){
            Toast.makeText(Account.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}