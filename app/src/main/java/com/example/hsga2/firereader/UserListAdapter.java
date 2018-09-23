package com.example.hsga2.firereader;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    //reference to dataset
    private ArrayList<String> users;

    //ViewHolder class to be used for each item in the list
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        //The textview within this layout
        public TextView tvUser;

        //constructor
        public UserViewHolder(View v){
            super(v);

            //Find the text view from the layout
            tvUser = v.findViewById(R.id.tvUser);
        }
    }

    //constructor
    public UserListAdapter(ArrayList<String> dataSet){
        users = dataSet;
    }

    //Create new views inflated from the layout file "user_text_view"
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_text_view, parent, false);

        //Create a new View Holder (from the inner class above)
        UserViewHolder holder = new UserViewHolder(v);

        //return the holder, as required by this method
        return holder;
    }

    //Replace the contents of a view        return holder;
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //get elt from your dataset at this position
        //replace the contents of the view with the elt

        //The User Info

        //TODO-- Figure out realtime updates, and be able to get that data in MainActivity.
        //TODO-- Then, find out what type of Object a query returns (a Collection?).
        //TODO-- Update this method based on that.  This should get each text object from
        //TODO-- a given user and write something like "Berto Gonzalez, born 1997".
        //DID IT!
        //Set the text view within the holder with text from the dataset.
        //users.get(position) is the formatted text from MainActivity and tvUser is the text view
        //in the holder defined above and passed through this constructor.
        holder.tvUser.setText(users.get(position));
    }

    //Return the size of the dataset
    @Override
    public int getItemCount() {
        return users.size();
    }

}
