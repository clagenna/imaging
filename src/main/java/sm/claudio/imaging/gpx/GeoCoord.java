package sm.claudio.imaging.gpx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import lombok.Getter;
import lombok.Setter;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.fsvisit.FSFoto;

public class GeoCoord implements IDistance<GeoCoord> {
  public static double                  MIGLIO_TERRESTRE_PER_Miglio_nautico = 1.15077945;
  public static double                  Kilometro_PER_Miglio_nautico        = 1.852;
  // private static Pattern                s_patGradiMinSec;
  private static Pattern                s_patNWGradiMinSec;
  private static Pattern                s_patGradiMinSecNW;
  private static Pattern                s_patDecimali;

  public static final DateTimeFormatter s_fmtTimeZ;
  public static final DateTimeFormatter s_fmt2Y4MD_hms;
  public static final DateTimeFormatter s_fmtmY4MD_hms;
  public static final DateTimeFormatter s_fmtmY4MD_hm;
  private static final ZoneId           s_zoneQui;
  private static final ZoneId           s_zoneUTC;
  public static final DateTimeFormatter s_dtfmt;
  public static final int               LATITUDE                            = 0;
  public static final int               LONGITUDINE                         = 1;

  public record GMS(String nsow, int grad, int min, double seco) {
    @Override
    public String toString() {
      return String.format(Locale.US, "%d°%02d'%2.4f\"%s", grad, min, seco, nsow);
    }
  }

  private GMS           temp;
  private double        longitude;
  private GMS           lonGMS;
  private double        latitude;
  private GMS           latGMS;
  private double        altitude;
  private LocalDateTime tstamp;
  @Getter @Setter
  private Path          filePath;
  @Getter @Setter
  private ESrcGeoCoord  sourceCoord;
  
  static {
    // s_patGradiMinSec = Pattern.compile("([+\\-]?[0-9]+). ([0-9]+). ([0-9,\\.]+).*");
    // N 17° 09' 58.3" W 179° 02' 46.8"
    s_patNWGradiMinSec = Pattern.compile("([nesw]) +([+\\-]?[0-9]+). ([0-9]+). ([0-9,\\.]+).*");
    // es: 17°09'58.3"S 179°02'46.8"W
    s_patGradiMinSecNW = Pattern.compile("([+\\-]?[0-9]+).([0-9]+).([0-9,\\.]+)\"([nNeEsSwW])+");
    s_patDecimali = Pattern.compile("[\\+\\-]?[0-9]+\\.[0-9]+");

    s_fmtTimeZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    s_fmt2Y4MD_hms = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    s_fmtmY4MD_hms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    s_fmtmY4MD_hm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    s_zoneQui = ZoneId.of("Europe/Rome");
    s_zoneUTC = ZoneId.of("UTC");
    s_dtfmt = DateTimeFormatter.ISO_DATE_TIME.withZone(s_zoneUTC);
  }

  public GeoCoord() {
    init();
  }

  public GeoCoord(ESrcGeoCoord p_src) {
    init();
    sourceCoord = p_src;
  }

  public GeoCoord(FSFile pf) {
    setSourceCoord(ESrcGeoCoord.foto);
    setLat(pf.getLatitude());
    setLon(pf.getLongitude());
    setTstamp(pf.getAcquisizione());
    setFilePath(pf.getPath());
  }

  private void init() {
    longitude = -1.;
    latitude = -1.;
    altitude = -1.;
    sourceCoord = ESrcGeoCoord.track;
  }

  public void setLongitude(String p_v) {
    longitude = convert(p_v, LONGITUDINE);
    lonGMS = convertWGS84(longitude, LONGITUDINE);
  }

  public double getLon() {
    return longitude;
  }

  public GMS getLonGMS() {
    return lonGMS;
  }

  public void setLon(double p) {
    longitude = p;
    lonGMS = convertWGS84(p, LONGITUDINE);
  }

  public void setLatitude(String p_v) {
    latitude = convert(p_v, LATITUDE);
    latGMS = convertWGS84(latitude, LATITUDE);
  }

