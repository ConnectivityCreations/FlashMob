package com.stridera.connectivitycreations.flashmob.models;

import com.parse.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

@ParseClassName("Categories")
public class Category extends ParseObject {

  public static final String NAME = "name";

  public Category() {}

  public Category(String name) {
    name = sanitizeCategoryName(name);
    put(NAME, name);
  }

  public String getName() {
    return getString(NAME);
  }

  public String toString() {
    if (getObjectId() == null) {
      return "New category: " + getName();
    }
    return getName();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Category)) return false;
    Category c = (Category)o;
    if (getObjectId() != null) {
      return getObjectId().equals(c.getObjectId());
    }
    if (c.getObjectId() != null) return false;
    return getName().equals(c.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  public static void findSimilar(String name, FindCallback<Category> callback) {
    ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
    query.whereContains(NAME, sanitizeCategoryName(name));
    query.setLimit(10);
    query.orderByDescending("updatedAt");
    query.findInBackground(callback);
  }

  public static void findExact(String name, GetCallback<Category> callback) {
    ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
    query.whereEqualTo(NAME, sanitizeCategoryName(name));
    query.getFirstInBackground(callback);
  }

  public static void findInBackground(Collection<String> ids, final FindCallback<Category> callback) {
    ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
    query.whereContainedIn("objectId", ids);

    query.findInBackground(callback);
  }

  public static String sanitizeCategoryName(String name) {
    String[] words = name.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = StringUtils.capitalize(words[i]);
    }
    name = StringUtils.join(words, " ");
    return name;
  }
}
