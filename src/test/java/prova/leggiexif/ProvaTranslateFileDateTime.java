package prova.leggiexif;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class ProvaTranslateFileDateTime {

  private static final Logger s_log     = LogManager.getLogger(ProvaTranslateFileDateTime.class);

  private String[]            m_arrDate = {                                                      //
      "f19590703_101122.jpg"                                                                     //
      , "WhatsApp Image 2020-03-14 at 01.32.31.jpeg"                                             //
      , "f20031030_140545.jpg"                                                                   //
  };

  @SuppressWarnings("preview")
  public record PatDate(Pattern pattern, DateTimeFormatter dtf) {
  };

  private static List<PatDate> lifmt;
  static {
    lifmt = new ArrayList<>();
    lifmt.add(new PatDate(Pattern.compile("f[0-9]{8}_[0-9]{6}\\.jpg"), DateTimeFormatter.ofPattern("'f'yyyyMMdd'_'HHmmss'.jpg'")));
    lifmt
        .add(new PatDate(Pattern.compile("f[0-9]{8}_[0-9]{6}\\.jpeg"), DateTimeFormatter.ofPattern("'f'yyyyMMdd'_'HHmmss'.jpeg'")));
    lifmt.add(new PatDate(Pattern.compile("WhatsApp Image +.* at .*\\.jpeg"),
        DateTimeFormatter.ofPattern("'WhatsApp Image 'yyyy-MM-dd' at 'HH.mm.ss'.jpeg'")));

  }

  @Test
  public void doTheJob() {
    // provaDateTime();
    // provaDateTimeFormatter();

    //    Pattern pa = Pattern.compile("f[0-9]{8}_[0-9]{6}\\.jpg");
    //    for ( String sz : m_arrDate) {
    //      Matcher mtc = pa.matcher(sz);
    //      if ( mtc.matches()) 
    //        System.out.printf("%s Match-a %s !!\n", sz );
    //      else
    //        System.err.printf("%s non match-a ...\n", sz);
    //    }

    // DateTimeFormatter fmt = DateTimeFormatterBuilder.
    for (String sz : m_arrDate) {
      LocalDateTime ldt = traduciInDateTime(sz);
      s_log.info("dato {}\tCapito:{}", sz, ldt.toString());
    }
  }

  @SuppressWarnings("unused")
  private void provaDateTime() {
    LocalTime time = LocalTime.parse("10:15:30", DateTimeFormatter.ISO_TIME);
    s_log.info(time);
    LocalDate date = LocalDate.parse("19590723", DateTimeFormatter.BASIC_ISO_DATE);
    s_log.info(date);

    LocalDateTime dateTimex = LocalDateTime.parse("1959-07-23T11:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    s_log.info(dateTimex);
    LocalDateTime dateTime1 = LocalDateTime.parse("Thu, 5 Jun 2014 05:10:40 GMT", DateTimeFormatter.RFC_1123_DATE_TIME);
    s_log.info(dateTime1);
    LocalDateTime dateTime2 = LocalDateTime.parse("2014-11-03T11:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    s_log.info(dateTime2);
    LocalDateTime dateTime3 = LocalDateTime.parse("2014-10-05T08:15:30+02:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    s_log.info(dateTime3);

  }

  @SuppressWarnings("unused")
  private void provaDateTimeFormatter() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' at 'HH.mm.ss");
    LocalDateTime dttim = LocalDateTime.parse("1958-07-23 at 11.12.31", formatter);
    s_log.info(dttim.toString());
  }

  private LocalDateTime traduciInDateTime(String p_sz) {
    LocalDateTime dt = null;
    if (p_sz == null)
      return dt;
    for (PatDate dtf : lifmt) {
      try {
        Matcher mtc = dtf.pattern.matcher(p_sz);
        if (mtc.matches()) {
          dt = LocalDateTime.parse(p_sz, dtf.dtf);
          break;
        }
      } catch (DateTimeParseException l_e) {
        // s_log.error("non interpreto " + p_sz, l_e);
      }
    }
    if (dt == null)
      s_log.error("Impossibile capire:" + p_sz);
    return dt;
  }

}
