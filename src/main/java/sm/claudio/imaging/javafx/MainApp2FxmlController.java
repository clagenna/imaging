package sm.claudio.imaging.javafx;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;
import sm.claudio.imaging.sys.Versione;

public class MainApp2FxmlController implements Initializable, ISwingLogger {
  private static final Logger                s_log             = LogManager.getLogger(MainApp2FxmlController.class);

  private static final String                IMAGE_EDITING_ICO = "image-editing.png";
  private static final String                EXIF_FILE_DIR     = "Exif File Dir";
  private static final String                FILE_DIR_EXIF     = "File Dir Exif";
  private static final String                DIR_FILE_EXIF     = "Dir File Exif";
  public static final SimpleDateFormat       s_fmt             = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  /**
   * Nel fxml ci deve essere la specifica:<br/>
   * <code>fx:controller="sm.clagenna...MainApp2FxmlController"</code>
   */
  public static final String                 CSZ_FXMLNAME      = "MainApp2.fxml";

  @FXML
  private JFXButton                          btCerca;
  @FXML
  private JFXButton                          btAnalizza;
  @FXML
  private JFXButton                          btEsegui;
  @FXML
  private JFXButton                          btInterpolaGPX;
  @FXML
  private JFXButton                          btDupl;
  @FXML
  private TextField                          txDir;
  @FXML
  private TextField                          txGpx;
  @FXML
  private JFXButton                          btCercaGPX;
  @FXML
  private ToggleSwitch                       ckUseDecimalGPS;
  @FXML
  private ToggleSwitch                       ckRecurse;
  @FXML
  private ChoiceBox<String>                  panRadioB;
  @FXML
  private Label                              lblLogs;

  @FXML
  private TableView<FSFile>                  table;
  @FXML
  private TableColumn<FSFile, String>        attuale;
  @FXML
  private TableColumn<FSFile, String>        percorso;
  @FXML
  private TableColumn<FSFile, String>        nuovonome;
  @FXML
  private TableColumn<FSFile, String>        dtassunta;
  @FXML
  private TableColumn<FSFile, LocalDateTime> dtnomefile;
  @FXML
  private TableColumn<FSFile, LocalDateTime> dtcreazione;
  @FXML
  private TableColumn<FSFile, LocalDateTime> dtultmodif;
  @FXML
  private TableColumn<FSFile, LocalDateTime> dtacquisizione;
  @FXML
  private TableColumn<FSFile, LocalDateTime> dtparentdir;
  @FXML
  private TableColumn<FSFile, Double>        longitude;
  @FXML
  private TableColumn<FSFile, Double>        latitude;

  private ImgModel                           m_model;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    panRadioB.getItems().addAll(EXIF_FILE_DIR, FILE_DIR_EXIF, DIR_FILE_EXIF);
    panRadioB.getSelectionModel().select(0);

    txDir.focusedProperty().addListener((obs, oldv, newv) -> txDirLostFocus(obs, oldv, newv));

    AppProperties prop = new AppProperties();
    prop.openProperties();
    prop.setSwingLogger(this);

    m_model = new ImgModel();
    m_model.setPriority(EExifPriority.ExifFileDir);

