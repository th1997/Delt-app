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
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import fr.projetl3.deltapp.maths.Derive;
import fr.projetl3.deltapp.maths.Equation2Degre;
import fr.projetl3.deltapp.maths.Integrale;

public class Accueil extends AppCompatActivity {

    private ImageButton  login, menu;
    private EditText     inputCalc;
    private Button       camera_button, calc;
    private TextView     title, result;
    private ImageView    camera_capture, click_here;
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
        try {
            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
            m.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupUI();
        checkPerm();
        switchUIvisibility();

        mAuth = FirebaseAuth.getInstance();
        user  = mAuth.getCurrentUser();

        List<Modules> modules = getListData();
        recyclerView.setAdapter(new CustomRecyclerViewAdapter(this, modules));

        // RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

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
                isModuleSelected = false;
                switchUIvisibility();
                moduleSelected = "SÉLECTION DU MODULE";
                camera_button.setBackgroundResource(R.drawable.rounded_button_disabled);
                title.setText(moduleSelected);
                inputCalc.setText("");
                result.setText("");
            } else
                Toast.makeText(Accueil.this, "Vous êtes déjà sur le menu de selection de module", Toast.LENGTH_SHORT).show();
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
        camera_button  = (Button)      findViewById(R.id.camera_capture_button_accueil);
        click_here     = (ImageView)   findViewById(R.id.iv_click_here);

        title          = (TextView)    findViewById(R.id.tv_module_title);

        calc           = (Button)      findViewById(R.id.calc_button);
        result         = (TextView)    findViewById(R.id.tv_result);

        inputCalc      = (EditText)    findViewById(R.id.input_calc);

        camera_capture = (ImageView)   findViewById(R.id.iv_camera_capture);
        recyclerView   = (RecyclerView) findViewById(R.id.recyclerView);

        try {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(1);
        } catch (Exception e){
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void switchUIvisibility(){
        if(isModuleSelected){
            inputCalc.setVisibility(View.VISIBLE);
            calc.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            click_here.setVisibility(View.VISIBLE);

            recyclerView.setVisibility(View.GONE);
            recyclerView.setAlpha(0);

        } else {
            inputCalc.setVisibility(View.GONE);
            calc.setVisibility(View.GONE);
            result.setVisibility(View.GONE);
            camera_capture.setVisibility(View.GONE);
            click_here.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(1);
        }

    }

    private void calcul(){
        try {
            if(isModuleSelected){
                String equationText = inputCalc.getText().toString().trim();
                switch (moduleSelected){
                    case "Equation 2nd degre":
                        try {
                            camera_capture.setVisibility(View.GONE);
                            Equation2Degre eq   = new Equation2Degre(equationText);
                            Toast.makeText(Accueil.this,"Map: " +  eq.getPolynome().getCoefficientPolynome().toString(), Toast.LENGTH_SHORT).show();
                            result.setText(eq.toString() + "\n" + eq.result());

                        }catch (Exception e){
                            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Calculs basique":
                        try {
                            camera_capture.setVisibility(View.GONE);
                            CalculBasique calculBasique = new CalculBasique(equationText);
                            result.setText(calculBasique.toString());
                        }catch (Exception e){
                            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Derivation":
                        try {
                            camera_capture.setVisibility(View.GONE);
                            Derive derive = new Derive(equationText);
                            String text = "f(" + derive.getSymbole() + ") = " + derive.getFonction() + "\nf'(" + derive.getSymbole() + ") = " + derive.getResult();
                            result.setText(text);
                        }catch (Exception e){
                            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Integrale":
                        try {
                            camera_capture.setVisibility(View.GONE);
                            Integrale integrale = new Integrale(equationText);
                            result.setText(integrale.toString());
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
        } catch (Exception e){
            Toast.makeText(Accueil.this, "Erreur = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(Accueil.this, "Erreur OCR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

            camera_capture.setImageBitmap(captureImage);
            camera_capture.setVisibility(View.VISIBLE);
            click_here.setVisibility(View.GONE);
        }
       // if ()
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

        list.add(new Modules("Calculs basique",this));
        list.add(new Modules("Equation 2nd degre",this));
        list.add(new Modules("Derivation",this));
        list.add(new Modules("Integrale",this));

        return list;
    }

    public void selectModule(String moduleName) {
        moduleSelected   = moduleName;
        isModuleSelected = true;
        switchUIvisibility();
        title.setText(moduleName);
        camera_button.setBackgroundResource(R.drawable.rounded_button);
    }
}