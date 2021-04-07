package prova.date;

import java.time.LocalDateTime;

import org.junit.Test;

import sm.claudio.imaging.sys.IParseData;
import sm.claudio.imaging.sys.ParseData;

public class ProvaParseDate {

  String szArr[] = { "1957", "boh 1957", //
      "1957-03", "boh 1957-07", "195703", "boh 195707", //
      "1957-07-23", "boh 1957-07-23", "19570723", "boh 19570723 che ne so", //
      "1957-07-23 11:13", "boh 1957-07-23 11 13:23", "19570723 1122", "boh 19570723 1233 che ne so", //
      "1957-07-23 11:13:33", "boh 1957-07-23 11 13:23:33", "19570723 112233", "boh 19570723 123322 che ne so", //
      };
  
  @Test
  public void doTheJob() {
    IParseData ip = new ParseData();
    
    for ( String sz : szArr) {
      LocalDateTime dt = ip.parseData(sz);
      if ( dt == null) 
        System.err.printf("no parse %s !\n",sz);
      else
        System.out.printf("Con %40s \tdt=%s\n", sz, ParseData.s_fmtDtExif.format(dt));
    }
    
  }
}
