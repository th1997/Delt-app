package com.example.projetl3;

import org.jetbrains.annotations.NotNull;

public class Utilisateur {
    public String email, nom, prenom;

    public Utilisateur(){
        this.email = "noemail";
        this.nom = "nom";
        this.prenom = "prenom";
    }

    public Utilisateur(String email, String nom, String prenom) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
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

    public String toString(){
        return "Email: " + email + " | Nom : " + prenom + " " + nom.toUpperCase();
    }
}
