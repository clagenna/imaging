package sm.claudio.imaging.gpx;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sm.claudio.imaging.sys.Utility;

@Getter
@Setter
@ToString
class Extra {
  private String type;
  private String name;
  private double intVal;
}

@Getter
@Setter
@ToString
class Activity {
  private String type;
  private double confidence;
  private Extra  extraObj;
}

public class GooglePos {
  private double  latitudeE7;
  private double  longitudeE7;
  private int     accuracy;
  private int     velocity;
  private int     heading;
  private int     altitude;
  private int     verticalAccuracy;
  List<Activity>  activity = new ArrayList<>();
  private String  source;
  private long    deviceTag;
  private String  platformType;
  private double  osLevel;
  private String  serverTimestamp;
  private String  deviceTimestamp;
  private String  timestamp;
  /*
   * com.google.gson.JsonIOException: Failed making field
   * 'java.time.LocalDateTime#date' accessible; either increase its visibility
   * or write a custom TypeAdapter for its declaring type. Caused by:
   * java.lang.reflect.InaccessibleObjectException: Unable to make field private
   * final java.time.LocalDate java.time.LocalDateTime.date accessible: module
   * java.base does not "opens java.time" to module com.google.gson
   */
  //  public LocalDateTime serverTs;
  //  public LocalDateTime deviceTs;
  //  public LocalDateTime timesTs;
  private boolean batteryCharging;
  private String  formFactor;

  public GooglePos() {
    //
  }

  public double getLatitudeE7() {
    return latitudeE7;
  }

  public void setLatitudeE7(double p_latitudeE7) {
    latitudeE7 = p_latitudeE7;
    if (p_latitudeE7 > 360.)
      latitudeE7 /= 10_000_000.;
  }

  public double getLongitudeE7() {
    return longitudeE7;
  }

  public void setLongitudeE7(double p_longitudeE7) {
    longitudeE7 = p_longitudeE7;
    if (p_longitudeE7 > 360.)
      longitudeE7 /= 10_000_000.;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(int p_accuracy) {
    accuracy = p_accuracy;
  }

  public int getVelocity() {
    return velocity;
  }

  public void setVelocity(int p_velocity) {
    velocity = p_velocity;
  }

  public int getHeading() {
    return heading;
  }

  public void setHeading(int p_heading) {
    heading = p_heading;
  }

  public int getAltitude() {
    return altitude;
  }

  public void setAltitude(int p_altitude) {
    altitude = p_altitude;
  }

  public int getVerticalAccuracy() {
    return verticalAccuracy;
  }

  public void setVerticalAccuracy(int p_verticalAccuracy) {
    verticalAccuracy = p_verticalAccuracy;
  }

  public List<Activity> getActivity() {
    return activity;
  }

  public void setActivity(List<Activity> p_activity) {
    activity = p_activity;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String p_source) {
    source = p_source;
  }

  public long getDeviceTag() {
    return deviceTag;
  }

  public void setDeviceTag(long p_deviceTag) {
    deviceTag = p_deviceTag;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String p_platformType) {
    platformType = p_platformType;
  }

  public double getOsLevel() {
    return osLevel;
  }

  public void setOsLevel(double p_osLevel) {
    osLevel = p_osLevel;
  }

  public String getServerTimestamp() {
    // return GeoCoord.s_fmtTimeZ.format(serverTimestamp);
    return serverTimestamp;
  }

  public void setServerTimestamp(String p_v) {
    serverTimestamp = p_v;
    // serverTs = Utility.parseUTC(p_v);
  }

  public LocalDateTime getServerTs() {
    if (serverTimestamp == null)
      return LocalDateTime.MIN;
    return Utility.parseUTC(serverTimestamp);
  }

  public String getDeviceTimestamp() {
    // return GeoCoord.s_fmtTimeZ.format(deviceTimestamp);
    return deviceTimestamp;
  }

  public void setDeviceTimestamp(String p_v) {
    deviceTimestamp = p_v;
    //    deviceTs = Utility.parseUTC(p_v);
  }

  //  public LocalDateTime getDeviceTs() {
  //    return deviceTs;
  //  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String p_v) {
    timestamp = p_v;
    //    timesTs = Utility.parseUTC(p_v);
  }

  public LocalDateTime getTimeTs() {
    if (timestamp == null)
      return LocalDateTime.MIN;
    return Utility.parseUTC(timestamp);
  }

  public boolean isBatteryCharging() {
    return batteryCharging;
  }

  public void setBatteryCharging(boolean p_batteryCharging) {
    batteryCharging = p_batteryCharging;
  }

  public String getFormFactor() {
    return formFactor;
  }

  public void setFormFactor(String p_formFactor) {
    formFactor = p_formFactor;
  }

  public boolean isGps() {
    return latitudeE7 * longitudeE7 != 0;
  }

}
