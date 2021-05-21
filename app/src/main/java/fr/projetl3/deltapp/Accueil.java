package fr.projetl3.deltapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.projetl3.deltapp.maths.Equation2Degre;

public class Accueil extends AppCompatActivity {


    private ImageButton login, menu, basics, eq2nd;
    private EditText inputCalc;
    private Button camera_button, calc;
    private TextView title, basics_title, eq2nd_title, result;
    private ImageView camera_capture, click_here;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean isModuleSelected = false;
    private String moduleSelected;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setupUI();
        if(ContextCompat.checkSelfPermission(Accueil.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Accueil.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    startActivity(new Intent(getApplicationContext(), Account.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginApp.class));
                }
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isModuleSelected){
                    // Prend photo blabla
                    Toast.makeText(Accueil.this, "Wow une photo!", Toast.LENGTH_SHORT).show();
                    turnCameraON();
                } else {
                    Toast.makeText(Accueil.this, "Vous devez sélectionner un module avant de prendre en photo!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isModuleSelected){
                    // Ouvre cameré et caches la selection des modules.
                    Toast.makeText(Accueil.this, "GG chenapan", Toast.LENGTH_SHORT).show();
                    isModuleSelected = false;
                    switchUIvisibility();
                    moduleSelected = "SÉLECTION DU MODULE";
                    camera_button.setBackgroundResource(R.drawable.rounded_button_disabled);
                    title.setText(moduleSelected);
                } else {
                    Toast.makeText(Accueil.this, "Vous êtes déjà sur le menu de selection de module", Toast.LENGTH_SHORT).show();

                }
            }
        });

        basics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleSelected = "Calculs basique";
                isModuleSelected = true;
                switchUIvisibility();
                title.setText(moduleSelected);
                camera_button.setBackgroundResource(R.drawable.rounded_button);
            }
        });

        eq2nd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleSelected = "Équation 2nd degré";
                isModuleSelected = true;
                switchUIvisibility();
                title.setText(moduleSelected);
                camera_button.setBackgroundResource(R.drawable.rounded_button);
            }
        });

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = inputCalc.getText().toString().trim();
                if(inputText.isEmpty()){
                    Toast.makeText(Accueil.this, "Vous devez remplir la zone de texte", Toast.LENGTH_SHORT).show();
                } else {
                    calcul();
                }
            }
        });
    }


    private void setupUI(){
        login = (ImageButton) findViewById(R.id.login_button_accueil);
        menu = (ImageButton) findViewById(R.id.menu_button_accueil);
        basics = (ImageButton) findViewById(R.id.basic_maths_button);
        eq2nd = (ImageButton) findViewById(R.id.equation_2nd_button);
        camera_button = (Button) findViewById(R.id.camera_capture_button_accueil);
        click_here = (ImageView) findViewById(R.id.iv_click_here);

        title = (TextView) findViewById(R.id.tv_module_title);
        basics_title = (TextView) findViewById(R.id.tv_basic_maths);
        eq2nd_title = (TextView) findViewById(R.id.tv_equation_2nd);

        inputCalc = (EditText) findViewById(R.id.input_calc);
        calc = (Button) findViewById(R.id.calc_button);
        result = (TextView) findViewById(R.id.tv_result);

        camera_capture = (ImageView) findViewById(R.id.iv_camera_capture);
        progressBar = (ProgressBar) findViewById(R.id.progressBarAnalyse);

        basics.setVisibility(View.VISIBLE);
        basics_title.setVisibility(View.VISIBLE);
        eq2nd.setVisibility(View.VISIBLE);
        eq2nd_title.setVisibility(View.VISIBLE);
    }
    private void switchUIvisibility(){
        if(isModuleSelected){
            basics.setVisibility(View.GONE);
            basics_title.setVisibility(View.GONE);
            eq2nd.setVisibility(View.GONE);
            eq2nd_title.setVisibility(View.GONE);

            inputCalc.setVisibility(View.VISIBLE);
            calc.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            click_here.setVisibility(View.VISIBLE);

        } else {
            basics.setVisibility(View.VISIBLE);
            basics_title.setVisibility(View.VISIBLE);
            eq2nd.setVisibility(View.VISIBLE);
            eq2nd_title.setVisibility(View.VISIBLE);

            inputCalc.setVisibility(View.GONE);
            calc.setVisibility(View.GONE);
            result.setVisibility(View.GONE);
            camera_capture.setVisibility(View.GONE);
            click_here.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

    }
    private void calcul(){
        if(isModuleSelected){
            switch (moduleSelected){
                case "Équation 2nd degré":
                    try {
                        progressBar.setVisibility(View.GONE);
                        camera_capture.setVisibility(View.GONE);
                        String equationText = inputCalc.getText().toString().trim();
                        Equation2Degre eq = new Equation2Degre(equationText, Accueil.this);
                        result.setText(equationText + "\n" + eq.toString() + "\n" + eq.result());
                    }catch (Exception e){
                        Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Calculs basique":
                    Toast.makeText(Accueil.this, "En cours de developpement", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(Accueil.this, "Vous devez sélectionner un module avant de lancer un calcul!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(Accueil.this, "Vous devez sélectionner un module avant de prendre en photo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnCameraON(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            boolean created = false;
            created = savebitmap(captureImage);
            if(created){
                Toast.makeText(Accueil.this, "Photo enregistré! ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Accueil.this, "Le fichier n'existe pas", Toast.LENGTH_LONG).show();
            }


            camera_capture.setImageBitmap(captureImage);
            camera_capture.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            click_here.setVisibility(View.GONE);
        }
    }

    public boolean savebitmap(Bitmap image) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = openFileOutput("desiredFilename.png", Context.MODE_PRIVATE);

            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;
        } catch (Exception e) {
            Toast.makeText(Accueil.this, "Le fichier n'existe pas: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}