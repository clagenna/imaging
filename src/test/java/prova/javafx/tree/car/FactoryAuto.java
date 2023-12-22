package prova.javafx.tree.car;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FactoryAuto {
  private List<Automobile> liAuto;

  public FactoryAuto() {
    init();
  }

  private void init() {
    liAuto = new ArrayList<>();

    liAuto.add(new Automobile("Mercedes", "SL500", 2022));
    liAuto.add(new Automobile("Mercedes", "SL500 AMG", 2021));
    liAuto.add(new Automobile("Mercedes", "CLA 200", 2020));
    liAuto.add(new Automobile("Mercedes", "Classe E", 2019));

    liAuto.add(new Automobile("FIAT", "Doblò", 2022));
    liAuto.add(new Automobile("FIAT", "500", 2021));
    liAuto.add(new Automobile("FIAT", "Panda", 2020));
    liAuto.add(new Automobile("FIAT", "Tipo", 2019));

  }

  public List<Automobile> getAutos() {
    return liAuto;
  }
  public List<String> getListMarche() {
    return liAuto.stream().map(a -> a.getMarca()).distinct().sorted().collect(Collectors.toList());
  }
  

}
