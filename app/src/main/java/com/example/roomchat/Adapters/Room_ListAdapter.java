package com.example.roomchat.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.roomchat.R;

import java.util.ArrayList;

public class Room_ListAdapter extends RecyclerView.Adapter<Room_ListAdapter.MyViewHolder> {
    private ArrayList<String > mDataset;
    private OnNoteListener onNoteListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView name;
        public TextView firstletter;
        OnNoteListener onNoteListener;

        public MyViewHolder(View v, OnNoteListener onNoteListener) {
            super(v);
            name = v.findViewById(R.id.name);
            firstletter = v.findViewById(R.id.firstletter);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public Room_ListAdapter(ArrayList<String > myDataset, OnNoteListener onNoteListener) {
        mDataset = myDataset;
        this.onNoteListener = onNoteListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Room_ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_listview, parent, false);
        MyViewHolder vh = new MyViewHolder(v,onNoteListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(mDataset.get(position));
        if(mDataset.get(position) != null) {
            holder.firstletter.setText(Character.toString(mDataset.get(position) .charAt(0)));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset == null){
            return 0;
        }
        return mDataset.size();
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
