package com.stridera.connectivitycreations.flashmob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stridera.connectivitycreations.flashmob.models.FlashUser;
import com.stridera.connectivitycreations.flashmob.R;

import java.util.List;

public class AttendeesAdapter extends ArrayAdapter<FlashUser> {

    private final Context context;
    private final List<FlashUser> attendees;

    public AttendeesAdapter(Context context, List<FlashUser> attendees){
        super(context, android.R.layout.simple_list_item_1, attendees);
        this.context = context;
        this.attendees = attendees;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FlashUser attendee = getItem(position);

        // Check if we are using a recycled view, if not we need to inflate
        if (convertView == null) {
            // create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_attendee, parent, false);
        }

        // Look up the views for populating the data (image, caption)
        ImageView ivAttendeeProfilePicture = (ImageView) convertView.findViewById(R.id.ivAttendeeProfilePicture);
        TextView tvAttendeeName = (TextView) convertView.findViewById(R.id.tvAttendeeName);

        // Clear out the views if it was recycled (right away)
        ivAttendeeProfilePicture.setImageResource(0);
        tvAttendeeName.setText("");

        // Fill in the views
        // TODO: Fill in the image of the attendee
        // TODO: Fill in the attendee's name
        // tvAttendeeName.setText(attendee.getName());

        return convertView;
    }
}
