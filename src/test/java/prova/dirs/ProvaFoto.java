package prova.dirs;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import sm.claudio.imaging.sys.Foto;

public class ProvaFoto {

  public ProvaFoto() {

  }

  @Test
  public void provalo() {
    Path pth = Paths.get("F:", "My Foto", "1963", "Eugenia Origini", "f19821103_114323.jpg");
    String sz = Foto.getId(pth);
    Foto fo = Foto.parse(pth);
    System.out.println(sz + " " + fo.toString());
  }

}
