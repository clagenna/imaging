package prova.javafx.tree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import sm.claudio.imaging.sys.FotoPath;

public class ProvaTreeModel {

  private List<FotoPath> lista;

  public ProvaTreeModel() {
    init();
  }

  private void init() {
    lista = new ArrayList<>();
    String[] arr = { //
        "F:\\My Foto\\2006\\2006-06-19 Giappone\\f20060619_042612.jpg",
        "F:\\My Foto\\2006\\2006-06-19 Giappone\\f20060619_042727_01.jpg",
        "F:\\My Foto\\2006\\2006-06-19 Giappone\\f20060620_012554.jpg" };

    for (String sz : arr) {
      Path pth = Paths.get(sz);
      FotoPath fo = new FotoPath(pth);
      lista.add(fo);
    }
  }
  
  public List<FotoPath> getLista() {
    return lista;
  }

}
