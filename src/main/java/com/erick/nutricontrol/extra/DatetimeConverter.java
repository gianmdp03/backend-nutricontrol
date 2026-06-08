package com.erick.nutricontrol.extra;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DatetimeConverter {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public static String convertFromUtcToTimezone(OffsetDateTime dateTime, String timezone) {
    ZonedDateTime zonedDateTime = dateTime.atZoneSameInstant(ZoneId.of(timezone));
    return zonedDateTime.format(FORMATTER);
  }
}
