package com.stridera.connectivitycreations.flashmob.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.activities.EventCreateActivity;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.List;

public class StreamAdapter extends ArrayAdapter<Flashmob> {
    private final Context context;
    private final List<Flashmob> items;
    private LatLng userLocation;

    private static class ViewHolder {
        RelativeLayout rlCard;
        ImageView ivImage;
        TextView tvTitle;
        TextView tvAddress;
        TextView tvTimes;
        TextView tvDistance;
        RelativeTimeTextView tvTimeTo;
        TextView tvButton;
    }

    public StreamAdapter(Context context, List<Flashmob> items) {
        super(context, R.layout.item_flashmob, items);
        this.context = context;
        this.items = items;

        initLocation();
    }

    private void initLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        }, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        Flashmob flashmob = getItem(position);

        if (convertView == null) {
            // create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_flashmob, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.rlCard = (RelativeLayout) convertView.findViewById(R.id.rlCard);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivStreamImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvStreamTitle);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.tvTimes = (TextView) convertView.findViewById(R.id.tvTimes);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvTimeTo = (RelativeTimeTextView) convertView.findViewById(R.id.tvTimeTo);
            viewHolder.tvButton = (TextView) convertView.findViewById(R.id.streamButton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvButton.setTag(flashmob.getObjectId());
        if (flashmob.isOwner()) {
            viewHolder.tvButton.setText("Edit");
            viewHolder.tvButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_image, 0, 0, 0);
            viewHolder.tvButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) v.getTag();
                    Intent i = new Intent(context, EventCreateActivity.class);
                    i.putExtra(EventCreateActivity.EVENT_ID, id);
                    i.putExtra(EventCreateActivity.SHOW_DETAILS_POST_SAVE, false);
                    context.startActivity(i);
                }
            });
        } else if (flashmob.isAttending()) {
            setupLeave(viewHolder.tvButton);
        } else {
            setupJoin(viewHolder.tvButton);
        }

        ParseFile pfImage = flashmob.getImage();
        if (pfImage != null) {
            viewHolder.ivImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Picasso.with(getContext()).load(pfImage.getUrl()).resize(200, viewHolder.ivImage.getMeasuredWidth()).into(viewHolder.ivImage);
        } else {
            viewHolder.ivImage.setImageBitmap(null);
        }

        viewHolder.tvTitle.setText(flashmob.getTitle());
        viewHolder.tvAddress.setText(flashmob.getAddress());
        viewHolder.tvTimes.setText(String.format("%s for %s", flashmob.getEventDate().toString(), flashmob.getEventDurationString()));

        if (userLocation != null) {
            double distance = flashmob.getLocation().distanceInMilesTo(new ParseGeoPoint(userLocation.latitude, userLocation.longitude));
            viewHolder.tvDistance.setText(String.format("%.2f mi", distance));
        } else {
            viewHolder.tvDistance.setText("");
        }

        viewHolder.tvTimeTo.setReferenceTime(flashmob.getEventDate().getTime());

        return convertView;
    }

    private void setupLeave(TextView v) {
        v.setText("Leave");
        v.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unjoin_image, 0, 0, 0);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String id = (String) v.getTag();
                Flashmob.getInBackground(id, new GetCallback<Flashmob>() {
                    @Override
                    public void done(Flashmob flashmob, ParseException e) {
                        flashmob.leave();
                        setupJoin((TextView) v);
                    }
                });
            }
        });
    }

    private void setupJoin(TextView v) {
        v.setText("Join");
        v.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_join_image, 0, 0, 0);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String id = (String) v.getTag();
                Flashmob.getInBackground(id, new GetCallback<Flashmob>() {
                    @Override
                    public void done(Flashmob flashmob, ParseException e) {
                        flashmob.join();
                        setupLeave((TextView) v);
                    }
                });
            }
        });
    }
}

