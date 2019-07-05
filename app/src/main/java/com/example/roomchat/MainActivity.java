package com.example.roomchat;

import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomchat.Adapters.Room_ListAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Room_ListAdapter.OnNoteListener {

    ArrayList<String > roomNames;
    RecyclerView roomlist;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference dataRef;
    FirebaseUser user;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(this,"Search",Toast.LENGTH_LONG).show();
                return true;
            case R.id.creategroup:
                //Toast.makeText(this,"Group Created",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this,CreateRoom.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                Toast.makeText(this,"Logout",Toast.LENGTH_LONG).show();
                signOut();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //For custom toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(myToolbar);

        //Room Names

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dataRef = database.getReference().child("user").child(getId(user.getEmail()));


        roomNames = new ArrayList<>();
        roomlist = findViewById(R.id.roomlist);


        roomlist.setHasFixedSize(true);

        //layout manager
        layoutManager = new LinearLayoutManager(this);
        roomlist.setLayoutManager(layoutManager);

//        roomNames = new String[2];
//        roomNames[0] = "Hello";
//        roomNames[1] = "Goodbye";

//        Toast.makeText(this,roomNames[0],Toast.LENGTH_LONG).show();



        //Adapter set
        mAdapter = new Room_ListAdapter(roomNames, this);
        roomlist.setAdapter(mAdapter);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomNames.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String room = postSnapshot.getValue(String.class);
                    roomNames.add(room);
                    mAdapter.notifyDataSetChanged();
                }
                //Collections.reverse(messageArray);
                mAdapter.notifyDataSetChanged();
                //roomlist.scrollToPosition(messageArray.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNoteClick(int position) {
//        Intent intent = new Intent(this,);
//        startActivity(intent);
        Toast.makeText(this,position+"",Toast.LENGTH_LONG).show();
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this,Splash.class);
                        startActivity(intent);
                    }
                });
        // [END auth_fui_signout]
    }

    String getId(String email){
        String id = "";
        for(int i = 0;i<email.length();i++){
            if(email.charAt(i) == '@'){
                break;
            }
            id += email.charAt(i);
        }

        return id;
    }

}