    ckUseDecimalGPS.selectedProperty().addListener(new ChangeListener<Boolean>() {

      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        ckMostraGMS((DragEvent) null);
      }
    });

    m_model.setRecursive(true);
    ckRecurse.setSelected(true);

    btAnalizza.setDisable(true);
    btEsegui.setDisable(true);
    btInterpolaGPX.setDisable(true);

    ckRecurse.selectedProperty().addListener(new ChangeListener<Boolean>() {

      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        System.out.println("ckRecurse.change event:" + newValue);
        ckRecurseClick((DragEvent) null);
      }
    });

    Stage mainstage = MainAppFxml.getInst().getPrimaryStage();
    initializeTable();

    mainstage.setOnCloseRequest(e -> exitApplication(e));
    InputStream stre = getClass().getResourceAsStream(IMAGE_EDITING_ICO);
    if (stre == null)
      stre = getClass().getClassLoader().getResourceAsStream(IMAGE_EDITING_ICO);
    if (stre != null) {
      Image ico = new Image(stre);
      mainstage.getIcons().add(ico);
    }
    // mainstage.setTitle("Convertitore nomi foto");
    mainstage.setTitle(Versione.getVersionEx());
    leggiProperties(mainstage);

  }

  private Object txDirLostFocus(ObservableValue<? extends Boolean> p_obs, Boolean p_oldv, Boolean p_newv) {
    if ( !p_newv) {
      // System.out.println("MainApp2FxmlController.txDirLostFocus():" + txDir.getText());
      settaDir(txDir.getText());
    }
    return null;
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
    latitude.setCellValueFactory(new PropertyValueFactory<FSFile, Double>(FSFile.COL10_LATITUDE));
    longitude.setCellValueFactory(new PropertyValueFactory<FSFile, Double>(FSFile.COL11_LONGITUDE));

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

    latitude.setCellFactory(column -> {
      return new MioTableCellRenderCoord<FSFile, Double>(FSFile.COL10_LATITUDE);
    });
    longitude.setCellFactory(column -> {
      return new MioTableCellRenderCoord<FSFile, Double>(FSFile.COL11_LONGITUDE);
    });
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
    for (TableColumn<FSFile, ?> c : table.getColumns()) {
      readColumnWidth(props, c);
    }
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
    sz = String.format("dim=%.2f,%.2f\n", wx, wy);
    s_log.debug(sz);
  }

  private void settaDir(String p_pth) {
    settaDir(p_pth, false);
  }

  private void settaDir(String p_pth, boolean bSetTx) {
    AppProperties props = AppProperties.getInst();
    // System.out.println("file:" + p_pth);
    props.setLastDir(p_pth);
    if (bSetTx)
      txDir.setText(p_pth);
    Path pth = Paths.get(p_pth);
    if ( !Files.exists(pth)) {
      String sz = String.format("Il path %s non esiste...", p_pth);
      msgBox(sz);
      sparaMess(sz);
      return;
    }
    if ( !Files.isDirectory(pth, LinkOption.NOFOLLOW_LINKS)) {
      String sz = String.format("Il path %s non e' un direttorio!", p_pth);
      msgBox(sz);
      sparaMess(sz);
      return;
    }
    m_model.setDirectory(p_pth);
    checkFattibilita();
  }

  private void settaGPX(String p_pth, boolean bSetTx) {
    AppProperties props = AppProperties.getInst();

    props.setLastGPX(p_pth);
    if (bSetTx)
      txGpx.setText(p_pth);
    Path pth = Paths.get(p_pth);
    if ( !Files.exists(pth)) {
      String sz = String.format("Il path del GPX %s non esiste...", p_pth);
      msgBox(sz);
      sparaMess(sz);
      return;
    }
    if ( !Files.isRegularFile(pth, LinkOption.NOFOLLOW_LINKS)) {
      String sz = String.format("Il file %s non e' un GPX!", p_pth);
      msgBox(sz);
      sparaMess(sz);
      return;
    }
    m_model.setFileGPX(p_pth);
    checkFattibilita();
  }

  @FXML
  void btCercaGPXClick(ActionEvent event) {
    Stage stage = MainAppFxml.getInst().getPrimaryStage();
    FileChooser fil = new FileChooser();
    fil.setTitle("Un file GPX con tracce (TRK)");
    fil.getExtensionFilters().add( //
        new ExtensionFilter("GPX file", "*.gpx"));
    fil.getExtensionFilters().add( //
        new ExtensionFilter("Takeout json", "*.json"));
    // imposto la dir precedente (se c'è)
    AppProperties props = AppProperties.getInst();
    String sz = props.getLastDir();
    if (sz != null) {
      if (Files.exists(Paths.get(sz)))
        fil.setInitialDirectory(new File(sz));
    }

    File fileScelto = fil.showOpenDialog(stage);
    if (fileScelto != null) {
      String pathDir = fileScelto.getAbsolutePath();
      settaGPX(pathDir, true);
    } else {
      System.out.println("Non hai scelto nessun GPX!!");
    }
    //
    //    double wx = stage.getWidth();
    //    double wy = stage.getHeight();
    //    sz = String.format("dim=%.2f,%.2f\n", wx, wy);
    //    s_log.debug(sz);
  }

  @FXML
  void ckMostraGMS(DragEvent event) {
    // System.out.println("MainApp2FxmlController.ckMostraGMS():" + ckUseDecimalGPS.isSelected());
    AppProperties prop = AppProperties.getInst();
    prop.setShowGMS(ckUseDecimalGPS.isSelected());
    table.refresh();
  }

  private void msgBox(String p_format) {
    msgBox(p_format, AlertType.INFORMATION);
  }

  private void msgBox(String p_txt, AlertType tipo) {
    Alert alt = new Alert(tipo);
    Scene sce = MainAppFxml.getInst().getPrimaryStage().getScene();
    Window wnd = null;
    if (sce != null)
      wnd = sce.getWindow();
    if (wnd != null) {
      alt.initOwner(wnd);
      alt.setTitle("Informazione");
      alt.setContentText(p_txt);
      alt.show();
    } else
      s_log.error("Windows==null; msg={}", p_txt);
  }

  private void checkFattibilita() {
    btAnalizza.setDisable( !m_model.isValoriOk());
    btEsegui.setDisable( !m_model.isValoriOk());
    btInterpolaGPX.setDisable( !m_model.isValoriOk() || !m_model.isGPXFileOk());
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

  @FXML
  void btInterpolaGPXClick(ActionEvent event) {
    Stage stage = MainAppFxml.getInst().getPrimaryStage();
    try {
      Scene sce = stage.getScene();
      sce.setCursor(Cursor.WAIT);
      // stage.getScene().getRoot().setCursor(Cursor.WAIT);
      m_model.interpolaGPX();
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
    saveColumnWidth(prop);
    Platform.exit();
  }

  private void saveColumnWidth(AppProperties p_prop) {
    // saveColumnWidth(p_prop, attuale);
    for (TableColumn<FSFile, ?> c : table.getColumns()) {
      // System.out.printf("MainApp2FxmlController.saveColumnWidth(\"%s\")\n", c.getId());
      saveColumnWidth(p_prop, c);
    }
  }

  private void readColumnWidth(AppProperties p_prop, TableColumn<?, ?> p_col) {
    String sz = String.format(AppProperties.CSZ_PROP_TBCOL, p_col.getId());
    double w = p_prop.getDoublePropVal(sz);
    if (w > 0)
      p_col.setPrefWidth(w);
  }

  private void saveColumnWidth(AppProperties p_prop, TableColumn<?, ?> p_col) {
    String sz = String.format(AppProperties.CSZ_PROP_TBCOL, p_col.getId());
    p_prop.setDoublePropVal(sz, p_col.getWidth());
  }

  @FXML
  void btCercaDuplicati(ActionEvent event) throws IOException {

    Stage stage = new Stage();
    Stage primaryStage = MainAppFxml.getInst().getPrimaryStage();
    stage.setWidth(800);
    stage.setHeight(600);

    URL url = getClass().getResource(TreeViewScene.CSZ_FXMLNAME);
    if (url == null)
      url = getClass().getClassLoader().getResource(TreeViewScene.CSZ_FXMLNAME);
    Parent radice = FXMLLoader.load(url);
    Scene scene = new Scene(radice, 600, 440);
    stage.setScene(scene);
    stage.setTitle("Cerca file duplicati");
    stage.initModality(Modality.NONE);
    stage.initOwner(primaryStage);

    stage.show();
  }
}
