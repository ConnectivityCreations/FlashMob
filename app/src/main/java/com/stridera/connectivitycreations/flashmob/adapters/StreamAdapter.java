package com.stridera.connectivitycreations.flashmob.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseFile;
import com.squareup.picasso.Picasso;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.List;

public class StreamAdapter extends ArrayAdapter<Flashmob> {
    private final Context context;
    private final List<Flashmob> items;

    private static class ViewHolder {
        RelativeLayout rlCard;
        ImageView ivImage;
        TextView tvTitle;
        TextView tvAddress;
        TextView tvTimes;
        TextView tvDistance;
        TextView tvTimeTo;
    }

    public StreamAdapter(Context context, List<Flashmob> items) {
        super(context, R.layout.item_flashmob, items);
        this.context = context;
        this.items = items;
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
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.tvTimes = (TextView) convertView.findViewById(R.id.tvTimes);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvTimeTo = (TextView) convertView.findViewById(R.id.tvTimeTo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        if (flashmob.getOwner().getObjectId() == FlashUser.getCurrentuser().getObjectId()) {
//            viewHolder.rlCard.setBackgroundColor(convertView.getResources().getColor(R.color.bright_foreground_material_light));
//        } else {
//            viewHolder.rlCard.setBackgroundColor(convertView.getResources().getColor(R.color.primary_dark_material_light));
//        }

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
        viewHolder.tvTimes.setText(String.format("%s for %s m", flashmob.getEventDate().toString(), flashmob.getDuration()));
        viewHolder.tvDistance.setText(""); // TODO: Compute these.
        viewHolder.tvTimeTo.setText("");

        return convertView;
    }
}