  public void setLat(double p) {
    latitude = p;
    latGMS = convertWGS84(p, LATITUDE);
  }

  public double getLat() {
    return latitude;
  }

  public GMS getLatGMS() {
    return latGMS;
  }

  public void setAltitude(String p_sz) {
    Matcher res = s_patDecimali.matcher(p_sz);
    if (res.matches()) {
      altitude = Double.parseDouble(p_sz);
      return;
    }
    String sz[] = p_sz.split(" ");
    altitude = Double.parseDouble(sz[0].replace(",", "."));
  }

  /**
   * converte stringhe ( del tipo <code>"N 49° 26' 23,88"</code>)in formato
   * gradi/min/sec in gradi decimali
   *
   * @param p_deg
   * @return
   */
  public double convert(String p_deg, int pLatLon) {
    double ret = -1.;
    String orient = pLatLon == LATITUDE ? "N" : "E";
    int gradi;
    int minu;
    double seco;

    // Decimali es: -34.35245572676254
    Matcher res = s_patDecimali.matcher(p_deg);
    if (res.matches()) {
      ret = Double.parseDouble(p_deg);
      convertWGS84(ret, pLatLon);
      return ret;
    }
    // GMS  N 17° 09' 58.3" W 179° 02' 46.8"
    // GMS  N 34° 23' 34.73"
    res = s_patNWGradiMinSec.matcher(p_deg.toLowerCase());
    if (res.matches()) {
      orient = res.group(1).toLowerCase();
      gradi = Integer.parseInt(res.group(2));
      minu = Integer.parseInt(res.group(3));
      seco = Double.parseDouble(res.group(4).replace(",", "."));
      temp = new GMS(orient, gradi, minu, seco);
      ret = gradi + minu / 60. + seco / 3600.;
      switch (orient) {
        case "W":
        case "w":
        case "S":
        case "s":
          ret *= -1.;
          break;
      }
      temp = new GMS(orient, gradi, minu, seco);
      return ret;
    }
    // es: 17°09'58.3"S 179°02'46.8"W
    res = s_patGradiMinSecNW.matcher(p_deg.toLowerCase());
    if (res.matches()) {
      gradi = Integer.parseInt(res.group(1));
      minu = Integer.parseInt(res.group(2));
      seco = Double.parseDouble(res.group(3).replace(",", "."));
      orient = res.group(4);
      ret = gradi + minu / 60. + seco / 3600.;
      temp = new GMS(orient, gradi, minu, seco);
      switch (orient) {
        case "W":
        case "w":
        case "S":
        case "s":
          ret *= -1.;
          break;
      }
    } else
      throw new UnsupportedOperationException("Non interpreto:" + p_deg);
    return ret;
  }

  public GMS convertWGS84(double vv, int pLatLon) {
    Double dd = Double.valueOf(Math.abs(vv));
    // String sgn = "";
    double gradi = dd.intValue();
    dd = (dd - gradi) * 60.;
    String orient = pLatLon == 0 ? "N" : "E";
    if (vv < 0)
      orient = pLatLon == 0 ? "S" : "W";
    double minu = Double.valueOf(dd).intValue();
    double seco = (dd - minu) * 60.;
    temp = new GMS(orient, (int) gradi, (int) minu, seco);
    return temp;
  }

