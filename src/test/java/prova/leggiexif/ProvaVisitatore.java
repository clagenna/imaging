package prova.leggiexif;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import sm.claudio.imaging.fsvisit.FSDir;
import sm.claudio.imaging.fsvisit.FSFileFactory;
import sm.claudio.imaging.fsvisit.FileSystemVisitatore;

public class ProvaVisitatore {

  @Test
  public void doTheJob() throws FileNotFoundException, URISyntaxException {
    String szDir = "F:/temp/photo/2020-03-23 Primavera";
    szDir = "src/test/resources/prova/leggiexif";
    Path fi = Paths.get(szDir);
    URL ur = getClass().getResource("/prova/leggiexif");
    fi = new File(ur.toURI()).toPath();
    new FSFileFactory();
    FSDir fsDir = new FSDir(fi);
    FileSystemVisitatore fsv = new FileSystemVisitatore();
    fsDir.accept(fsv);
  }

}

