package sm.claudio.imaging.sys;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class Foto implements Comparable<Foto> {
  private SimpleStringProperty id;
  private List<FotoPath>       lista;

  public Foto() {

  }

  public Foto(String p_string) {
    id = new SimpleStringProperty(this, "id", p_string);
  }

  public static Foto parse(Path pth) {
    Foto ret = new Foto();
    ret.id = new SimpleStringProperty(ret, "id", Foto.getId(pth));
    ret.add(pth);
    return ret;
  }

  public void add(Path p_pth) {
    if (lista == null)
      lista = new ArrayList<>();
    lista.add(new FotoPath(p_pth));
  }

  public String getId() {
    return id.get();
  }

  public SimpleStringProperty idProperty() {
    if (id == null)
      id = new SimpleStringProperty(this, getId());
    return id;
  }

  public Path getFile() {
    Path ret = null;
    if (lista != null && lista.size() > 0)
      ret = lista.get(0).getPath();
    return ret;
  }

  public List<FotoPath> getFiles() {
    return lista;
  }

  public static String getId(Path p_pth) {
    String sz = p_pth.getFileName().toString().toLowerCase();
    //    if ( sz.contains("_01."))
    //      System.out.println("Trovato!");
    if (sz.startsWith("f"))
      sz = sz.substring(1);
    int n = sz.lastIndexOf('.');
    if (n > 0)
      sz = sz.substring(0, n);
    if (sz.length() > 15)
      sz = sz.substring(0, 15);
    return sz;
  }

  public int getSize() {
    if (lista == null)
      return 0;
    return lista.size();
  }

  @Override
  public String toString() {
    String sz = String.format("Id:\"%s\"\nPth=\t", id);
    String vir = "";
    if (lista != null) {
      for (FotoPath f : lista) {
        sz += vir + f.toString();
        vir = "\n\t";
      }
    }
    return sz;
  }

  @Override
  public int compareTo(Foto p_o) {
    if (p_o == null || //
        p_o.id == null || //
        p_o.id.get() == null || //
        id == null || //
        id.get() == null //
    )
      System.out.println("Foto.compareTo() ERROR!!!");
    return id.get().compareTo(p_o.id.get());
  }

}
