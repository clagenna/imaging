package sm.claudio.imaging.javafx;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.Foto;
import sm.claudio.imaging.sys.FotoPath;
import sm.claudio.imaging.sys.ScanDuplFiles;

public class TreeViewScene implements Initializable {

  private static final Logger s_log        = LogManager.getLogger(TreeViewScene.class);
  /**
   * Nel fxml ci deve essere la specifica:<br/>
   * <code>fx:controller="sm.clagenna...MainApp2FxmlController"</code>
   */
  public static final String  CSZ_FXMLNAME = "TreeViewScene.fxml";

  @FXML
  private TreeView<String>    tree;

  private Path                startDir;
  private TreeItem<String>    treeRoot;

  public TreeViewScene() {
    //
  }

  @Override
  public void initialize(URL p_location, ResourceBundle p_resources) {
    AppProperties prop = AppProperties.getInst();
    String szIni = prop.getLastDir();
    setStartDir(Paths.get(szIni));
    ScanDuplFiles dupl = new ScanDuplFiles();
    dupl.scanDirs(getStartDir());
    List<Foto> liDupl = dupl.getDoppi();
    costruisciTree(liDupl);
  }

  private void costruisciTree(List<Foto> p_liDupl) {
    treeRoot = new TreeItem<>("Files Duplicati");
    treeRoot.setExpanded(true);
    for (Foto fo : p_liDupl) {
      TreeItem<String> ramo = new TreeItem<>(fo.getId());
      treeRoot.getChildren().add(ramo);

      for (FotoPath pth : fo.getFiles()) {
        String szNam = String.format("%s\t%s", pth.nameProperty().get(), pth.fullPathProperty().get());
        ramo.getChildren().add(new TreeItem<String>(szNam));
      }
    }
    tree.setRoot(treeRoot);
  }

  public Path getStartDir() {
    return startDir;
  }

  public void setStartDir(Path p_startDir) {
    startDir = p_startDir;
  }

}
