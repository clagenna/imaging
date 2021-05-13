package sm.claudio.imaging.sys;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseData implements IParseData {
  public static DateTimeFormatter    s_fmtDtExif = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
  private static final LocalDateTime s_dtMin;
  private static final LocalDateTime s_dtMax;

  static {
    s_dtMin = LocalDateTime.parse("1861:01:01 00:00:00", s_fmtDtExif);
    s_dtMax = LocalDateTime.parse("2050:12:31 23:59:59", s_fmtDtExif);
  }

  private static Pattern[] s_arrpat = { //
      //               .* yyyy-MM-dd?*hh.mm.ss ?*
      Pattern.compile(".*([0-9]{4})-([0-9]{2})-([0-9]{2}).*([0-9]{2})\\.([0-9]{2})\\.([0-9]{2}).*"), //
      //               .* yyyyMMdd?hhmmss .*
      Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).([0-9]{2})([0-9]{2})([0-9]{2}).*"), //

      //               .* yyyy-MM-dd?*hh.mm ?*
      Pattern.compile(".*([0-9]{4})-([0-9]{2})-([0-9]{2}).*([0-9]{2})\\.([0-9]{2})\\.*"), //
      //               .* yyyy-MM-dd ?*
      Pattern.compile(".*([0-9]{4})-([0-9]{2})-([0-9]{2})\\.*"), //
      //               .* yyyy-MM ?*
      Pattern.compile(".*([0-9]{4})-([0-9]{2})\\.*"), //

      //               .* yyyyMMdd?hhmm .*
      Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).([0-9]{2})([0-9]{2}).*"), //
      //               .* yyyyMMdd .*
      Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).*"), //
      //               .* yyyyMM .*
      Pattern.compile(".*([0-9]{4})([0-9]{2}).*"), //

      //               .* yyyy .*
      Pattern.compile(".*([0-9]{4}).*"), //
  };

  @Override
  public LocalDateTime parseData(String p_sz) {
    LocalDateTime dtRet = null;
    if (p_sz == null)
      return null;
    String[] szFmtDt = { "2999", "01", "01", "06", "00", "00" };

    for (Pattern pat : s_arrpat) {
      Matcher mtch = pat.matcher(p_sz);
      if (mtch.find()) {
        for (int k = 0; k < mtch.groupCount(); k++)
          szFmtDt[k] = mtch.group(k + 1);
        String szDt = String.format("%s:%s:%s %s:%s:%s", (Object[]) szFmtDt);
        dtRet = LocalDateTime.parse(szDt, s_fmtDtExif);
        if (dtRet.isAfter(s_dtMin) && dtRet.isBefore(s_dtMax))
          break;
        dtRet = null;
      }
    }

    return dtRet;
  }

}
