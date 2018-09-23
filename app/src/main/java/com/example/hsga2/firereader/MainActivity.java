package com.example.hsga2.firereader;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    //declare firestore DB
    FirebaseFirestore db;

    //Layout items
    private Button createButton;
    private EditText etFirstName, etLastName, etBirthYear;

    //RecyclerView items
    private RecyclerView rvUsers;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    //Data
    private ArrayList<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Linking Layout Items
        createButton = (Button) findViewById(R.id.btnCreateUser);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etBirthYear = findViewById(R.id.etBirthYear);
        rvUsers = findViewById(R.id.rvUsers);

        //initialize the userList to a new empty ArrayList
        userList = new ArrayList<String>();

        //init firestore DB
        db = FirebaseFirestore.getInstance();

        //init recyclerView
        rvInit();

        //Get data drom db
        //getData();

        //Setup realtime snapshot listener
        initRealtime();

    }

    //Initialize RecyclerView for users
    private void rvInit(){
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvUsers.setHasFixedSize(true);

        //init Linear Layout Manager
        manager = new LinearLayoutManager(this);
        rvUsers.setLayoutManager(manager);

        //specify the adapter
        adapter = new UserListAdapter(userList);
        rvUsers.setAdapter(adapter);
    }

    //OnClick for create user button
    public void onClick(View view){

       //If the fields aren't empty...
        if (etFirstName.getText().equals("") || etLastName.getText().equals("") || etBirthYear.getText().equals("")){
            Toast.makeText(this, "Please insert user info before pressing create", Toast.LENGTH_SHORT).show();
        } else {
            //...Create a new user based on the data in the ETs
            Map<String, Object> user = new HashMap<>();
            user.put("first", etFirstName.getText().toString());
            user.put("last", etLastName.getText().toString());
            user.put("born", Integer.parseInt(etBirthYear.getText().toString()));

            //Add a new document with a generated ID
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this,
                                    "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();

                            //Once the doc has been uploaded, clear the Edit Texts
                            clearFields();
                        }
                    })
                    //If the upload failed...
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Notify the user
                            Toast.makeText(MainActivity.this,
                                    "Error adding document", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //Clear each of the Edit Texts
    private void clearFields(){
        etFirstName.setText("");
        etLastName.setText("");
        etBirthYear.setText("");
    }

    //Getting data from Firebase
    private void getData(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //On success, add each document in the collection from "db"
                            //into the userList.
                            for(QueryDocumentSnapshot document: task.getResult()){
                               userList.add("Name: " + document.getString("first") + " "
                                       + document.getString("last") + ", born: " +
                                       document.get("born"));

                               //Notify the adapter that the underlying dataset has changed.
                               adapter.notifyDataSetChanged();

                               //switched to notify dataset changed b/c is for more general case.
                               //adapter.notifyItemInserted(userList.size()-1);

                            }
                        } else {
                            //If the add failed, notify the user!
                            Toast.makeText(getApplicationContext(), "failed to get info from " +
                                    "fire base", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Create db listener for realtime updates
    private void initRealtime(){
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            //If we have an error, notify the log
                            Log.w("ERROR", "Listen Failed", e);
                            return;
                        }

                        //Notify the user that the listener (to "db") was added successfully
                        Toast.makeText(getApplicationContext(), "Listen Successful", Toast.LENGTH_LONG).show();

                        //Clear the userList in preparation for update
                        //this makes sure that no matter where any updates are placed,
                        //we don't end up with a problem when it comes to notifying the adapter.
                        userList.clear();
                        adapter.notifyDataSetChanged();

                        //For each document in "value" - the return of our query for the collection...
                        for (QueryDocumentSnapshot doc : value){

                            //Format the String and fill it with info from the user at "position"
                            userList.add("Name: " + doc.getString("first") + " "
                                    + doc.getString("last") + ", born: " +
                                    doc.get("born"));

                            //notify the adapter
                            adapter.notifyItemInserted(userList.size()-1);
                        }

                        //Notify the user that the update has been completed
                        Toast.makeText(getApplicationContext(), "Update Complete", Toast.LENGTH_LONG).show();
                    }
                });

    }

}
