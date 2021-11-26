package sm.claudio.imaging.javafx;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.controlsfx.control.ToggleSwitch;

import com.jfoenix.controls.JFXButton;

import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class MainApp2FxmlController implements Initializable, ISwingLogger {

  private static final String               EXIF_FILE_DIR = "Exif File Dir";
  private static final String               FILE_DIR_EXIF = "File Dir Exif";
  private static final String               DIR_FILE_EXIF = "Dir File Exif";
  public static final SimpleDateFormat      s_fmt         = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  /**
   * Nel fxml ci deve essere la specifica:<br/>
   * <code>fx:controller="sm.clagenna...MainApp2FxmlController"</code>
   */
  public static final String                CSZ_FXMLNAME  = "MainApp2.fxml";

  @FXML private JFXButton                   btCerca;
  @FXML private JFXButton                   btAnalizza;
  @FXML private JFXButton                   btEsegui;
  @FXML private TextField                   txDir;
  @FXML private ToggleSwitch                ckRecurse;
  @FXML private ChoiceBox<String>           panRadioB;
  @FXML private Label                       lblLogs;

  @FXML private TableView<FSFile>           table;
  @FXML private TableColumn<FSFile, String> attuale;
  @FXML private TableColumn<FSFile, String> percorso;
  @FXML private TableColumn<FSFile, String> nuovonome;
  @FXML private TableColumn<FSFile, String> dtassunta;
  @FXML private TableColumn<FSFile, String> dtnomefile;
  @FXML private TableColumn<FSFile, String> dtcreazione;
  @FXML private TableColumn<FSFile, String> dtultmodif;
  @FXML private TableColumn<FSFile, String> dtacquisizione;
  @FXML private TableColumn<FSFile, String> dtparentdir;

  private ImgModel                          m_model;

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

  }

  private void initializeTable() {
    attuale.setText("Attuale");
    attuale.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL01_ATTUALE));
    percorso.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL02_PERCORSO));
    nuovonome.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL03_NUOVONOME));
    dtassunta.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL04_DTASSUNTA));
    dtnomefile.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL05_DTNOMEFILE));
    dtcreazione.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL06_DTCREAZIONE));
    dtultmodif.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL07_DTULTMODIF));
    dtacquisizione.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL08_DTACQUISIZIONE));
    dtparentdir.setCellValueFactory(new PropertyValueFactory<FSFile, String>(FSFile.COL09_DTPARENTDIR));
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
    if (sz != null)
      fil.setInitialDirectory(new File(sz));

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
    for (FSFile fi : li) {
      itms.add(fi);
    }
  }

  @FXML
  void btEseguiClick(ActionEvent event) {
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
}