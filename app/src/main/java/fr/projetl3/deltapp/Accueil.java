package fr.projetl3.deltapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.projetl3.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.projetl3.deltapp.maths.Equation2Degre;

public class Accueil extends AppCompatActivity {

    private ImageButton  login, menu, basics, eq2nd;
    private EditText     inputCalc;
    private Button       camera_button, calc;
    private TextView     title, basics_title, eq2nd_title, result;
    private ImageView    camera_capture, click_here;
    private ProgressBar  progressBar;
    private FirebaseUser user;
    private boolean      isModuleSelected = false;
    private String       moduleSelected;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mAuth = FirebaseAuth.getInstance();
        user  = mAuth.getCurrentUser();

        setupUI();
        checkPerm();

        login.setOnClickListener(v -> {
            if(user != null)
                startActivity(new Intent(getApplicationContext(), Account.class));
            else
                startActivity(new Intent(getApplicationContext(), LoginApp.class));
        });

        camera_button.setOnClickListener(v -> {
            if(isModuleSelected)
                turnCameraON();
            else
                Toast.makeText(Accueil.this, "Vous devez sélectionner un module avant de prendre en photo!", Toast.LENGTH_SHORT).show();
        });

        menu.setOnClickListener(v -> {
            if(isModuleSelected){
                // Ouvre camera et caches la selection des modules.
                Toast.makeText(Accueil.this, "GG chenapan", Toast.LENGTH_SHORT).show();
                isModuleSelected = false;
                switchUIvisibility();
                moduleSelected = "SÉLECTION DU MODULE";
                camera_button.setBackgroundResource(R.drawable.rounded_button_disabled);
                title.setText(moduleSelected);
            } else
                Toast.makeText(Accueil.this, "Vous êtes déjà sur le menu de selection de module", Toast.LENGTH_SHORT).show();
        });

        basics.setOnClickListener(v -> {
            moduleSelected   = "Calculs basique";
            isModuleSelected = true;
            switchUIvisibility();
            title.setText(moduleSelected);
            camera_button.setBackgroundResource(R.drawable.rounded_button);
        });

        eq2nd.setOnClickListener(v -> {
            moduleSelected   = "Équation 2nd degré";
            isModuleSelected = true;
            switchUIvisibility();
            title.setText(moduleSelected);
            camera_button.setBackgroundResource(R.drawable.rounded_button);
        });

        calc.setOnClickListener(v -> {
            String inputText = inputCalc.getText().toString().trim();

            if(inputText.isEmpty())
                Toast.makeText(Accueil.this, "Vous devez remplir la zone de texte", Toast.LENGTH_SHORT).show();
            else
                calcul();
        });
    }

    private void setupUI(){
        login          = (ImageButton) findViewById(R.id.login_button_accueil);
        menu           = (ImageButton) findViewById(R.id.menu_button_accueil);
        basics         = (ImageButton) findViewById(R.id.basic_maths_button);
        eq2nd          = (ImageButton) findViewById(R.id.equation_2nd_button);
        camera_button  = (Button)      findViewById(R.id.camera_capture_button_accueil);
        click_here     = (ImageView)   findViewById(R.id.iv_click_here);

        title          = (TextView)    findViewById(R.id.tv_module_title);
        basics_title   = (TextView)    findViewById(R.id.tv_basic_maths);
        eq2nd_title    = (TextView)    findViewById(R.id.tv_equation_2nd);

        inputCalc      = (EditText)    findViewById(R.id.input_calc);
        calc           = (Button)      findViewById(R.id.calc_button);
        result         = (TextView)    findViewById(R.id.tv_result);

        camera_capture = (ImageView)   findViewById(R.id.iv_camera_capture);
        progressBar    = (ProgressBar) findViewById(R.id.progressBarAnalyse);

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
                        Equation2Degre eq   = new Equation2Degre(equationText, Accueil.this);
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
        try {
            File file = getOutputMediaFile();
            Uri uri = Uri.fromFile(file);
            //Uri imageUri = Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + "saved.png"));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 100);
        } catch (Exception e){
            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            assert data != null;

            Bitmap  captureImage = (Bitmap) data.getExtras().get("data");
            File    file         = getOutputMediaFile();
            /*
            boolean created      = savebitmap(captureImage, file);

            if(created){
                assert file != null;
                Toast.makeText(Accueil.this, "Photo enregistré! " + file.getPath(), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(Accueil.this, "Le fichier n'existe pas", Toast.LENGTH_LONG).show();
            */
            TextRecognizer recognizer = TextRecognition.getClient();
            InputImage     inputImage = InputImage.fromBitmap(captureImage,0 );
            Task<Text>     result     = recognizer.process(inputImage)
                            .addOnSuccessListener(visionText -> {
                                Toast.makeText(Accueil.this, "Succès OCR: " + visionText.getText(), Toast.LENGTH_LONG).show();
                                inputCalc.setText(visionText.getText());
                                progressBar.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(
                                    e -> {
                                        Toast.makeText(Accueil.this, "Erreur OCR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    });

            camera_capture.setImageBitmap(captureImage);
            camera_capture.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            click_here.setVisibility(View.GONE);
        }
    }

    private  File getOutputMediaFile(){
        File   mediaFile;
        @SuppressLint("SimpleDateFormat")
        String timeStamp       = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String mImageName      = "MI_"+ timeStamp +".png";
        File   mediaStorageDir = new File(getExternalFilesDir(null),"");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdirs())
                return null;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private boolean savebitmap(Bitmap image, File pictureFile) {
        if (pictureFile == null) {
            Toast.makeText(Accueil.this, "Error creating media file, check storage permissions: ", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            Toast.makeText(Accueil.this, "File not found: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(Accueil.this, "Error accessing file:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void checkPerm(){
        if(ContextCompat.checkSelfPermission(Accueil.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Accueil.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
        if(ContextCompat.checkSelfPermission(Accueil.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Accueil.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }
}