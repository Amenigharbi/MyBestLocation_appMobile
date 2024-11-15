package com.example.location.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.location.Config;
import com.example.location.Position;
import com.example.location.R;
import com.example.location.databinding.FragmentHomeBinding;
import com.example.location.DetailsActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.location.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ArrayList<Position> data = new ArrayList<>();
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Button click to start download
        binding.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Download d = new Download();
                d.execute(); // Lancer la tâche asynchrone
            }
        });

        // Configure the ListView item click listener
        binding.lv.setOnItemClickListener((parent, view, position, id) -> {
            // On récupère la position sélectionnée
            Position selectedPosition = data.get(position);

            // Créer un intent pour ouvrir DetailsActivity
            Intent intent = new Intent(getActivity(), DetailsActivity.class);

            // Passer les informations de la position via Intent (par exemple, l'id, latitude, longitude)
            intent.putExtra("idposition", selectedPosition.getIdposition());
            intent.putExtra("pseudo", selectedPosition.getPseudo());

            // On passe directement les coordonnées sans les nettoyer
            String latitude = selectedPosition.getLatitude();  // Ne pas nettoyer ici
            String longitude = selectedPosition.getLongitude(); // Ne pas nettoyer ici

            // Passer les coordonnées
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);

            intent.putExtra("numero", selectedPosition.getNumero());

            // Démarrer DetailsActivity
            startActivity(intent);
        });

        binding.addPosition.setOnClickListener(view -> {
            // Créez une intention pour ouvrir DetailsActivity sans passer de données supplémentaires
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class Download extends AsyncTask<Void, Void, List<Position>> {

        @Override
        protected List<Position> doInBackground(Void... voids) {
            JSONParser parser = new JSONParser();
            JSONObject response = parser.makeRequest(Config.URL_get_all);
            List<Position> data = new ArrayList<>();

            if (response != null) {
                try {
                    int success = response.getInt("success");
                    if (success > 0) {
                        JSONArray tableau = response.getJSONArray("positions");
                        for (int i = 0; i < tableau.length(); i++) {
                            JSONObject ligne = tableau.getJSONObject(i);
                            int idposition = ligne.getInt("idposition");
                            String pseudo = ligne.getString("pseudo");
                            String longitude = ligne.getString("longitude");
                            String latitude = ligne.getString("latitude");
                            String numero = ligne.getString("numero"); // Extraction du champ numero
                            data.add(new Position(idposition, pseudo, longitude, latitude, numero)); // Ajout du numero
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Download", "Error parsing JSON data", e);
                }
            } else {
                Log.e("Download", "Response from server is null");
            }
            return data;
        }

        @Override
        protected void onPostExecute(List<Position> result) {
            if (result.isEmpty()) {
                Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
            } else {
                data = new ArrayList<>(result);
                binding.lv.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, result));

                // Vous pouvez également vérifier ici les coordonnées des positions
                for (Position position : result) {
                    Log.d("HomeFragment", "Position ID: " + position.getIdposition() + ", Lat: " + position.getLatitude() + ", Lon: " + position.getLongitude());
                }
            }
        }
    }
}
