package com.dhc3800.mp5;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private GeofencingClient geofencingClient;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SetLocation> locationList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recylerView);
        locationList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new SetLocationAdapter(locationList);
        recyclerView.setAdapter(mAdapter);




        geofencingClient = LocationServices.getGeofencingClient(this);





    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Description");
        SharedPreferences sPref = this.getPreferences(Context.MODE_PRIVATE);
        String search = sPref.getString("search", "");
        if (search != "") {
            searchView.setQuery(search, false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();
                filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();

                filter(newText);
                return false;
            }
        });
        return true;

    }

    public void filter(String text) {

    }
}
