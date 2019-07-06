package com.example.roomchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomchat.Adapters.Chat_ListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatRoom extends AppCompatActivity {

    TextView chat_header, send;
    EditText message;

    RecyclerView chatlistView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    String username;
    ArrayList<String> chatlist;

    DatabaseReference dataRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chat_header = findViewById(R.id.chat_header);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        chatlistView = findViewById(R.id.chatlist);

        Intent intent = getIntent();

        chat_header.setText(intent.getStringExtra("roomname"));
        username = intent.getStringExtra("username");

        //  Toast.makeText(this,"Username: "+ username +", Chat Header: "+ chat_header.getText().toString(),Toast.LENGTH_SHORT).show();
        chatlist = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        chatlistView.setLayoutManager(layoutManager);
        mAdapter = new Chat_ListAdapter(chatlist);
        chatlistView.setAdapter(mAdapter);

        //Extract Chat first
        //Then set in adapter

        dataRef = FirebaseDatabase.getInstance().getReference().child("groups").child(chat_header.getText().toString());
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlist.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String temp = data.getValue().toString();
                    chatlist.add(temp);
                    mAdapter.notifyDataSetChanged();
                }

                chatlistView.scrollToPosition(chatlist.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = "[" + username + "]: ";
                temp += message.getText().toString();
                addMessage(temp);
            }
        });

    }


    public void addMessage(String str){
        dataRef.push().setValue(str);
    }


}
