package com.stridera.connectivitycreations.flashmob.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.stridera.connectivitycreations.flashmob.R;
import com.stridera.connectivitycreations.flashmob.models.Category;
import org.apmem.tools.layouts.FlowLayout;

import java.util.LinkedHashSet;
import java.util.List;

public class CategoryFragment extends Fragment {
  FlowLayout categoryFlowLayout;
  LinkedHashSet<Category> selectedCategories = new LinkedHashSet<>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_category, container, false);

    categoryFlowLayout = (FlowLayout) view.findViewById(R.id.textFlowLayout);

    return view;
  }

  public void add(Category item) {
    selectedCategories.add(item);
    TextView categoryTextView = new TextView(getActivity());
    categoryTextView.setText(item.getName());
    categoryFlowLayout.addView(categoryTextView);
  }

  public LinkedHashSet<Category> getSelectedCategories() {
    return this.selectedCategories;
  }

  public void setSelectedCategories(List<Category> selectedCategories) {
    for (Category category : selectedCategories) {
      add(category);
    }
  }
}
