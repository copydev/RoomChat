package com.example.roomchat;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
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
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
//            case R.id.search:
//                Toast.makeText(this,"Search",Toast.LENGTH_LONG).show();
//                return true;
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
        Toolbar myToolbar = findViewById(R.id.toolbar_main_activity);
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

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("groups").child(roomNames.get(position));
        dr.push().setValue(user.getDisplayName() + " has entered the room.");
        Intent intent = new Intent(MainActivity.this,ChatRoom.class);
        intent.putExtra("roomname",roomNames.get(position));
        intent.putExtra("username",user.getDisplayName());
        startActivity(intent);

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

    public void search(final String search){
        Toast.makeText(getApplicationContext(),search,Toast.LENGTH_SHORT).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("groups");
        final DatabaseReference uref =FirebaseDatabase.getInstance().getReference().child("user").child(getId(user.getEmail()));
        final DatabaseReference reflistener = FirebaseDatabase.getInstance().getReference();



        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(search).exists()){



                    uref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int chk = 0;
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                if(data.getValue().equals(search)){
                                    chk = 1;
                                    break;
                                }
                            }

                            if(chk == 0) {
                                reflistener.child("user").child(getId(user.getEmail())).push().setValue(search);
                            }
                            reflistener.child("groups").child(search).push().setValue(user.getDisplayName() + " has entered the room.");
                            Intent intent = new Intent(MainActivity.this,ChatRoom.class);
                            intent.putExtra("username",user.getDisplayName());
                            intent.putExtra("roomname",search);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
                        }
                    });


                }
                else{
                    Toast.makeText(getApplicationContext(),"No such room is present.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
