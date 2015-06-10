package com.stridera.connectivitycreations.flashmob.utils;

import android.location.Address;

public class LocationHelper {
  public static String addressToString(Address address) {
    StringBuilder addressStringBuilder = new StringBuilder();
    String divider = ", ";
    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
      addressStringBuilder.append(divider).append(address.getAddressLine(i));
    }
    return addressStringBuilder.toString().substring(divider.length());
  }
}
