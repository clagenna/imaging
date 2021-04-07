package prova.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ProvaAmgRegex {
  String[]  arr    = {                                                                                //
      "WhatsApp Image 1987-11-10 at 21.13.14",                                                        //
      "WhatsApp Image 1987-11-10 at 21.13.14_qualcosa.jpg",                                           //
      "F19570723_221013.jpg",                                                                         //
      "F19570723_221013_2.jpg" };
  
  Pattern[] arrpat = {                                                                                //
      Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).([0-9]{2})([0-9]{2})([0-9]{2}).*"),           //
      Pattern.compile(".*([0-9]{4})-([0-9]{2})-([0-9]{2}).*([0-9]{2})\\.([0-9]{2})\\.([0-9]{2}).*")   //
  };

  @Test
  public void doTheJob() {
    for (String sz : arr) {
      analizzaData(sz);
    }

  }

  private void analizzaData(String p_sz) {
    String szMsg = "Nessuno match per " + p_sz;
    for (Pattern pat : arrpat) {
      Matcher mtch = pat.matcher(p_sz);
      if (mtch.find()) {
        szMsg = String.format("su \"%s\"\n\tTrovo: %s %s %s\t%s %s %s", p_sz, //
            mtch.group(1), //
            mtch.group(2), //
            mtch.group(3), //
            mtch.group(4), //
            mtch.group(5), //
            mtch.group(6) //
        );
        break;
      }
    }
    System.out.println(szMsg);
  }
}
