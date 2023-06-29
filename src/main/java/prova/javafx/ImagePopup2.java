package prova.javafx;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sm.claudio.imaging.javafx.ImageViewResizer;

public class ImagePopup2 extends Application {

  private static final String CSZ_FOTO = "dati/photo/2012/2012-08-18 Badia Prataglia/IMG_0492.JPG";
  private Stage               primaryStage;

  private String              fileName;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage p_Stage) {
    primaryStage = p_Stage;
    fileName = CSZ_FOTO;
    Button btn = new Button();
    btn.setText("Apri Image Popup");
    btn.setOnAction(e -> imagePopupWindowShow(fileName));

    BorderPane pane = new BorderPane();
    pane.setCenter(btn);

    Scene scene = new Scene(pane);

    p_Stage.setScene(scene);
    p_Stage.setTitle("Image Popup Example");
    p_Stage.show();
  }

  public void imagePopupWindowShow(String fileName2) {

    Stage stage = new Stage();
    stage.setWidth(800);
    stage.setHeight(600);
    File imageFile = new File(fileName2);
    Image image = new Image(imageFile.toURI().toString());
    ImageView imageView = new ImageView(image);
    ImageViewResizer imgResiz = new ImageViewResizer(imageView);

    VBox vbox = new VBox();
    StackPane root = new StackPane();
    root.getChildren().addAll(imgResiz);

    vbox.getChildren().addAll(root);
    VBox.setVgrow(root, Priority.ALWAYS);
    stage.setScene(new Scene(vbox));

    stage.setTitle(fileName);
    stage.initModality(Modality.NONE);
    stage.initOwner(primaryStage);

    stage.show();

  }
}
