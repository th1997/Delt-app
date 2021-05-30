package fr.projetl3.deltapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetl3.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import fr.projetl3.deltapp.maths.CalculBasique;
import fr.projetl3.deltapp.maths.Derive;
import fr.projetl3.deltapp.maths.Equation2Degre;
import fr.projetl3.deltapp.maths.Integrale;
import fr.projetl3.deltapp.recyclerViews.CustomRecyclerViewAdapter;

public class Accueil extends AppCompatActivity {

    private static final String SHARED_PREF = "fr.projetl3.deltapp.shared_pref";
    private static final String THEMES = "fr.projetl3.deltapp.themes";
    private boolean IS_DARK;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

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
    private ArrayList<HashMap<String, String>> last10;

    public static final String LOG_TAG = "AndroidExample";


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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        login.setOnClickListener(v -> {
            try {
                if(user != null)
                    startActivity(new Intent(getApplicationContext(), Account.class));
                else
                    startActivity(new Intent(getApplicationContext(), LoginApp.class));
            } catch (Exception e){
                Toast.makeText(Accueil.this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

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
                moduleSelected = "HOME";
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

    /*
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;

    }
    */

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
                            result.setText(eq.toString() + "\n" + eq.getResult());
                            savelast10("Equation 2nd degre", equationText);
                        }catch (Exception e){
                            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Calculs basique":
                        try {
                            camera_capture.setVisibility(View.GONE);
                            CalculBasique calculBasique = new CalculBasique(equationText);
                            result.setText(calculBasique.toString());
                            savelast10("Calculs basique", equationText);
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
                            savelast10("Derivation", equationText);
                        }catch (Exception e){
                            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Integrale":
                        try {
                            camera_capture.setVisibility(View.GONE);
                            Integrale integrale = new Integrale(equationText);
                            result.setText(integrale.toString());
                            savelast10("Integrale", equationText);
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
            //intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            int rotation = getImageOrientation(this, UriSav.toString());
            Toast.makeText(Accueil.this, "Rotation = " + rotation, Toast.LENGTH_LONG).show();
            TextRecognizer recognizer = TextRecognition.getClient();
            InputImage     inputImage = null;
            try {
                inputImage = InputImage.fromBitmap(captureImage, rotation); // ,getRotationCompensation(getCameraId(),this,true)
            } catch (Exception e) { // CameraAccessException
                e.printStackTrace();
            }
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

    private void saveArrayList(ArrayList<HashMap<String, String>> arrayList, DatabaseReference data){
        try {
            data.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(arrayList).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Toast.makeText(Accueil.this, "Sauvegarde réussi !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Accueil.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void savelast10(String name, String expression){
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
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put(name, expression);
                        arrayList.add(hashMap);
                        last10 = snapshot.getValue(al);
                        if (last10 != null) {
                            if (last10.size() == 10){last10.remove(9); }
                            arrayList.addAll(last10);
                            saveArrayList(arrayList, usersRef);
                        } else {
                            //Toast.makeText(Accueil.this, "Vous n'avez réalisé aucun calculs.", Toast.LENGTH_LONG).show();
                            saveArrayList(arrayList, usersRef);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Accueil.this, "Impossible de charger vos 10 dernier calculs!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e){
            Toast.makeText(Accueil.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // ROTATION :
    public int getImageOrientation(Context context, String imagePath) {
        int orientation = getOrientationFromExif(imagePath);
        if(orientation <= 0) {
            orientation = getOrientationFromMediaStore(context, imagePath);
        }

        return orientation;
    }

    private int getOrientationFromExif(String imagePath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;

                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    orientation = 0;

                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to get image exif orientation", e);
        }

        return orientation;
    }

    private int getOrientationFromMediaStore(Context context, String imagePath) {
        Uri imageUri = getImageContentUri(context, imagePath);
        if(imageUri == null) {
            return -1;
        }

        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);

        int orientation = -1;
        if (cursor != null && cursor.moveToFirst()) {
            orientation = cursor.getInt(0);
            cursor.close();
        }

        return orientation;
    }

    private Uri getImageContentUri(Context context, String imagePath) {
        String[] projection = new String[] {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + "=? ";
        String[] selectionArgs = new String[] {imagePath};
        Uri IMAGE_PROVIDER_URI = Uri.fromFile(new File(imagePath));
        Cursor cursor = context.getContentResolver().query(IMAGE_PROVIDER_URI, projection,
                selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            int imageId = cursor.getInt(0);
            cursor.close();

            return Uri.withAppendedPath(IMAGE_PROVIDER_URI, Integer.toString(imageId));
        }

        if (new File(imagePath).exists()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, imagePath);

            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return null;
    }

}
































