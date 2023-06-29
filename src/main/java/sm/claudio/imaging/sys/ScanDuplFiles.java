package sm.claudio.imaging.sys;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScanDuplFiles {

  private static final Logger s_log = LogManager.getLogger(ScanDuplFiles.class);

  private List<Path>          m_lifi;
  private Map<String, Foto>   map;
  private Path                startDir;
  private List<Foto>          m_liDoppi;

  public ScanDuplFiles() {
    //
  }

  public void scanDirs(Path pth) {
    setStartDir(pth);
    scanDirs();
    toMapFoto();
    filtraSingoli();
  }

  private void scanDirs() {
    Path pthStart = getStartDir();
    String[] exts = { "png", "jpg", "jpeg" };
    try (Stream<Path> wk = Files.walk(pthStart, FileVisitOption.FOLLOW_LINKS)) {
      m_lifi = wk.filter(f -> !Files.isDirectory(f)) //
          .filter(f -> isEndsWith(f, exts)) //
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
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
    s_log.debug("ProvaWalk.toMapFoto() time=" + tt.stop());
  }

  private void filtraSingoli() {
    m_liDoppi = map //
        .values() //
        .stream() //
        .sorted() //
        .filter(f -> f.getSize() > 1) //
        .collect(Collectors.toList());

  }

  public Path getStartDir() {
    return startDir;
  }

  public void setStartDir(Path p_startDir) {
    startDir = p_startDir;
  }

  public List<Foto> getDoppi() {
    return m_liDoppi;
  }
}
