package sm.claudio.imaging.javafx;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import lombok.Getter;
import sm.claudio.imaging.sys.AppProperties;

public class MainAppFxml extends Application {

  private static final String        CSZ_MAIN_APP2_CSS = "/sm/claudio/imaging/javafx/styleMainApp2.css";
  @Getter private static MainAppFxml inst;
  @Getter private Stage              primaryStage;

  @Override
  public void start(Stage pStage) throws Exception {
    inst = this;
    this.primaryStage = pStage;

    URL url = getClass().getResource(MainApp2FxmlController.CSZ_FXMLNAME);
    if (url == null)
      url = getClass().getClassLoader().getResource(MainApp2FxmlController.CSZ_FXMLNAME);
    Parent radice = FXMLLoader.load(url);
    Scene scene = new Scene(radice, 900, 440);
    url = getClass().getResource(CSZ_MAIN_APP2_CSS);
    if (url == null)
      url = getClass().getClassLoader().getResource(CSZ_MAIN_APP2_CSS);
    scene.getStylesheets().add(url.toExternalForm());

    pStage.setScene(scene);
    pStage.show();
  }

  @Override
  public void stop() throws Exception {
    System.out.println("MainAppFxml.stop()");
    AppProperties prop = AppProperties.getInst();

    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_X, String.format("%.0f", primaryStage.getWidth()));
    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_Y, String.format("%.0f", primaryStage.getHeight()));
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_X, String.format("%.0f", primaryStage.getX()));
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_Y, String.format("%.0f", primaryStage.getY()));

    prop.saveProperties();

    super.stop();
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
