package fr.projetl3.deltapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.BitmapFactory;
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

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.projetl3.deltapp.maths.CalculBasique;
import fr.projetl3.deltapp.maths.Equation2Degre;

public class Accueil extends AppCompatActivity {

    private ImageButton  login, menu;
    private EditText     inputCalc;
    private Button       camera_button, calc;
    private TextView     title, result;
    private ImageView    camera_capture, click_here, imageView;
    private ProgressBar  progressBar;
    private FirebaseUser user;
    private URI          UriSav;
    private boolean      isModuleSelected = false;
    private String       moduleSelected;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;

    public static final String LOG_TAG = "AndroidExample";


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

        List<Modules> modules = getListData();
        this.recyclerView = this.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new CustomRecyclerViewAdapter(this, modules));

        // RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageView = this.findViewById(R.id.imageView_module);

        if(imageView != null)
            this.imageView.setImageResource(R.drawable.icons_basic_maths);

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

        /*basics.setOnClickListener(v -> {
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
        });*/

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
        camera_button  = (Button)      findViewById(R.id.camera_capture_button_accueil);
        click_here     = (ImageView)   findViewById(R.id.iv_click_here);

        title          = (TextView)    findViewById(R.id.tv_module_title);

        calc           = (Button)      findViewById(R.id.calc_button);
        result         = (TextView)    findViewById(R.id.tv_result);

        camera_capture = (ImageView)   findViewById(R.id.iv_camera_capture);
        progressBar    = (ProgressBar) findViewById(R.id.progressBarAnalyse);
    }

    private void switchUIvisibility(){
        if(isModuleSelected){
            inputCalc.setVisibility(View.VISIBLE);
            calc.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            click_here.setVisibility(View.VISIBLE);

        } else {
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
            String equationText = inputCalc.getText().toString().trim();
            switch (moduleSelected){
                case "Équation 2nd degré":
                    try {
                        progressBar.setVisibility(View.GONE);
                        camera_capture.setVisibility(View.GONE);
                        Equation2Degre eq   = new Equation2Degre(equationText, Accueil.this);
                        result.setText(eq.toString() + "\n" + eq.result());
                    }catch (Exception e){
                        Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "Calculs basique":
                    try {
                        progressBar.setVisibility(View.GONE);
                        camera_capture.setVisibility(View.GONE);
                        CalculBasique calculBasique = new CalculBasique(equationText);
                        result.setText(calculBasique.result());
                    }catch (Exception e){
                        Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
            Bitmap  captureImage      = BitmapFactory.decodeFile(UriSav.toString());
            TextRecognizer recognizer = TextRecognition.getClient();
            InputImage     inputImage = InputImage.fromBitmap(captureImage,0 );
            Task<Text>     result     = recognizer.process(inputImage)
                    .addOnSuccessListener(visionText -> {
                        String blockText = "";
                        for(Text.TextBlock tx : visionText.getTextBlocks()) {
                            blockText = tx.getText();
                            if(blockText.contains("=")){
                                break;
                            }
                        }
                        Toast.makeText(Accueil.this, "Succès OCR: " +blockText, Toast.LENGTH_LONG).show();
                        inputCalc.setText(blockText);
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

    private  File getOutputMediaFile() throws URISyntaxException {
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
        UriSav = new URI(mediaFile.getAbsolutePath());

        return mediaFile;
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


    private  List<Modules> getListData() {
        List<Modules> list = new ArrayList<>();

        Modules basique  = new Modules("Calculs basique", Uri.parse("android.resource://"+this.getPackageName()+"/drawable/icons_basic_maths").toString());
        Modules eq2nddeg = new Modules("Équation 2nd degré",  Uri.parse("android.resource://"+this.getPackageName()+"/drawable/icons_eq2degre").toString());

        list.add(basique);
        list.add(eq2nddeg);



        return list;
    }

}