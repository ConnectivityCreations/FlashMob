package com.stridera.connectivitycreations.flashmob.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeHelper {
  private static final int MILLIS_IN_A_SECOND = 1000;
  private static final int SECONDS_IN_A_MINUTE = 60;
  private static final int MILLIS_IN_A_MINUTE = MILLIS_IN_A_SECOND * SECONDS_IN_A_MINUTE;

  public static Integer getDurationInMinutes(Calendar startTime, Calendar endTime) {
    Integer duration = null;
    if (endTime != null) {
      long differenceInMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
      duration = (int)(differenceInMillis / MILLIS_IN_A_MINUTE);
    }
    return duration;
  }

  public static Calendar createAfter(Calendar reference, int hourOfDay, int minute) {
    Calendar time = new GregorianCalendar(reference.get(Calendar.YEAR), reference.get(Calendar.MONTH), reference.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    if (time.before(reference)) {
      time.add(Calendar.DAY_OF_MONTH, 1);
    }
    return time;
  }
}
