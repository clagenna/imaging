package prova.javafx.popup;


import java.io.File;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;

/**
 * Example to show an image popup with an
 * autoplaying audio file.
 * @author Mr. Davis
 */
public class ImagePopup extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage)
    {
        Button btn = new Button();
        btn.setText("Click to show Image Popup");
        btn.setOnAction(e -> imagePopupWindowShow());
        
        BorderPane pane = new BorderPane();
        pane.setCenter(btn);
        
        Scene scene = new Scene(pane);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Popup Example");
        primaryStage.show();
    }
    
    /**
     * This method will be the image popup.
     */
    public void imagePopupWindowShow() {
        // All of our necessary variables
        File imageFile;
        File audioFile;
        Image image;
        ImageView imageView;
        Media audio;
        MediaPlayer audioPlayer;
        BorderPane pane;
        Scene scene;
        Stage stage;
        
        // The path to your image can be a URL,
        // or it can be a directory on your computer.
        // If the picture is on your computer, type the path
        // likes so:
        //     C:\\Path\\To\\Image.jpg
        // If you have a Mac, it's like this:
        //     /Path/To/Image.jpg
        // Replace the path with the one on your computer
        imageFile = new File("dati/photo/2012/2012-08-18 Badia Prataglia/IMG_0492.JPG");
        image = new Image(imageFile.toURI().toString());
        imageView = new ImageView(image);

        // The same thing applies with audio files. Replace
        // this with the path to your audio file
        audioFile = new File("dati/Claudio Baglioni - Mille giorni di te e di me.mp3");
        audio = new Media(audioFile.toURI().toString());
        audioPlayer = new MediaPlayer(audio);
        audioPlayer.setAutoPlay(true);

        // Our image will sit in the middle of our popup.
        pane = new BorderPane();
        pane.setCenter(imageView);
        scene = new Scene(pane);

        // Create the actual window and display it.
        stage = new Stage();
        stage.setScene(scene);
        // Without this, the audio won't stop!
        stage.setOnCloseRequest(
            e -> {
                e.consume();
                audioPlayer.stop();
                stage.close();
            }
        );
        stage.showAndWait();
    }
}