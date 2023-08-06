package prova.javafx.tree.car;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PrAlberoMain extends Application {
  private Stage              primaryStage;
  public static final String CSZ_CSS_MAIN = "/sm/claudio/imaging/javafx/styleMainApp2.css";

  public PrAlberoMain() {
    //
  }

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage p_Stage) throws Exception {
    primaryStage = p_Stage;
    primaryStage.setTitle("Prova in JavaFX del tree view");
    URL url = getClass().getResource(PrAlberoController.CSZ_FXMLNAME);
    if (url == null)
      url = getClass().getClassLoader().getResource(PrAlberoController.CSZ_FXMLNAME);
    Parent radice = FXMLLoader.load(url);
    Scene scene = new Scene(radice, 600, 400);
    url = getClass().getResource(CSZ_CSS_MAIN);
    if (url == null)
      url = getClass().getClassLoader().getResource(CSZ_CSS_MAIN);
    scene.getStylesheets().add(url.toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    // aggiungi le op per fermare lo stage
    super.stop();
  }
}
