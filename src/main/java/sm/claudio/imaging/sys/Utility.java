package sm.claudio.imaging.sys;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utility {

  private static ZoneId           s_zoneQui;
  private static ZoneId           s_zoneUTC;
  public static DateTimeFormatter s_dtfmt;
  static {
    s_zoneQui = ZoneId.of("Europe/Rome");
    s_zoneUTC = ZoneId.of("UTC");
    s_dtfmt = DateTimeFormatter.ISO_DATE_TIME.withZone(s_zoneUTC);
  }

  public Utility() {
  }

  public static LocalDateTime parseUTC(String p_sz) {
    ZonedDateTime dtUTC = ZonedDateTime.parse(p_sz, s_dtfmt);
    ZonedDateTime dtqui = dtUTC.withZoneSameInstant(s_zoneQui);
    LocalDateTime tstamp = dtqui.toLocalDateTime();
    return tstamp;
  }

}
