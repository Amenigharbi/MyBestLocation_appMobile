package com.example.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Récupérer les données latitude et longitude
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");

        // Si aucune coordonnée n'est fournie, on définit une position par défaut (ex: Paris)
        if (latitude == null || longitude == null) {
            latitude = "35.8256"; // Latitude de Paris
            longitude = "10.63699"; // Longitude de Paris
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnAddPosition = findViewById(R.id.btnPosition);
        btnAddPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude != null && longitude != null) {
                    // Créer une intention pour passer les coordonnées à DetailsActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latitude", latitude);
                    resultIntent.putExtra("longitude", longitude);
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Ferme MapActivity et retourne à DetailsActivity
                } else {
                    Toast.makeText(MapActivity.this, "Veuillez d'abord sélectionner une position", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Convertir les coordonnées en double
        double lat = Double.parseDouble(latitude);
        double lng = Double.parseDouble(longitude);

        // Définir la position initiale sur la carte
        LatLng position = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(position).title("Position sélectionnée"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        // Définir le comportement lorsqu'on clique sur la carte pour sélectionner une nouvelle position
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Met à jour les coordonnées avec la position sélectionnée
                latitude = String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);

                // Effacer le précédent marqueur et en ajouter un nouveau
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Nouvelle position"));

                // Afficher les nouvelles coordonnées dans un Toast
                Toast.makeText(MapActivity.this, "Coordonnées: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
