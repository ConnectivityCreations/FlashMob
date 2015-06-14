package com.stridera.connectivitycreations.flashmob.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.adapters.CommentsAdapter;
import com.stridera.connectivitycreations.flashmob.models.Comments;
import com.stridera.connectivitycreations.flashmob.models.Flashmob;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends ActionBarActivity {

    public static final String EVENT_ID = "event_id";

    private Flashmob flashmob;
    // TODO: Make sure to use our own custom model for Comment
    private ArrayList<Comments> comments;
    private CommentsAdapter aComments;

    private Button btnCommentSubmit;
    private EditText etComment;
    private ListView lvComments;

    protected String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_comments);

        comments = new ArrayList<>();
        aComments = new CommentsAdapter(this, comments);
        lvComments = (ListView) findViewById(R.id.lvComments);
        lvComments.setAdapter(aComments);

        eventId = getIntent().getStringExtra(EVENT_ID);
        Flashmob.getInBackground(eventId, new GetCallback<Flashmob>() {
            @Override
            public void done(Flashmob fm, ParseException e) {
                if (e == null) {
                    flashmob = fm;
                    setupViews();
                } else {
                    Log.d("Blah", "Error: " + e.getMessage());
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupViews() {
        refreshComments();

        btnCommentSubmit = (Button) findViewById(R.id.btnSubmitComment);
        btnCommentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Comments comment = new Comments(etComment.getText() + "", flashmob);
                comment.saveInBackground(new com.parse.SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            refreshComments();
                        } else {
                            Log.d("DEBUGGGGGGG", "Something bad happened.");
                        }
                    }
                });

                etComment.setText("");
                aComments.notifyDataSetChanged();
            }
        });

        etComment = (EditText) findViewById(R.id.etComment);
    }

    public void refreshComments()
    {
        Comments.findCommentsInBackground(flashmob, new FindCallback<Comments>() {
            @Override
            public void done(List<Comments> retrievedComments, ParseException e) {
                comments.clear();
                comments.addAll(retrievedComments);
                aComments.notifyDataSetChanged();
            }
        });

    }

}
