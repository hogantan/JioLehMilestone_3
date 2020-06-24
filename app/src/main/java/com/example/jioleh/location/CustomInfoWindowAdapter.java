package com.example.jioleh.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jioleh.R;
import com.example.jioleh.listings.JioActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.google_maps_custom_info_window, null);
    }

    private void renderWindow(Marker marker, View view) {

        TextView tv_title = view.findViewById(R.id.tv_map_title);
        TextView tv_date = view.findViewById(R.id.tv_map_date);
        ImageView jioActivityImageView = view.findViewById(R.id.iv_nearBy_JioActivity_image);
        TextView tv_location = view.findViewById(R.id.tv_map_location_address);
        TextView tv_time = view.findViewById(R.id.tv_map_time);
        TextView tv_paticipants = view.findViewById(R.id.map_numberOfParticipants);


        JioActivity jio = (JioActivity) marker.getTag();

        if(jio.getImageUrl()!=null && !jio.getImageUrl().equals("")) {
            Picasso.get().load(jio.getImageUrl()).into(jioActivityImageView);
        }

        String title = jio.getTitle();
        String date = jio.getEvent_date();
        String time = jio.getEvent_time();
        String location = jio.getLocation();
        int numOfCurrentParticipants = jio.getCurrent_participants();
        int numOfMaxParticipants = jio.getMax_participants();

        tv_title.setText(title);
        tv_date.setText(date);
        tv_time.setText(time);
        tv_location.setText(location);
        tv_paticipants.setText(numOfCurrentParticipants+ "/" + numOfMaxParticipants);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindow(marker,window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindow(marker,window);
        return window;
    }
}
