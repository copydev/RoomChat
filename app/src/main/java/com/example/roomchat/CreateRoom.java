package com.example.roomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateRoom extends AppCompatActivity {


    FirebaseUser user;

    TextView roomName, description;
    Button createRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        user = FirebaseAuth.getInstance().getCurrentUser();

        roomName = findViewById(R.id.roomname);
        description = findViewById(R.id.description);
        createRoom = findViewById(R.id.createRoomButton);

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = roomName.getText().toString();
                if(name == null || name.length() == 0){
                    Toast.makeText(CreateRoom.this,"Invalid Room Name",Toast.LENGTH_SHORT).show();
                }
                else{

                    //Check for duplicate groups
                    //ToDo

                    DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference().child("groups");

                    roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int chk = 1;
                            if(dataSnapshot.child(name).exists()){
                                chk = 0;
                            }
                            else{
                                chk =1;
                            }

                            if(chk == 0){
                                Toast.makeText(CreateRoom.this,"Room already exists.",Toast.LENGTH_SHORT).show();

                            }
                            else{
                                String id = getId(user.getEmail());

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getReference().child("user").child(id);

                                userRef.push().setValue(name);

                                DatabaseReference groupRef = database.getReference().child("groups").child(name);
                                groupRef.push().setValue(user.getDisplayName()+" has entered the chat.");

                                Intent intent = new Intent(CreateRoom.this, MainActivity.class);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
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
