package com.example.location;

public class Config {
    // Adresse IP correcte
    public static final String IP = "192.168.1.15:8080";

    // URLs avec l'adresse IP correspondante
    public static final String URL_get_all = "http://" + IP + "/servicephp/get_all.php";
    public static final String URL_add_pos = "http://" + IP + "/servicephp/add_position.php";
}
