package prova.javafx.tree.car;

import lombok.Getter;
import lombok.Setter;

public class Automobile {

  @Getter @Setter
  private String marca;
  @Getter @Setter
  private String modello;
  @Getter @Setter
  private int    anno;

  public Automobile(String p_ma, String p_mod, int p_ann) {
    setMarca(p_ma);
    setModello(p_mod);
    setAnno(p_ann);
  }

}
