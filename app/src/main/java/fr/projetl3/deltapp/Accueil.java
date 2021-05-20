package fr.projetl3.deltapp;

import androidx.appcompat.app.AppCompatActivity;
import com.example.projetl3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import fr.projetl3.deltapp.maths.Equation2Degre;

public class Accueil extends AppCompatActivity {

    private ImageButton login, menu, basics, eq2nd;
    private EditText inputCalc;
    private Button camera, calc;
    private TextView title, basics_title, eq2nd_title, result;

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

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isModuleSelected){
                    // Prend photo blabla
                    Toast.makeText(Accueil.this, "Wow une photo!", Toast.LENGTH_SHORT).show();
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
                    camera.setBackgroundResource(R.drawable.rounded_button_disabled);
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
                camera.setBackgroundResource(R.drawable.rounded_button);
            }
        });

        eq2nd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleSelected = "Équation 2nd degré";
                isModuleSelected = true;
                switchUIvisibility();
                title.setText(moduleSelected);
                camera.setBackgroundResource(R.drawable.rounded_button);
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
        camera = (Button) findViewById(R.id.camera_capture_button_accueil);

        title = (TextView) findViewById(R.id.tv_module_title);
        basics_title = (TextView) findViewById(R.id.tv_basic_maths);
        eq2nd_title = (TextView) findViewById(R.id.tv_equation_2nd);

        inputCalc = (EditText) findViewById(R.id.input_calc);
        calc = (Button) findViewById(R.id.calc_button);
        result = (TextView) findViewById(R.id.tv_result);

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

        } else {
            basics.setVisibility(View.VISIBLE);
            basics_title.setVisibility(View.VISIBLE);
            eq2nd.setVisibility(View.VISIBLE);
            eq2nd_title.setVisibility(View.VISIBLE);

            inputCalc.setVisibility(View.GONE);
            calc.setVisibility(View.GONE);
            result.setVisibility(View.GONE);
        }

    }

    private void calcul(){
        if(isModuleSelected){
            switch (moduleSelected){
                case "Équation 2nd degré":
                    try {
                        String equationText = inputCalc.getText().toString().trim();
                        Equation2Degre eq = new Equation2Degre(equationText);
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
}