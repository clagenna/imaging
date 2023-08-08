package prova.javafx.tree.car;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class PrAlberoController implements Initializable {
  public static final String                   CSZ_FXMLNAME = "/prova/tree/car/PrAlbero.fxml";

  @FXML
  private TreeTableView<Automobile>            tree;
  @FXML
  private TreeTableColumn<Automobile, String>  colMarca;
  @FXML
  private TreeTableColumn<Automobile, String>  colModello;
  @FXML
  private TreeTableColumn<Automobile, Integer> colAnno;

  private FactoryAuto                          fact;

  public PrAlberoController() {
    //
  }

  @Override
  public void initialize(URL p_loc, ResourceBundle p_res) {

    colMarca.setCellValueFactory(new TreeItemPropertyValueFactory<>("marca"));
    colModello.setCellValueFactory(new TreeItemPropertyValueFactory<>("modello"));
    colAnno.setCellValueFactory(new TreeItemPropertyValueFactory<>("anno"));

    popolaTree();
    // TreeItemPropertyValueFactory factUno = new TreeItemPropertyValueFactory<>("id");
  }

  private void popolaTree() {
    fact = new FactoryAuto();
    TreeItem<Automobile> itRoot = new TreeItem<>(new Automobile("Automobili", "...", 0));
    Map<String, TreeItem<Automobile>> itMap = new HashMap<>();
    for (String mar : fact.getListMarche()) {
      TreeItem<Automobile> it = new TreeItem<Automobile>(new Automobile(mar, "...", 0));
      itMap.put(mar, it);
      itRoot.getChildren().add(it);
    }
    for (Automobile au : fact.getAutos()) {
      String ma = au.getMarca();
      TreeItem<Automobile> it = itMap.get(ma);
      TreeItem<Automobile> itFi = new TreeItem<Automobile>(au);
      it.getChildren().add(itFi);
    }
    tree.setRoot(itRoot);
  }
}