  public double geoDistance(GeoCoord altro) {
    double ret = -1;
    double lat1 = Math.toRadians(this.latitude);
    double lon1 = Math.toRadians(this.longitude);
    double lat2 = Math.toRadians(altro.latitude);
    double lon2 = Math.toRadians(altro.longitude);
    // distanza in radianti sul cerchio terrestre, utilizzando la legge dei coseni
    double angolo = Math.acos( //
        Math.sin(lat1) * Math.sin(lat2) //
            + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    // ogni grado sul cerchio terrestre è di 60 miglia nautiche
    double migliaNaut = 60 * Math.toDegrees(angolo);
    ret = Kilometro_PER_Miglio_nautico * migliaNaut;
    return ret;
  }

  public void setTstamp(String p_sz) {
    tstamp = null;
    if (p_sz == null)
      return;
    // se date time Zulu -> UTC -> GMT(in disuso)
    try {
      if (p_sz.endsWith("Z")) {
        // converto UTC in local datetime considerando il fuso orario
        ZonedDateTime dtUTC = ZonedDateTime.parse(p_sz, s_dtfmt);
        ZonedDateTime dtqui = dtUTC.withZoneSameInstant(s_zoneQui);
        tstamp = dtqui.toLocalDateTime();
      }
    } catch (Exception e) {
      //
    }
    try {
      if (tstamp == null)
        tstamp = LocalDateTime.parse(p_sz, s_fmt2Y4MD_hms);
    } catch (Exception e) {
      //
    }
    try {
      if (tstamp == null)
        tstamp = LocalDateTime.parse(p_sz, s_fmtTimeZ);
    } catch (Exception e) {
      //
    }
    try {
      if (tstamp == null)
        tstamp = LocalDateTime.parse(p_sz, s_fmtmY4MD_hms);
    } catch (Exception e) {
      //
    }
    try {
      if (tstamp == null)
        tstamp = LocalDateTime.parse(p_sz, s_fmtmY4MD_hm);
    } catch (Exception e) {
      //
    }
    if (tstamp == null)
      throw new UnsupportedOperationException("Errore timst:" + p_sz);
  }

  public static GeoCoord fromFSFoto(FSFoto pf) {
    if ( !pf.isGPS())
      return null;
    GeoCoord ret = new GeoCoord(ESrcGeoCoord.foto);
    ret.setLat(pf.getLatitude());
    ret.setLon(pf.getLongitude());
    ret.setTstamp(pf.getAcquisizione());
    ret.setFilePath(pf.getPath());
    return ret;
  }

  public void setTstamp(LocalDateTime p_dt) {
    tstamp = p_dt;
  }

  public boolean isValid() {
    return (latitude != -1 && longitude != -1) || tstamp != null;
  }

  public boolean hasGpsSet() {
    return longitude != -1. && latitude != -1.;
  }

  @Override
  public String toString() {
    String szTim = "-";
    String szFi = "";
    if (tstamp != null)
      szTim = s_fmtmY4MD_hms.format(tstamp);
    if (filePath != null)
      szFi = " File=" + filePath.toString();
    String sz = String.format(Locale.US, "(%s,%s)(%.10f,%.10f) (Alt:%.0f m) il %s (da %s)%s" //
        , latGMS != null ? latGMS.toString() : "*null*" //
        , lonGMS != null ? lonGMS.toString() : "*null*" //
        , latitude //
        , longitude, altitude, szTim, sourceCoord.toString(), szFi);
    return sz;
  }

  public String toCsv() {
    String szTim = "";
    if (tstamp != null)
      szTim = s_fmtmY4MD_hms.format(tstamp);
    String szFi = "";
    if (filePath != null)
      szFi = filePath.toString();
    String szCoo = ";";
    if (latitude != -1 && longitude != -1)
      szCoo = String.format(Locale.US, "%.10f;%.10f", latitude, longitude);
    String sz = String.format(Locale.US, "%s;%s;%.1f;%s;%s", //
        szTim, szCoo, altitude, sourceCoord.toString(), szFi);
    return sz;
  }

  public String toCsv3() {
    String szFi = "";
    if (filePath != null)
      szFi = filePath.toString();
    String szCoo = ";";
    if (latitude != -1 && longitude != -1)
      szCoo = String.format(Locale.US, "%.10f;%.10f", latitude, longitude);
    String sz = String.format(Locale.US, "%s;%s", //
        szCoo, szFi);
    return sz;
  }

  public boolean leggiMetadata(File p_fi) {
    boolean bDeb = false;
    Metadata metadata = null;
    if (bDeb)
      System.out.println("\n" + p_fi.getAbsolutePath());
    try {
      metadata = ImageMetadataReader.readMetadata(p_fi);
    } catch (ImageProcessingException | IOException e) {
      System.err.printf("Errore \"%s\" su file \"%s\"\n", e.getMessage(), p_fi.getAbsoluteFile());
      return false;
    }
    //    String szDtTime = null;
    @SuppressWarnings("unused") String szTimeZone = null;
    String szLatLonRef = null;
    //    String szLongitude = null;
    //    String szAltitude = null;
    setFilePath(p_fi.toPath());
    for (Directory directory : metadata.getDirectories()) {
      for (Tag tag : directory.getTags()) {
        if (bDeb)
          System.out.format("%s/%s = %s\n", directory.getName(), tag.getTagName(), tag.getDescription());
        String szTag = String.format("%s/%s", directory.getName(), tag.getTagName());
        String szVal = tag.getDescription();
        switch (szTag) {
          //  Exif SubIFD/Time Zone = +02:00
          //  Exif SubIFD/Time Zone Original = +02:00
          case "Exif SubIFD/Time Zone":
            szTimeZone = szVal;
            break;

          case "Exif SubIFD/Date/Time Original":
          case "Exif IFD0/Date/Time":
            // szDtTime = szVal;
            setTstamp(szVal);
            break;

          // GPS/GPS Latitude Ref = N
          // GPS/GPS Latitude = 49° 26' 23,88
          // GPS/GPS Longitude Ref = E
          // GPS/GPS Longitude = 1° 5' 57,27
          // GPS/GPS Altitude Ref = Sea level
          // GPS/GPS Altitude = 100 metres
          case "GPS/GPS Latitude Ref":
            szLatLonRef = szVal;
            break;
          case "GPS/GPS Latitude":
            setLatitude(szLatLonRef + " " + szVal);
            break;
          case "GPS/GPS Longitude Ref":
            szLatLonRef = szVal;
            break;
          case "GPS/GPS Longitude":
            setLongitude(szLatLonRef + " " + szVal);
            break;
          case "GPS/GPS Altitude Ref":
            break;
          case "GPS/GPS Altitude":
            setAltitude(szVal);
            break;
        }
      }
      if (directory.hasErrors()) {
        for (String error : directory.getErrors()) {
          System.err.format("ERROR: %s", error);
        }
      }
    }
    if (bDeb)
      System.out.println(toCsv());
    return true;
  }

  @Override
  public int compareTo(GeoCoord p_o) {
    if ( (p_o == null) || (p_o.tstamp == null))
      return -1;
    if (tstamp == null)
      return 1;
    return tstamp.compareTo(p_o.tstamp);
  }

  public LocalDateTime getDatetime() {
    return tstamp;
  }

  @Override
  public long distance(GeoCoord p_from) {
    if (p_from == null || p_from.tstamp == null || tstamp == null)
      return Long.MAX_VALUE;
    Duration dur = Duration.between(tstamp, p_from.tstamp);
    return dur.toMillis();
  }

  public static void main(String[] args) {
    // GPS/GPS Latitude Ref = N
    // GPS/GPS Latitude = 49° 26' 23,88
    // GPS/GPS Longitude Ref = E
    // GPS/GPS Longitude = 1° 5' 57,27
    // GPS/GPS Altitude Ref = Sea level
    // GPS/GPS Altitude = 100 metres

    // N 49° 26' 23.88" E 1° 5' 57.27"
    GeoCoord app = new GeoCoord();
    app.setLatitude("N 49° 26' 23,88");
    app.setLongitude("E  1° 5' 57,27");
    app.setAltitude("0 metres");
    System.out.println(app.toString());

    // N 49° 27' 41.26" E  0° 16' 27.26"
    GeoCoord app2 = new GeoCoord();
    app2.setLatitude("N 49° 27' 41.26\"");
    app2.setLongitude("E  0° 16' 27.26\"");
    app2.setAltitude("245 metres");
    System.out.println(app2.toString());

    double dist = app.distance(app2);
    System.out.printf("Dist 1-2 = %.3f\n", dist);
  }

}
