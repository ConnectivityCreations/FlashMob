package com.stridera.connectivitycreations.flashmob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stridera.connectivitycreations.flashmob.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategorySuggestionAdapter extends BaseAdapter {

  class ViewHolder {
    TextView textView;
  }

  Context context;
  Category exact = null;
  List<Category> categories = new ArrayList<>();

  public CategorySuggestionAdapter(Context context) {
    this.context = context;
  }

  public void setSimilar(List<Category> list) {
    categories.clear();
    list.remove(this.exact);

    boolean exactFirst = exact != null && !exact.isDirty();
    if (exactFirst) categories.add(exact);
    categories.addAll(list);
    if (exact != null && !exactFirst) categories.add(exact);

    notifyDataSetChanged();
  }

  public void clear() {
    categories.clear();
    notifyDataSetChanged();
  }

  public void setExact(Category category) {
    if (exact != null) {
      categories.remove(exact);
    }
    exact = category;
    setSimilar(new ArrayList<Category>(categories));
  }

  @Override
  public int getCount() {
    return categories.size();
  }

  @Override
  public Category getItem(int position) {
    return categories.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
      viewHolder.textView = (TextView) convertView.findViewById(android.R.id.text1);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.textView.setText(getItem(position).toString());

    return convertView;
  }
}
