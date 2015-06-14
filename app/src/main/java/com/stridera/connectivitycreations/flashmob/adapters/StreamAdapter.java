package com.stridera.connectivitycreations.flashmob.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;
import com.stridera.connectivitycreations.flashmob.utils.TimeHelper;

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
        ViewHolder viewHolder;
        Flashmob flashmob = getItem(position);

        if (convertView == null) {
            // create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_flashmob, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.rlCard = (RelativeLayout) convertView.findViewById(R.id.rlCard);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvDrawerItemTitle);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.tvTimes = (TextView) convertView.findViewById(R.id.tvTimes);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvTimeTo = (RelativeTimeTextView) convertView.findViewById(R.id.tvTimeTo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (flashmob.isOwner()) {
            viewHolder.rlCard.setBackgroundColor(convertView.getResources().getColor(R.color.light_red));
        } else if (flashmob.isAttending()) {
            viewHolder.rlCard.setBackgroundColor(convertView.getResources().getColor(R.color.light_blue));
        } else {
            viewHolder.rlCard.setBackgroundColor(convertView.getResources().getColor(R.color.bright_foreground_material_dark));
        }

        ParseFile pfImage = flashmob.getImage();
        // TODO: Move to background.  Really slow!  No cacheing.
//        Bitmap image = null;
//        if (pfImage != null) {
//            try {
//                byte[] imageData = pfImage.getData();
//                image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//                image = Bitmap.createScaledBitmap(image, 102, 102, true);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        if (pfImage != null) {
          Picasso.with(getContext()).load(pfImage.getUrl()).resize(102, 102).into(viewHolder.ivImage);
        } else {
          viewHolder.ivImage.setImageBitmap(null);
        }
        viewHolder.tvTitle.setText(flashmob.getTitle());
        viewHolder.tvAddress.setText(flashmob.getAddress());
        Integer duration = TimeHelper.getDurationInMinutes(flashmob.getEventDate(), flashmob.getEventEnd());
        viewHolder.tvTimes.setText(String.format("%s for %s m", flashmob.getEventDate().toString(), duration));

        if (userLocation != null) {
            double distance = flashmob.getLocation().distanceInMilesTo(new ParseGeoPoint(userLocation.latitude, userLocation.longitude));
            viewHolder.tvDistance.setText(String.format("%.2f mi", distance)); // TODO: Compute these.
        } else {
            viewHolder.tvDistance.setText("");
        }
        viewHolder.tvTimeTo.setReferenceTime(flashmob.getEventDate().getTime());

        return convertView;
    }
}

