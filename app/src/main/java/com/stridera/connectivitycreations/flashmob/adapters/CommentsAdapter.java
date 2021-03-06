package com.stridera.connectivitycreations.flashmob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Comments;

import java.util.List;

public class CommentsAdapter extends ArrayAdapter<Comments> {

    private final Context context;
    private final List<Comments> comments;

    public CommentsAdapter(Context context, List<Comments> comments) {
        super(context, android.R.layout.simple_list_item_1, comments);
        this.context = context;
        this.comments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Comments comment = getItem(position);

        // Check if we are using a recycled view, if not we need to inflate
        if (convertView == null) {
            // create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }

        // Look up the views for populating the data (image, caption)
        ImageView ivCommenterProfilePicture = (ImageView) convertView.findViewById(R.id.ivCommenterProfilePicture);
        TextView tvCommenterName = (TextView) convertView.findViewById(R.id.tvCommenterName);
        TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);
        TextView tvCommentCreatedTime = (TextView) convertView.findViewById(R.id.tvCommentCreatedTime);

        // Clear out the views if it was recycled (right away)
        ivCommenterProfilePicture.setImageResource(0);
        tvCommenterName.setText("");
        tvComment.setText("");
        tvCommentCreatedTime.setText("");

        // Fill in the views
        Picasso.with(getContext())
                .load(comment.getOwner().getAvatarURL())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(ivCommenterProfilePicture);
        tvCommenterName.setText(comment.getOwner().getName());
        tvComment.setText(comment.getCommentText());
        tvCommentCreatedTime.setText(comment.getRelativeTimeAgo() + "");

        return convertView;
    }
}
