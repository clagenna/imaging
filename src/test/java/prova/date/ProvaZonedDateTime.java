package prova.date;

import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class ProvaZonedDateTime {

  @Test
  public void doTheJob() {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2003-06-01 23:59:01", fmt);
    System.out.println("parto da " + dt.toString());

    ZonedDateTime zdt = dt.atZone(ZoneId.systemDefault());
    System.out.println("zoned dt=" + zdt.toString());

    ZonedDateTime zdt2 = dt.atZone(ZoneId.of("GMT"));
    System.out.println("zoned dt=" + zdt2.toString());

    Instant inst = zdt.toInstant();
    FileTime tm = FileTime.fromMillis(inst.toEpochMilli());
    System.out.println("file dt=" + tm.toString());

    inst = zdt2.toInstant();
    tm = FileTime.fromMillis(inst.toEpochMilli());
    System.out.println("GMT dt=" + tm.toString());

  }

}
