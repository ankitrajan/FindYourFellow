package com.example.ankit.findyourfellow;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends ArrayAdapter {

    private FirebaseAuth mAuth;
    private List name = new ArrayList(); //name
    private List id = new ArrayList();

    public RequestAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    public void add(String object, String object2)
    {
        name.add(object);
        id.add(object2);
        super.add(object);
        super.add(object2);
    }

    static class RowHolder
    {
        TextView EMAIL;
        Button ACCEPT;
        Button DECLINE;
    }

    @Override
    public int getCount()
    {
        return this.name.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.name.get(position);
    }

    public Object getId(int position) { return this.id.get(position);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        row = convertView;
        RowHolder holder;

        final int currentPosition = position;

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, parent, false);

            holder = new RowHolder();

            holder.EMAIL = (TextView) row.findViewById(R.id.track_item_text);
            holder.ACCEPT = (Button) row.findViewById(R.id.list_item_accept);
            holder.DECLINE = (Button) row.findViewById(R.id.list_item_decline);

            holder.ACCEPT.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    mAuth = FirebaseAuth.getInstance();

                    String friendId = (String) getId(currentPosition);

                    String userId = mAuth.getCurrentUser().getUid().toString();

                    String userEmail = mAuth.getCurrentUser().getDisplayName().toString();

                    Firebase friendRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + friendId);

                    Firebase userRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + userId);

                    userRef.child("Friends").child(friendId).setValue((String) getItem(currentPosition));

                    friendRef.child("Friends").child(userId).setValue(userEmail);

                    userRef.child("FriendRequest").child(friendId).removeValue();

                    Toast.makeText(getContext(), "Friend added", Toast.LENGTH_SHORT).show();
                }
            });

            holder.DECLINE.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    mAuth = FirebaseAuth.getInstance();

                    String friendId = (String) getId(currentPosition);

                    String userId = mAuth.getCurrentUser().getUid().toString();

                    Firebase userRef = new Firebase("https://findyourfellow.firebaseio.com/Users/" + userId + "/FriendRequest/");

                    userRef.child(friendId).removeValue();

                    Toast.makeText(getContext(), "Friend declined", Toast.LENGTH_SHORT).show();
                }
            });

            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder) row.getTag();
        }

        String FR = (String) getItem(position);
        holder.EMAIL.setText(FR);

        return row;
    }
}
