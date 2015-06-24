package com.stridera.connectivitycreations.flashmob.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Category;

import org.apmem.tools.layouts.FlowLayout;
import org.droidparts.widget.ClearableEditText;

import java.util.Collection;
import java.util.LinkedHashSet;

public class CategoryFragment extends Fragment {
  private static final String TAG = CategoryFragment.class.getSimpleName();
  FlowLayout categoryFlowLayout;
  LinkedHashSet<Category> selectedCategories = new LinkedHashSet<>();
  ClearableEditText editText = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category, container, false);

    categoryFlowLayout = (FlowLayout) view.findViewById(R.id.textFlowLayout);

    return view;
  }

  public void setEditable(boolean editable) {
    editText = (ClearableEditText) getLayoutInflater(null).inflate(R.layout.edittext_category, null);
    setSelectedCategories(this.selectedCategories);
  }

  public void setText(CharSequence text) {
    editText.setText(text);
  }

  public void addTextChangedListener(TextWatcher textWatcher) {
    editText.addTextChangedListener(textWatcher);
  }

  public String getUserText() {
    return editText.getText().toString().trim();
  }

  private void clear() {
    selectedCategories.clear();
    categoryFlowLayout.removeAllViews();
  }

  public void add(Category item) {
    selectedCategories.add(item);
    TextView categoryTextView = new TextView(getActivity());
    categoryTextView.setText(item.getName());
    int index = categoryFlowLayout.getChildCount();
    if (editText != null) {
      index--;
      final float scale = getResources().getDisplayMetrics().density;
      int pixels = (int) (10 * scale + 0.5f);
      int width = editText.getWidth() - categoryTextView.getMeasuredWidth() - pixels;
      int layoutWidth = categoryFlowLayout.getWidth();
      editText.setWidth(width < editText.getMinimumWidth() ? layoutWidth : width);
      editText.setHint("");
    }
    categoryFlowLayout.addView(categoryTextView, index);
  }

  public LinkedHashSet<Category> getSelectedCategories() {
    return this.selectedCategories;
  }

  public void setSelectedCategories(Collection<Category> selectedCategories) {
    clear();
    if (editText != null) {
      editText.setWidth(categoryFlowLayout.getWidth());
      editText.setHint(R.string.categories);
      categoryFlowLayout.addView(editText);
    }
    for (Category category : selectedCategories) {
      add(category);
    }
  }
}
