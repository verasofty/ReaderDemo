package com.onsigna.readerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Movements extends AppCompatActivity {

    private static String TAG = Movements.class.getSimpleName();
    private ListView list;
    ArrayList<SubjectData> arrayList = new ArrayList<SubjectData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movements);
        setUp();
        actions();
    }

    private void setUp() {
        list = (ListView) findViewById(R.id.listView);
        arrayList.add(new SubjectData("JAVA", "https://www.tutorialspoint.com/java/",             "https://www.tutorialspoint.com/java/images/java-mini-logo.jpg"));
        arrayList.add(new SubjectData("Python", "https://www.tutorialspoint.com/python/", "https://www.tutorialspoint.com/python/images/python-mini.jpg"));
        arrayList.add(new SubjectData("Javascript", "https://www.tutorialspoint.com/javascript/", "https://www.tutorialspoint.com/javascript/images/javascript-mini-logo.jpg"));
        arrayList.add(new SubjectData("Cprogramming", "https://www.tutorialspoint.com/cprogramming/", "https://www.tutorialspoint.com/cprogramming/images/c-mini-logo.jpg"));
        arrayList.add(new SubjectData("Cplusplus", "https://www.tutorialspoint.com/cplusplus/", "https://www.tutorialspoint.com/cplusplus/images/cpp-mini-logo.jpg"));
        arrayList.add(new SubjectData("Android", "https://www.tutorialspoint.com/android/", "https://www.tutorialspoint.com/android/images/android-mini-logo.jpg"));
        CustomAdapter customAdapter = new CustomAdapter(this, arrayList);
        list.setAdapter(customAdapter);

        customAdapter.setOnClickListener(view -> {
            Log.d(TAG, "== customAdapter.setOnClickListener() ==");
            int position = (Integer) view.getTag();
            Log.d(TAG, "Nombre -> " + arrayList.get(position).SubjectName);
        });
    }

    private void actions() {


        list.setOnItemClickListener((adapterView, view, position, l) -> {
            Log.d(TAG, "== onItemClick() ==");
            Log.d(TAG, "Nombre -> " + arrayList.get(position).SubjectName);
        });

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "== onItemSelected() ==");
                Log.d(TAG, "Nombre -> " + arrayList.get(position).SubjectName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}