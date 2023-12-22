package prova.javafx.tree;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProvaTreeMain extends Application {
  public Stage primaryStage;

  public ProvaTreeMain() {
    //
  }

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage p_Stage) throws Exception {
    primaryStage = p_Stage;
    primaryStage.setTitle("Prova del TreeTableView con Path");

    URL url = getClass().getResource(ProvaTreeController.CSZ_FXMLNAME);
    if (url == null)
      url = getClass().getClassLoader().getResource(ProvaTreeController.CSZ_FXMLNAME);
    Parent radice = FXMLLoader.load(url);
    Scene scene = new Scene(radice, 900, 440);
    //    url = getClass().getResource(CSZ_MAIN_APP2_CSS);
    //    if (url == null)
    //      url = getClass().getClassLoader().getResource(CSZ_MAIN_APP2_CSS);
    //    scene.getStylesheets().add(url.toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    // aggiungi le op per fermare lo stage
    super.stop();
  }

}
