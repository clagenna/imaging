package sm.claudio.imaging.sys;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;

public class Utility {

  private static ZoneId           s_zoneQui;
  private static ZoneId           s_zoneUTC;
  public static DateTimeFormatter s_dtfmt;
  public static DateTimeFormatter s_dtfmt_YMD_hms;
  static {
    s_zoneQui = ZoneId.of("Europe/Rome");
    s_zoneUTC = ZoneId.of("UTC");
    s_dtfmt = DateTimeFormatter.ISO_DATE_TIME.withZone(s_zoneUTC);
    s_dtfmt_YMD_hms = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
  }

  public static final TagInfoAscii EXIF_TAG_MAKE  = new TagInfoAscii(   //
      "Make", 0x010f, -1,                                               //
      TiffDirectoryType.EXIF_DIRECTORY_IFD0);
  public static final TagInfoAscii EXIF_TAG_MODEL = new TagInfoAscii(   //
      "Model", 0x0110, -1,                                              //
      TiffDirectoryType.EXIF_DIRECTORY_IFD0);

  public Utility() {
  }

  public static LocalDateTime parseUTC(String p_sz) {
    ZonedDateTime dtUTC = ZonedDateTime.parse(p_sz, s_dtfmt);
    ZonedDateTime dtqui = dtUTC.withZoneSameInstant(s_zoneQui);
    LocalDateTime tstamp = dtqui.toLocalDateTime();
    return tstamp;
  }

}
