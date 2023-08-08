package prova.javafx.tree;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import sm.claudio.imaging.sys.FotoPath;

public class ProvaTreeController implements Initializable {

  public static final String CSZ_FXMLNAME = "/prova/tree/ProvaTree.fxml";

  @FXML
  private TreeView<FotoPath> tree;

  public ProvaTreeController() {
    //
  }

  @Override
  public void initialize(URL p_location, ResourceBundle p_resources) {
    //

  }

}
