package com.example.location;

public class Position {
    int idposition;
    String pseudo, longitude, latitude;
    String numero; // Nouveau champ

    // Constructeur avec idposition
    public Position(int idposition, String pseudo, String longitude, String latitude, String numero) {
        this.idposition = idposition;
        this.pseudo = pseudo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.numero = numero;
    }

    // Constructeur sans idposition
    public Position(String pseudo, String longitude, String latitude, String numero) {
        this.pseudo = pseudo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.numero = numero;
    }

    @Override
    public String toString() {
        return pseudo + " (" + longitude + ", " + latitude + ") - " + numero;
    }

    // Getters et Setters si nécessaire
    public int getIdposition() {
        return idposition;
    }

    public void setIdposition(int idposition) {
        this.idposition = idposition;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
