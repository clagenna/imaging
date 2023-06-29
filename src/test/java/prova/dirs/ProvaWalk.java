package prova.dirs;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import sm.claudio.imaging.sys.Foto;
import sm.claudio.imaging.sys.TimerMeter;

public class ProvaWalk {

  private List<Path>        m_lifi;
  private Map<String, Foto> map;

  public ProvaWalk() {
    //
  }

  @Test
  public void provalo() throws InterruptedException {
    TimerMeter tt = new TimerMeter("Scan Dir").start();
    // Thread.sleep(2000);
    scanDirs();
    System.out.println(tt.stop());
    System.out.printf("Qta files:%d\n", m_lifi.size());
    toMapFoto();
    List<Foto> liDoppi = map //
        .values() //
        .stream() //
        .sorted() //
        .filter(f -> f.getSize() > 1) //
        .collect(Collectors.toList());
    System.out.println("--- doppi ---");
    for (Foto f : liDoppi)
      System.out.println(f.toString());
    System.out.printf("%d in doppioni\n", liDoppi.size());
  }

  private void scanDirs() {
    Path pthStart = Paths.get("f:", "My Foto");
    String[] exts = { "png", "jpg", "jpeg" };
    try (Stream<Path> wk = Files.walk(pthStart, FileVisitOption.FOLLOW_LINKS)) {
      m_lifi = wk.filter(f -> !Files.isDirectory(f)) //
          .filter(f -> isEndsWith(f, exts)) //
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void toMapFoto() {
    map = new HashMap<>();
    TimerMeter tt = new TimerMeter("Convert path to foto").start();
    for (Path pth : m_lifi) {
      String szId = Foto.getId(pth);
      if (map.containsKey(szId))
        map.get(szId).add(pth);
      else
        map.put(szId, Foto.parse(pth));
    }
    System.out.println("ProvaWalk.toMapFoto() time=" + tt.stop());
  }

  private boolean isEndsWith(Path p_f, String[] p_exts) {
    boolean bRet = false;
    String szExt = p_f.getFileName().toString().toLowerCase();
    for (String ext : p_exts) {
      bRet = szExt.endsWith(ext);
      if (bRet)
        break;
    }
    return bRet;
  }

}
