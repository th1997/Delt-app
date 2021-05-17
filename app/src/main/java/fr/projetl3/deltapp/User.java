package fr.projetl3.deltapp;

public class User {

    public String email;
    public String nom;
    public String prenom;

    public User(String email, String nom, String prenom) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
    }

    public User() {
        this.email = "Sans email";
        this.nom = "Sans nom";
        this.prenom = "Sans prenom";
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
}
