package sm.claudio.imaging.javafx;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.controlsfx.control.ToggleSwitch;

import com.jfoenix.controls.JFXButton;

import prova.javafx.ImageViewResizer;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class MainApp2FxmlController implements Initializable, ISwingLogger {

  private static final String                      EXIF_FILE_DIR = "Exif File Dir";
  private static final String                      FILE_DIR_EXIF = "File Dir Exif";
  private static final String                      DIR_FILE_EXIF = "Dir File Exif";
  public static final SimpleDateFormat             s_fmt         = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  /**
   * Nel fxml ci deve essere la specifica:<br/>
   * <code>fx:controller="sm.clagenna...MainApp2FxmlController"</code>
   */
  public static final String                       CSZ_FXMLNAME  = "MainApp2.fxml";

  @FXML private JFXButton                          btCerca;
  @FXML private JFXButton                          btAnalizza;
  @FXML private JFXButton                          btEsegui;
  @FXML private TextField                          txDir;
  @FXML private ToggleSwitch                       ckRecurse;
  @FXML private ChoiceBox<String>                  panRadioB;
  @FXML private Label                              lblLogs;

  @FXML private TableView<FSFile>                  table;
  @FXML private TableColumn<FSFile, String>        attuale;
  @FXML private TableColumn<FSFile, String>        percorso;
  @FXML private TableColumn<FSFile, String>        nuovonome;
  @FXML private TableColumn<FSFile, String>        dtassunta;
  @FXML private TableColumn<FSFile, LocalDateTime> dtnomefile;
  @FXML private TableColumn<FSFile, LocalDateTime> dtcreazione;
  @FXML private TableColumn<FSFile, LocalDateTime> dtultmodif;
  @FXML private TableColumn<FSFile, LocalDateTime> dtacquisizione;
  @FXML private TableColumn<FSFile, LocalDateTime> dtparentdir;

  private ImgModel                                 m_model;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    panRadioB.getItems().addAll(EXIF_FILE_DIR, FILE_DIR_EXIF, DIR_FILE_EXIF);
    panRadioB.getSelectionModel().select(0);

    AppProperties prop = new AppProperties();
    prop.openProperties();
    prop.setSwingLogger(this);

    m_model = new ImgModel();
    m_model.setPriority(EExifPriority.ExifFileDir);
    m_model.setRecursive(true);
    ckRecurse.setSelected(true);

    btAnalizza.setDisable(true);
    btEsegui.setDisable(true);

    ckRecurse.selectedProperty().addListener(new ChangeListener<Boolean>() {

      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        System.out.println("ckRecurse.change event:" + newValue);
        ckRecurseClick((DragEvent) null);
      }
    });

    initializeTable();

    Stage mainstage = MainAppFxml.getInst().getPrimaryStage();
    mainstage.setOnCloseRequest(e -> exitApplication(e));
    leggiProperties(mainstage);

  }

  private void initializeTable() {
    // attuale.setText("Attuale");
    table.setRowFactory(tv -> {
      TableRow<FSFile> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && ( !row.isEmpty())) {
          // FSFile rowData = row.getItem();
          // System.out.println("Double click on: " + rowData.getAttuale());
          imagePopupWindowShow(row);
        }
      });
      return row;
    });

    attuale.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL01_ATTUALE));
    percorso.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL02_PERCORSO));
    nuovonome.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL03_NUOVONOME));
    dtassunta.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL04_DTASSUNTA));
    dtnomefile.setCellValueFactory(new PropertyValueFactory<FSFile, LocalDateTime>(FSFile.COL05_DTNOMEFILE));
    dtcreazione.setCellValueFactory(new PropertyValueFactory<FSFile, LocalDateTime>(FSFile.COL06_DTCREAZIONE));
    dtultmodif.setCellValueFactory(new PropertyValueFactory<FSFile, LocalDateTime>(FSFile.COL07_DTULTMODIF));
    dtacquisizione.setCellValueFactory(new PropertyValueFactory<FSFile, LocalDateTime>(FSFile.COL08_DTACQUISIZIONE));
    dtparentdir.setCellValueFactory(new PropertyValueFactory<FSFile, LocalDateTime>(FSFile.COL09_DTPARENTDIR));

    dtnomefile.setCellFactory(column -> {
      return new MioTableCellRenderDate<FSFile, LocalDateTime>(FSFile.COL05_DTNOMEFILE);
    });
    dtcreazione.setCellFactory(column -> {
      return new MioTableCellRenderDate<FSFile, LocalDateTime>(FSFile.COL06_DTCREAZIONE);
    });
    dtultmodif.setCellFactory(column -> {
      return new MioTableCellRenderDate<FSFile, LocalDateTime>(FSFile.COL07_DTULTMODIF);
    });
    dtacquisizione.setCellFactory(column -> {
      return new MioTableCellRenderDate<FSFile, LocalDateTime>(FSFile.COL08_DTACQUISIZIONE);
    });
    dtparentdir.setCellFactory(column -> {
      return new MioTableCellRenderDate<FSFile, LocalDateTime>(FSFile.COL09_DTPARENTDIR);
    });

    //    dtnomefile.setCellFactory(column -> {
    //      return new TableCell<FSFile, LocalDateTime>() {
    //        @Override
    //        protected void updateItem(LocalDateTime item, boolean empty) {
    //          super.updateItem(item, empty);
    //          if (item == null || empty) {
    //            setText(null);
    //            setStyle("");
    //            return;
    //          }
    //          if (getTableRow() == null)
    //            return;
    //          FSFile fil = getTableRow().getItem();
    //          if ( ! (fil instanceof FSFoto)) {
    //            setText(null);
    //            setStyle("");
    //            return;
    //          }
    //          FSFoto fot = (FSFoto) fil;
    //          // Format date.
    //          LocalDateTime dtass = fot.getDtAssunta();
    //          String sz = fil.formatDt(dtass);
    //          setText(sz);
    //          // Style all dates in March with a different color.
    //          int v = dtass.compareTo(item);
    //          // v = -1;
    //          switch (v) {
    //            case -1:
    //              setTextFill(Color.DARKGREEN);
    //              setStyle("-fx-background-color: lightcoral");
    //              break;
    //            case 0:
    //              setTextFill(Color.BLACK);
    //              setStyle("");
    //              break;
    //            case 1:
    //              setTextFill(Color.CHOCOLATE);
    //              setStyle("-fx-background-color: deepskyblue");
    //              break;
    //          }
    //        }
    //      };
    //    });
  }

  private void leggiProperties(Stage mainstage) {
    AppProperties props = AppProperties.getInst();
    int posX = props.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_X);
    int posY = props.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_Y);
    int dimX = props.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_X);
    int dimY = props.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_Y);

    double minx = 0, maxx = 0, miny = 0, maxy = 0;

    for (Screen scr : Screen.getScreens()) {
      Rectangle2D schermo = scr.getBounds();
      System.out.println(schermo);
      minx = schermo.getMinX() < minx ? schermo.getMinX() : minx;
      maxx = schermo.getMaxX() >= maxx ? schermo.getMaxX() : maxx;
      miny = schermo.getMinY() < miny ? schermo.getMinY() : miny;
      maxy = schermo.getMaxY() >= maxy ? schermo.getMaxY() : maxy;
    }
    //    System.out.printf("X %.2f - %.2f\n", minx, maxx);
    //    System.out.printf("Y %.2f - %.2f\n", miny, maxy);

    if ( (dimX * dimY) > 0) {
      mainstage.setWidth(dimX);
      mainstage.setHeight(dimY);
    }
    if ( (posX * posY) != 0) {
      posX = (int) (posX < minx ? minx : posX);
      posY = (int) (posY < minx ? minx : posY);
      mainstage.setX(posX);
      mainstage.setY(posY);
    }
    String sz = props.getPropVal(AppProperties.CSZ_PROP_LASTDIR);
    if (sz != null)
      settaDir(sz, true);
  }

  @FXML
  public void onEnter(ActionEvent ae) {
    String szPath = txDir.getText();
    settaDir(szPath);
  }

  @FXML
  void btCercaClick(ActionEvent event) {
    Stage stage = MainAppFxml.getInst().getPrimaryStage();
    DirectoryChooser fil = new DirectoryChooser();
    // imposto la dir precedente (se c'è)
    AppProperties props = AppProperties.getInst();
    String sz = props.getLastDir();
    if (sz != null) {
      if (Files.exists(Paths.get(sz)))
        fil.setInitialDirectory(new File(sz));
    }

    File fileScelto = fil.showDialog(stage);
    if (fileScelto != null) {
      String pathDir = fileScelto.getAbsolutePath();
      settaDir(pathDir, true);
    } else {
      System.out.println("Non hai scelto nulla !!");
    }

    double wx = stage.getWidth();
    double wy = stage.getHeight();
    System.out.printf("dim=%.2f,%.2f\n", wx, wy);

  }

  private void settaDir(String p_pth) {
    settaDir(p_pth, false);
  }

  private void settaDir(String p_pth, boolean bSetTx) {
    AppProperties props = AppProperties.getInst();
    System.out.println("file:" + p_pth);
    props.setLastDir(p_pth);
    if (bSetTx)
      txDir.setText(p_pth);
    m_model.setDirectory(p_pth);
    checkFattibilita();
  }

  private void checkFattibilita() {
    btAnalizza.setDisable( !m_model.isValoriOk());
    btEsegui.setDisable( !m_model.isValoriOk());
    if (m_model.getListFiles() != null)
      btAnalizzaClick(null);
  }

  @FXML
  void ckRecurseClick(DragEvent event) {
    // System.out.println("MainApp2FxmlController.ckRecurseClick():" + ckRecurse.isSelected());
    m_model.setRecursive(ckRecurse.isSelected());
    checkFattibilita();
  }

  @FXML
  void btAnalizzaClick(ActionEvent event) {
    Stage stage = MainAppFxml.getInst().getPrimaryStage();
    try {
      stage.getScene().getRoot().setCursor(Cursor.WAIT);
      clear();
      m_model.analizza();
      caricaGriglia();
    } finally {
      stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
    }

  }

  private void clear() {
    sparaMess(s_fmt.format(new Date()) + " Inizio esecuzione");
    m_model.clear();
  }

  private void caricaGriglia() {
    List<FSFile> li = m_model.getListFiles();
    ObservableList<FSFile> itms = table.getItems();
    itms.clear();
    if (li == null)
      return;
    for (FSFile fi : li) {
      itms.add(fi);
    }
  }

  @FXML
  void btEseguiClick(ActionEvent event) {
    Stage stage = MainAppFxml.getInst().getPrimaryStage();
    try {
      stage.getScene().getRoot().setCursor(Cursor.WAIT);
      m_model.rinominaFiles();
      clear();
      m_model.analizza();
      caricaGriglia();
    } finally {
      stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
    }

  }

  @Override
  public void sparaMess(String p_msg) {
    String szOut = "";
    if (p_msg != null)
      szOut = p_msg;
    lblLogs.setText(szOut);
  }

  @Override
  public void addRow(String att, String nuo, Path loc, Date dt) {
    // obsoleto e fuori moda !
    //
    //    Object[] arr = new Object[4];
    //    arr[0] = att;
    //    arr[1] = nuo;
    //    arr[2] = loc.toAbsolutePath();
    //    arr[3] = MainFrame2.s_fmt.format(dt);
    //    DefaultTableModel mod = (DefaultTableModel) table.getModel();
    //    mod.addRow(arr);
  }

  public void imagePopupWindowShow(TableRow<FSFile> row) {
    FSFile filejpeg = row.getItem();
    Stage stage = new Stage();
    Stage primaryStage = MainAppFxml.getInst().getPrimaryStage();
    stage.setWidth(800);
    stage.setHeight(600);
    //    File imageFile = rowData.getPath().toFile();
    //    Image image = new Image(imageFile.toURI().toString());
    //    ImageView imageView = new ImageView(image);
    //    ImageViewResizer imgResiz = new ImageViewResizer(imageView);

    ImageViewResizer imgResiz = caricaImg(filejpeg);

    //    StackPane root = new StackPane();
    //    root.getChildren().addAll(imgResiz);

    VBox vbox = new VBox();
    vbox.getChildren().addAll(imgResiz);
    // VBox.setVgrow(root, Priority.ALWAYS);
    Scene scene = new Scene(vbox);
    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent event) {
        switch (event.getCode()) {
          case RIGHT:
          case LEFT:
            doRightLeft(row, event.getCode(), scene);
            break;
          default:
            break;
        }

      }
    });

    stage.setScene(scene);
    stage.setTitle(filejpeg.getAttuale());
    stage.initModality(Modality.NONE);
    stage.initOwner(primaryStage);

    stage.show();
  }

  private ImageViewResizer caricaImg(FSFile p_fi) {
    File imageFile = p_fi.getPath().toFile();
    Image image = new Image(imageFile.toURI().toString());
    ImageView imageView = new ImageView(image);
    ImageViewResizer imgResiz = new ImageViewResizer(imageView);
    return imgResiz;
  }

  protected void doRightLeft(TableRow<FSFile> row, KeyCode code, Scene scene) {
    boolean bOk = false;
    int qta = table.getItems().size();
    int indx = table.getSelectionModel().getSelectedIndex();
    switch (code) {
      case LEFT:
        table.getFocusModel().focusPrevious();
        indx--;
        if (indx >= 0)
          bOk = true;
        break;
      case RIGHT:
        table.getFocusModel().focusNext();
        indx++;
        if (indx < qta)
          bOk = true;
        break;
      default:
        break;
    }
    if ( !bOk)
      return;
    table.getSelectionModel().select(indx);
    FSFile fi = table.getSelectionModel().getSelectedItem();
    System.out.printf("MainApp2FxmlController.doRightLeft(%s)\n", fi.getAttuale());

    ImageViewResizer imgResiz = caricaImg(fi);

    VBox vbox = new VBox();
    vbox.getChildren().addAll(imgResiz);
    scene.setRoot(vbox);

  }

  public void exitApplication(WindowEvent e) {
    if (m_model == null)
      return;
    AppProperties prop = AppProperties.getInst();
    prop.setPropVal(AppProperties.CSZ_PROP_LASTDIR, m_model.getDirectory());

    Platform.exit();
  }

}
