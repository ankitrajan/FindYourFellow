package com.example.ankit.findyourfellow;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends ArrayAdapter{

    private FirebaseAuth mAuth;

    private List email = new ArrayList<>();
    private List id = new ArrayList<>();
    private List friendLocations = new ArrayList<>();
    private List userLocations = new ArrayList<>();

    private Context c;

    public TrackAdapter(Context context, int resource)
    {
        super(context, resource);
        this.c = context;
    }

    public void add(String object, String object2, String object3, String object4, String object5, String object6)
    {
        email.add(object);
        id.add(object2);
        friendLocations.add(object3);
        friendLocations.add(object4);
        userLocations.add(object5);
        userLocations.add(object6);
        super.add(object);
        super.add(object2);
    }

    static class RowHolder
    {
        TextView EMAIL;
        TextView DISTANCE;
        ImageView COLOR; //will hold status
    }

    @Override
    public int getCount()
    {
        return this.email.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.email.get(position);
    }

    public Object getId(int position) { return this.id.get(position);}

    public Object getFriendLatitude(int position) { return this.friendLocations.get(position*2);}

    public Object getFriendLongitude(int position) { return this.friendLocations.get((position*2)+1);}

    public Object getUserLatitude(int position) { return this.userLocations.get(position*2);}

    public Object getUserLongitude(int position) { return this.userLocations.get((position*2)+1);}

    public boolean isAlreadyInList(String testId) { return this.id.contains(testId);}

    //If element is already in list and friend location is changed, function is called to replace old values
    public void replaceList(String testId, String newFLat, String newFLong, String newULat, String newULong)
    {
        int itemIndex = this.id.indexOf(testId);    //location of friend to replace

        this.friendLocations.set((itemIndex*2), newFLat);
        this.friendLocations.set(((itemIndex*2)+1), newFLong);
        this.userLocations.set((itemIndex*2), newULat);
        this.userLocations.set(((itemIndex*2)+1), newULong);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        row = convertView;
        RowHolder holder;

        final int currentPosition = position;

        //Inflate row
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.track_item, parent, false);

            holder = new RowHolder();

            holder.EMAIL = (TextView) row.findViewById(R.id.track_item_text);
            holder.DISTANCE = (TextView) row.findViewById(R.id.track_item_distance);
            holder.COLOR = (ImageView) row.findViewById(R.id.track_item_color);


            holder.EMAIL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth = FirebaseAuth.getInstance();

                    //Send to FriendInfoActivity with proper information when name is clicked
                    Intent intent = new Intent(c, FriendInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FRIENDKEY", (String) getId(currentPosition));
                    intent.putExtra("USERKEY", mAuth.getCurrentUser().getUid().toString());
                    intent.putExtra("FRIENDNAME", (String) getItem(currentPosition));
                    c.startActivity(intent);
                }
            });

            row.setTag(holder);
        }
        else
            holder = (RowHolder) row.getTag();

        //Display name
        String EM = (String) getItem(position);
        holder.EMAIL.setText(EM);

        //Based on information in database, display distance
        Location friendLocation = new Location("");
        final Location userLocation = new Location("");

        String fLatitude = (String) getFriendLatitude(currentPosition);
        String fLongitude = (String) getFriendLongitude(currentPosition);
        String uLatitude = (String) getUserLatitude(currentPosition);
        String uLongitude = (String) getUserLongitude(currentPosition);

        double fLat = Double.parseDouble(fLatitude);
        double fLong = Double.parseDouble(fLongitude);
        double uLat = Double.parseDouble(uLatitude);
        double uLong = Double.parseDouble(uLongitude);

        friendLocation.setLatitude(fLat);
        friendLocation.setLongitude(fLong);
        userLocation.setLatitude(uLat);
        userLocation.setLongitude(uLong);

        float distance = friendLocation.distanceTo(userLocation);
        int intDistance = (int) distance;

        //Display distance
        holder.DISTANCE.setText(String.valueOf(intDistance) + "m");

        //Depending on distance, display appropriate color
        if (intDistance <= 50)
            holder.COLOR.setBackgroundColor(Color.GREEN);
        else if (intDistance <= 100)
            holder.COLOR.setBackgroundColor(Color.YELLOW);
        else {
            holder.COLOR.setBackgroundColor(Color.RED);
            addNotification();  //Send notification that friend is far and might be in danger
        }

        return row;
    }

    private void addNotification() {

        //Send notification to user
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_alert) //logo
                        .setContentTitle("The Bar: ALERT!!!") //large text
                        .setContentText("FRIEND might be in DANGER !!!");// small text

        Intent notificationIntent = new Intent(getContext(), TrackAdapter.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}