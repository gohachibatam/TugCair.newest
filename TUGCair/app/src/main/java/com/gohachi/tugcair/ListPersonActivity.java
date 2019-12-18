package com.gohachi.tugcair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class ListPersonActivity extends AppCompatActivity {

    private RecyclerView mPersonList;
    private FirebaseFirestore mFirestore;


    private PersonListAdapter personListAdapter;
    private List<Person> personsList;

    private static final String TAG = "ListPersonActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_person);

        mFirestore = FirebaseFirestore.getInstance();
        personsList = new ArrayList<>();
        personListAdapter = new PersonListAdapter(getApplicationContext(), personsList);

        mPersonList = findViewById(R.id.person_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mPersonList.setHasFixedSize(true);
        mPersonList.setLayoutManager(manager);
        mPersonList.setAdapter(personListAdapter);

        //TODO : RefreshData
        UpdateDataPerson();
    }

    public void UpdateDataPerson(){
        if (personsList.size() > 0)
            personsList.clear();

        mFirestore.collection("persons").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "error : " + e.getMessage());
                }

                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                        String full_name = doc.getDocument().getString("fullname");
//                        String noktp = doc.getDocument().getString("noktp");
//                        String address = doc.getDocument().getString("address");
//                        String no_phone = doc.getDocument().getString("no_phone");
//                        String locationcoord = doc.getDocument().getString("locationcoord");

                        String personId = doc.getDocument().getId();
                        Person persons = doc.getDocument().toObject(Person.class).withId(personId);
                        personsList.add(persons);

                        personListAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    private void redirectPage(Activity activity, Class goTo) {
        Intent intent = new Intent(activity, goTo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            redirectPage(ListPersonActivity.this, DashboardActitvity.class);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
