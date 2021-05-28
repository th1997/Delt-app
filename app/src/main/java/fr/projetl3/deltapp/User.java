package fr.projetl3.deltapp;

import android.net.Uri;

public class User {

    private String email;
    private String nom;
    private String prenom;

    private Uri    profilpics;

    public User(String email, String nom, String prenom) {
        this.email  = email;
        this.nom    = nom;
        this.prenom = prenom;
    }

    public User() {
        this.email  = "Sans email";
        this.nom    = "Sans nom";
        this.prenom = "Sans prenom";
        profilpics  = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setProfilpics(Uri profilpics) {
        this.profilpics = profilpics;
    }

    public Uri getProfilpics() {
        return profilpics;
    }
}
