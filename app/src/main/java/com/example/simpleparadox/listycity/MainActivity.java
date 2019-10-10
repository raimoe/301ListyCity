package com.example.simpleparadox.listycity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Declare the variables so that you will be able to reference it later.
    ListView cityList;
    ArrayAdapter<City> cityAdapter;
    ArrayList<City> cityDataList;
    CustomList customList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String TAG = "Sample";
        Button addCityButton;
        final Button delCityButton;
        final EditText delCityEditText;
        final EditText addCityEditText;
        final EditText addProvinceEditText;
        FirebaseFirestore db;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addCityButton = findViewById(R.id.add_city_button);
        delCityButton = findViewById(R.id.del_city_button);
        delCityEditText = findViewById(R.id.del_city_field);
        addCityEditText = findViewById(R.id.add_city_field);
        addProvinceEditText = findViewById(R.id.add_province_edit_text);
        cityList = findViewById(R.id.city_list);

        cityDataList = new ArrayList<>();

        cityAdapter = new CustomList(MainActivity.this, cityDataList);

        cityList.setAdapter(cityAdapter);
        // Access a cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        // Get a top-level reference to the collection
        final CollectionReference collectionReference = db.collection("Cities");

        delCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityName = delCityEditText.getText().toString();
                if (cityName.length() > 0){

                    collectionReference
                            .document(cityName)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });
                    // Setting the fields to null so the user can add a new city
                    delCityEditText.setText("");
                }
            }
        });

        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cityName = addCityEditText.getText().toString();
                final String provinceName = addProvinceEditText.getText().toString();
                HashMap<String,String> data = new HashMap<>();
                if (cityName.length() >0 && provinceName.length() >0){
                    // If there is some data in the EditText field, then we create a new key-value pair
                    data.put("province_name",provinceName);
                    //The set methods sets a unique id for the document
                    collectionReference
                            .document(cityName)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // These are a method which gets executed when the task is successful
                                    Log.d(TAG, "Data addition successful");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //This method gets executed if there is any problem
                                    Log.d(TAG, "Data addition failed" + e.toString());
                                }
                            });
                    // Setting the fields to null so the user can add a new city
                    addCityEditText.setText("");
                    addProvinceEditText.setText("");
                }
            }
        });
    collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            //clear the old list
            cityDataList.clear();
            for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                Log.d(TAG, String.valueOf(doc.getData().get("province_name:")));
                String city = doc.getId();
                String province = (String) doc.getData().get("province_name:");
                cityDataList.add(new City(city, province)); //Adding the cities and provinces from FireStore
            }
         cityAdapter.notifyDataSetChanged();//Notifying the adapter to render any new data fetched from the cloud
        }
    });

    }

}
