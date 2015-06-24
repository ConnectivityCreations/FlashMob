package com.stridera.connectivitycreations.flashmob.utils;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class LocationHelper {
  private static final String TAG = LocationHelper.class.getSimpleName();

  public static String addressToString(Address address) {
    if (address == null) {
      return "";
    }
    StringBuilder addressStringBuilder = new StringBuilder();
    String divider = ", ";
    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
      addressStringBuilder.append(divider).append(address.getAddressLine(i));
    }
    return addressStringBuilder.toString().substring(divider.length());
  }

  public static Address findAddress(Geocoder geocoder, String locationName, LatLng center, double precision, int resultsToCheck) {
    try {
      List<Address> addresses = center == null ?
          geocoder.getFromLocationName(
              locationName,
              1
          ) : geocoder.getFromLocationName(
          locationName,
          resultsToCheck,
          center.latitude - precision,
          center.longitude - precision,
          center.latitude + precision,
          center.longitude + precision
      );
      if (addresses.isEmpty()) {
        return null;
      }
      if (center == null) {
        return addresses.get(0);
      }

      Address closestAddress = null;
      float minDistance = Float.MAX_VALUE;
      float[] results = new float[1];
      for (Address address : addresses) {
        Location.distanceBetween(center.latitude, center.longitude, address.getLatitude(), address.getLongitude(), results);
        float distance = results[0];
        if (distance < minDistance) {
          minDistance = distance;
          closestAddress = address;
        }
      }
      return closestAddress;
    } catch (IOException e) {
      Log.e(TAG, "Geocoding failed", e);
    }

    return null;
  }
}
