package com.example.location.ui.home;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.location.Config;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.location.JSONParser;
import com.example.location.Position;
import com.example.location.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ArrayList<Position> data=new ArrayList<Position>();

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Download d = new Download();
                d.execute(); // Lancer la tâche asynchrone
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public class Download extends AsyncTask<Void, Void, List<Position>> {
        AlertDialog alertDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

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
                            String numero = ligne.getString("numero");  // Extraction du champ numero
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
                binding.lv.setAdapter(new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        result
                ));
            }

        }
    }

}



















