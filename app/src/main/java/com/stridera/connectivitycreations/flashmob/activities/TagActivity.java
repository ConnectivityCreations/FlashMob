package com.stridera.connectivitycreations.flashmob.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.adapters.CategorySuggestionAdapter;
import com.stridera.connectivitycreations.flashmob.fragments.CategoryFragment;
import com.stridera.connectivitycreations.flashmob.models.Category;

import java.util.List;


public class TagActivity extends AppCompatActivity {

  private static final String TAG = TagActivity.class.getSimpleName();

  EditText categoryEditText;
  CategoryFragment categoryFragment;
  CategorySuggestionAdapter categoryAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tag);

    categoryEditText = (EditText) findViewById(R.id.categoryEditText);
    categoryAdapter = new CategorySuggestionAdapter(this);
    categoryFragment = (CategoryFragment) getSupportFragmentManager().findFragmentById(R.id.categoryFragment);

    initToolbar();
    initEditText();
    initSuggestionsListView();
    displayDefaultSuggestions();
  }

  private void initSuggestionsListView() {
    ListView suggestionsListView = (ListView) findViewById(R.id.suggestionsListView);
    suggestionsListView.setAdapter(categoryAdapter);
    suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        addCategory(categoryAdapter.getItem(position));
        categoryEditText.setText(null);
      }
    });
  }

  private void initEditText() {
    categoryEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        updateSuggestions();
      }
    });
  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
    setSupportActionBar(toolbar);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_tag, menu);
    return true;
  }

  private void updateSuggestions() {
    final String current = categoryEditText.getText().toString().trim();
    if (current.isEmpty()) {
      displayDefaultSuggestions();
      return;
    }

    Category.findSimilar(current, new FindCallback<Category>() {
      @Override
      public void done(List<Category> list, ParseException e) {
        if (e != null) {
          Log.e(TAG, e.getMessage(), e);
          return;
        }
        categoryAdapter.setSimilar(list);
      }
    });

    Category.findExact(current, new GetCallback<Category>() {
      @Override
      public void done(Category category, ParseException e) {
        categoryAdapter.setExact(category == null ? new Category(current) : category);
      }
    });
  }

  private void displayDefaultSuggestions() {
    categoryAdapter.clear();
  }

  private void addCategory(Category item) {
    categoryFragment.add(item);
  }
}
