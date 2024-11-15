package com.example.location;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class DetailsActivity extends AppCompatActivity {

    private TextView tvPseudo, tvLongitude, tvLatitude, tvNumero;
    private Button btnBack, btnMap, btnAdd,btnAddPosition,btnDelete,btnSendSms;
    private EditText etPseudo, etLongitude, etLatitude, etNumero; // Declare EditText fields
    private static final int MAP_REQUEST_CODE = 1;
    private static final String URL_GET_POSITION = "http://192.168.1.15:8080/servicephp/get_all.php"; // URL de votre API
    private Handler handler = new Handler();
    private Runnable updatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            // Mettre à jour la position toutes les 1 minute
            updatePosition();
            // Planifier la mise à jour après 1 minute (60 000 ms)
            handler.postDelayed(this, 60000); // 1 minute
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Annuler le Runnable pour éviter les fuites de mémoire lorsque l'activité est détruite
        handler.removeCallbacks(updatePositionRunnable);
    }
    private void updatePosition() {
        String idposition = getIntent().getStringExtra("idposition");
        Log.d("DetailsActivity", "Mise à jour de la position pour idposition: " + idposition);
        getPositionDetails(idposition); // Cette méthode fera une nouvelle requête API pour récupérer la position
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        handler.post(updatePositionRunnable);  // Lancer le Runnable au démarrage
        Log.d("DetailsActivity", "Handler started for position updates.");
        tvPseudo = findViewById(R.id.tvPseudo);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvNumero = findViewById(R.id.tvNumero);
        btnBack = findViewById(R.id.btnBack);
        btnMap = findViewById(R.id.btnMap);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddPosition = findViewById(R.id.btnAddPosition);
        // Initialize EditText fields
        etPseudo = findViewById(R.id.ps);
        etLongitude = findViewById(R.id.longu);
        etLatitude = findViewById(R.id.att);
        etNumero = findViewById(R.id.num);

        // Récupérer l'id de la position à partir des données de l'activité précédente
        String idposition = getIntent().getStringExtra("idposition");
        String pseudo = getIntent().getStringExtra("pseudo");
        String longitude = getIntent().getStringExtra("longitude");
        String latitude = getIntent().getStringExtra("latitude");
        String numero = getIntent().getStringExtra("numero");
        if (pseudo != null) etPseudo.setText(pseudo);
        if (longitude != null) etLongitude.setText(longitude);
        if (latitude != null) etLatitude.setText(latitude);
        if (numero != null) etNumero.setText(numero);

        // Récupérer les données depuis l'API
        getPositionDetails(idposition);

        // Actions des boutons
        btnBack.setOnClickListener(v -> finish());
        btnMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(DetailsActivity.this, MapActivity.class);
            mapIntent.putExtra("longitude", etLongitude.getText().toString());
            mapIntent.putExtra("latitude", etLatitude.getText().toString());
            startActivity(mapIntent);
        });
        btnAddPosition.setOnClickListener(v -> {
            Intent mapIntent = new Intent(DetailsActivity.this, MapActivity.class);
            startActivityForResult(mapIntent, MAP_REQUEST_CODE); // Ouvrir la carte pour sélectionner la position
        });

        btnAdd.setOnClickListener(v -> {
            String pseudoText = etPseudo.getText().toString().trim();
            String longitudeText = etLongitude.getText().toString().trim();
            String latitudeText = etLatitude.getText().toString().trim();
            String numeroText = etNumero.getText().toString().trim();
            // Vérification des champs
            if (pseudoText.isEmpty() || longitudeText.isEmpty() || latitudeText.isEmpty() || numeroText.isEmpty()) {
                Toast.makeText(DetailsActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            addPosition(pseudoText, longitudeText, latitudeText, numeroText);
        });
        btnDelete = findViewById(R.id.btnDelete);
        btnSendSms = findViewById(R.id.btnSendSms);
        btnDelete.setOnClickListener(v -> {
            findPositionId();
        });


        btnSendSms.setOnClickListener(v -> {
            String numeroT = etNumero.getText().toString().trim();
            if (!numeroT.isEmpty()) {
                sendSms(numeroT);
            } else {
                Toast.makeText(DetailsActivity.this, "Numéro invalide", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void deletePosition(String idposition) {
        String url = "http://192.168.1.15:8080/servicephp/delete_position.php?idposition=" + idposition;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int success = jsonResponse.getInt("success");

                        if (success == 1) {
                            Toast.makeText(DetailsActivity.this, "Position supprimée avec succès", Toast.LENGTH_SHORT).show();
                            finish(); // Retourner à l'activité précédente
                        } else {
                            Toast.makeText(DetailsActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Erreur de réponse JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(DetailsActivity.this, "Erreur de réseau", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void sendSms(String numero) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + numero));
        smsIntent.putExtra("sms_body", "Bonjour, voici un message depuis l'application.");
        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Aucune application SMS disponible", Toast.LENGTH_SHORT).show();
        }
    }
    private void findPositionId() {
        String pseudo = etPseudo.getText().toString().trim();
        String longitude = etLongitude.getText().toString().trim();
        String latitude = etLatitude.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();

        if (pseudo.isEmpty() || longitude.isEmpty() || latitude.isEmpty() || numero.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs pour rechercher l'ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.15:8080/servicephp/find_position.php?pseudo=" + pseudo +
                "&longitude=" + longitude + "&latitude=" + latitude + "&numero=" + numero;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int success = jsonResponse.getInt("success");

                        if (success == 1) {
                            String idposition = jsonResponse.getString("idposition");
                            deletePosition(idposition); // Appeler la méthode pour supprimer
                        } else {
                            Toast.makeText(DetailsActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Erreur de réponse JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(DetailsActivity.this, "Erreur de réseau", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK) {
            String latitude = data.getStringExtra("latitude");
            String longitude = data.getStringExtra("longitude");

            // Mettre à jour les EditText avec les nouvelles coordonnées
            etLatitude.setText(latitude);
            etLongitude.setText(longitude);
        }
    }

    private void addPosition(String pseudo, String longitude, String latitude, String numero) {
        String url = "http://192.168.1.15:8080/servicephp/add_position.php?pseudo=" + pseudo + "&longitude=" + longitude + "&latitude=" + latitude + "&numero=" + numero;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int success = jsonResponse.getInt("success");

                        if (success == 1) {
                            Toast.makeText(DetailsActivity.this, "Position ajoutée avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailsActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailsActivity.this, "Erreur de réponse JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(DetailsActivity.this, "Erreur de réseau", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }


    private void getPositionDetails(String idposition) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_POSITION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray positionsArray = jsonResponse.getJSONArray("positions");

                            for (int i = 0; i < positionsArray.length(); i++) {
                                JSONObject position = positionsArray.getJSONObject(i);
                                String id = position.getString("idposition");

                                // Afficher les données seulement si elles correspondent à l'ID
                                if (id.equals(idposition)) {
                                    tvPseudo.setText("Pseudo: " + position.getString("pseudo"));
                                    tvLongitude.setText("Longitude: " + position.getString("longitude"));
                                    tvLatitude.setText("Latitude: " + position.getString("latitude"));
                                    tvNumero.setText("numero: " + position.getString("numero"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailsActivity.this, "Erreur de récupération des données", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, error -> Toast.makeText(DetailsActivity.this, "Erreur de réseau", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
