package sm.claudio.imaging.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.fsvisit.FSFile;
import sm.claudio.imaging.fsvisit.FSFoto;
import sm.claudio.imaging.main.EExifPriority;
import sm.claudio.imaging.sys.AppProperties;
import sm.claudio.imaging.sys.ISwingLogger;

public class MainFrame2 extends JFrame implements ISwingLogger {

  /** long serialVersionUID */
  private static final long             serialVersionUID = 5862421005705834030L;
  private static final Logger           s_log            = LogManager.getLogger(MainFrame2.class);
  private static final SimpleDateFormat s_fmt            = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");;

  private Dimension                     m_winDim;
  private Point                         m_winPos;
  private JTextField                    m_txDir;
  private ImgModel                      m_model;
  private JButton                       m_btAnalizza;
  private JButton                       m_btExec;
  private JLabel                        m_lblLogs;
  private JTable                        table;

  public MainFrame2() {
    creaComponenti();
  }

  protected void creaComponenti() {

    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception l_e) {
      MainFrame2.s_log.error("Set Look and Feel", l_e);
    }

    AppProperties prop = new AppProperties();
    prop.openProperties();
    prop.setSwingLogger(this);
    m_model = new ImgModel();

    setTitle("Retagging di foto");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent p_e) {
        locWindowClosing(p_e);
      }
    });

    initPos(prop);

    //    Container pane = getContentPane();
    //    pane.setLayout(new GridBagLayout());
    //    creaFileChooser(pane);
    //    creaRadios(pane);
    //    creaJTable(pane);
    //    creaCheck(pane);
    //    creaButtonExec(pane);
    //    creaLogArea(pane);

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
    gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
    gridBagLayout.columnWidths = new int[] { 100, 100 };
    gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
    //    gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
    //    gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
    getContentPane().setLayout(gridBagLayout);

    JPanel panChooseDir = new JPanel();
    GridBagConstraints gbc_panCercaDir = new GridBagConstraints();
    gbc_panCercaDir.insets = new Insets(0, 0, 5, 5);
    gbc_panCercaDir.fill = GridBagConstraints.BOTH;
    gbc_panCercaDir.gridx = 0;
    gbc_panCercaDir.gridy = 0;
    getContentPane().add(panChooseDir, gbc_panCercaDir);
    panChooseDir.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

    JLabel m_lbChooseDir = new JLabel("Directory");
    panChooseDir.add(m_lbChooseDir);

    m_txDir = new JTextField();
    panChooseDir.add(m_txDir);
    m_txDir.setColumns(20);
    m_txDir.setText(prop.getLastDir());
    m_txDir.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locTxDir_Click(e);
      }
    });
    panChooseDir.add(m_txDir);

    JButton m_btChooseDir = new JButton("Cerca...");
    m_btChooseDir.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        locBtChooseDir(e);
      }
    });

    panChooseDir.add(m_btChooseDir);

    JPanel panRadioB = new JPanel();
    GridBagConstraints gbc_panRadio = new GridBagConstraints();
    gbc_panRadio.insets = new Insets(0, 0, 5, 0);
    gbc_panRadio.gridheight = 2;
    gbc_panRadio.fill = GridBagConstraints.BOTH;
    gbc_panRadio.gridx = 1;
    gbc_panRadio.gridy = 0;
    getContentPane().add(panRadioB, gbc_panRadio);

    ButtonGroup m_rdbPrioGroup = new ButtonGroup();
    panRadioB.setLayout(new BoxLayout(panRadioB, BoxLayout.Y_AXIS));

    JRadioButton m_rdbPrioEFD = new JRadioButton("Exif File Dir");
    panRadioB.add(m_rdbPrioEFD);
    m_rdbPrioGroup.add(m_rdbPrioEFD);
    m_rdbPrioEFD.setActionCommand(EExifPriority.ExifFileDir.toString());
    m_rdbPrioEFD.setSelected(true);
    m_model.setPriority(EExifPriority.ExifFileDir);
    m_rdbPrioEFD.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locRadioPrio_Click(e);
      }
    });

    JRadioButton m_rdbPrioFDE = new JRadioButton("File Dir Exif");
    panRadioB.add(m_rdbPrioFDE);
    m_rdbPrioGroup.add(m_rdbPrioFDE);
    m_rdbPrioFDE.setActionCommand(EExifPriority.FileDirExif.toString());
    m_rdbPrioFDE.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locRadioPrio_Click(e);
      }
    });

    JRadioButton m_rdbPrioDFE = new JRadioButton("Dir File Exif");
    panRadioB.add(m_rdbPrioDFE);
    m_rdbPrioGroup.add(m_rdbPrioDFE);
    m_rdbPrioDFE.setActionCommand(EExifPriority.DirFileExif.toString());
    m_rdbPrioDFE.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locRadioPrio_Click(e);
      }
    });

    JPanel panExec = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panExec.getLayout();
    flowLayout.setAlignment(FlowLayout.LEADING);
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.insets = new Insets(0, 0, 5, 5);
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 1;
    getContentPane().add(panExec, gbc_panel);

    JCheckBox ckbRecurse = new JCheckBox("Recurse Dirs.");
    ckbRecurse.setSelected(true);
    m_model.setRecursive(true);
    ckbRecurse.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locChkRecursive_Click(e);
      }
    });
    panExec.add(ckbRecurse);

    m_btAnalizza = new JButton("Analizza");
    m_btAnalizza.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locBtAnalizza_Click(e);
      }
    });
    m_btAnalizza.setEnabled(false);
    panExec.add(m_btAnalizza);

    m_btExec = new JButton("Esegui");
    m_btExec.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locBtEsegui_Click(e);
      }
    });
    m_btExec.setEnabled(false);
    panExec.add(m_btExec);

    table = new JTable();
    // table.setFillsViewportHeight(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    intestaTabella();

    GridBagConstraints gbc_table = new GridBagConstraints();
    gbc_table.gridwidth = 2;
    gbc_table.insets = new Insets(0, 0, 5, 0);
    gbc_table.fill = GridBagConstraints.BOTH;
    gbc_table.gridx = 0;
    gbc_table.gridy = 2;

    JScrollPane scrlTable = new JScrollPane(table);
    getContentPane().add(scrlTable, gbc_table);

    m_lblLogs = new JLabel("Riga di log");
    m_lblLogs.setHorizontalAlignment(SwingConstants.CENTER);
    m_lblLogs.setForeground(Color.BLUE);
    GridBagConstraints gbc_lblLog = new GridBagConstraints();
    gbc_lblLog.gridwidth = 2;
    gbc_lblLog.gridx = 0;
    gbc_lblLog.gridy = 3;
    getContentPane().add(m_lblLogs, gbc_lblLog);

    pack();
    setVisible(true);
  }

  //  @SuppressWarnings("unused")
  //  private void creaFileChooser(Container pane) {
  //    AppProperties prop = AppProperties.getInst();
  //
  //    JPanel panChooseDir = new JPanel(new FlowLayout(FlowLayout.LEADING));
  //    JLabel m_lbChoseDir = new JLabel("Directory");
  //    panChooseDir.add(m_lbChoseDir);
  //
  //    m_txDir = new JTextField();
  //    m_txDir.setSize(140, 25);
  //    m_txDir.setMinimumSize(new Dimension(80, m_txDir.getPreferredSize().height));
  //    m_txDir.setColumns(30);
  //    m_txDir.setText(prop.getLastDir());
  //    m_txDir.addActionListener(new ActionListener() {
  //
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locTxDir_Click(e);
  //      }
  //    });
  //    panChooseDir.add(m_txDir, BorderLayout.CENTER);
  //
  //    JButton m_btChooseDir = new JButton("Scegli...");
  //    m_btChooseDir.addActionListener(new ActionListener() {
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locBtChooseDir(e);
  //      }
  //    });
  //    panChooseDir.add(m_btChooseDir);
  //
  //    GridBagConstraints gblc = new GridBagConstraints();
  //    gblc.fill = GridBagConstraints.HORIZONTAL;
  //    gblc.gridx = 0;
  //    gblc.gridy = 0;
  //    gblc.gridwidth = 2;
  //    pane.add(panChooseDir, gblc);
  //  }

  //  @SuppressWarnings("unused")
  //  private void creaRadios(Container pane) {
  //    JPanel panRadioB = new JPanel(new GridBagLayout());
  //    ButtonGroup m_rdbPrioGroup = new ButtonGroup();
  //    GridBagConstraints gbcPanGen2 = new GridBagConstraints();
  //
  //    gbcPanGen2.fill = GridBagConstraints.HORIZONTAL;
  //    gbcPanGen2.gridy = 0;
  //    gbcPanGen2.gridx = 1;
  //
  //    GridBagConstraints gbcRadio1 = new GridBagConstraints();
  //
  //    JRadioButton m_rdbPrioEFD = new JRadioButton("Exif File Dir");
  //    m_rdbPrioEFD.setActionCommand(EExifPriority.ExifFileDir.toString());
  //    m_rdbPrioEFD.setSelected(true);
  //    m_model.setPriority(EExifPriority.ExifFileDir);
  //    m_rdbPrioEFD.addActionListener(new ActionListener() {
  //
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locRadioPrio_Click(e);
  //      }
  //    });
  //    m_rdbPrioGroup.add(m_rdbPrioEFD);
  //    gbcRadio1.fill = GridBagConstraints.HORIZONTAL;
  //    gbcRadio1.gridx = 0;
  //    gbcRadio1.gridy = 0;
  //    panRadioB.add(m_rdbPrioEFD, gbcRadio1);
  //
  //    JRadioButton m_rdbPrioFDE = new JRadioButton("File Dir Exif");
  //    m_rdbPrioFDE.setActionCommand(EExifPriority.FileDirExif.toString());
  //    m_rdbPrioFDE.addActionListener(new ActionListener() {
  //
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locRadioPrio_Click(e);
  //      }
  //    });
  //
  //    m_rdbPrioGroup.add(m_rdbPrioFDE);
  //    GridBagConstraints gbcRadio2 = new GridBagConstraints();
  //    gbcRadio2.fill = GridBagConstraints.HORIZONTAL;
  //    gbcRadio2.gridx = 0;
  //    gbcRadio2.gridy = 1;
  //    panRadioB.add(m_rdbPrioFDE, gbcRadio2);
  //
  //    JRadioButton m_rdbPrioDFE = new JRadioButton("Dir File Exif");
  //    m_rdbPrioDFE.setActionCommand(EExifPriority.DirFileExif.toString());
  //    m_model.setPriority(EExifPriority.DirFileExif);
  //    m_rdbPrioDFE.setSelected(true);
  //    m_rdbPrioDFE.addActionListener(new ActionListener() {
  //
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locRadioPrio_Click(e);
  //      }
  //    });
  //
  //    m_rdbPrioGroup.add(m_rdbPrioDFE);
  //    GridBagConstraints gbcRadio3 = new GridBagConstraints();
  //    gbcRadio3.fill = GridBagConstraints.HORIZONTAL;
  //    gbcRadio3.gridx = 0;
  //    gbcRadio3.gridy = 2;
  //    panRadioB.add(m_rdbPrioDFE, gbcRadio3);
  //
  //    GridBagConstraints gblc = new GridBagConstraints();
  //    gblc.fill = GridBagConstraints.HORIZONTAL;
  //    gblc.gridx = 2;
  //    gblc.gridy = 0;
  //    gblc.gridheight = 2;
  //    pane.add(panRadioB, gblc);
  //
  //  }

  //  @SuppressWarnings("unused")
  //  private void creaJTable(Container pane) {
  //    table = new JTable();
  //    intestaTabella();
  //
  //    GridBagConstraints gblc = new GridBagConstraints();
  //    gblc.fill = GridBagConstraints.HORIZONTAL;
  //    gblc.gridx = 0;
  //    gblc.gridy = 2;
  //    gblc.gridwidth = 3;
  //    pane.add(table, gblc);
  //  }

  //  @SuppressWarnings("unused")
  //  private void creaCheck(Container pane) {
  //    JCheckBox chk = new JCheckBox("Recursive");
  //    chk.setSelected(true);
  //    m_model.setRecursive(true);
  //    chk.addActionListener(new ActionListener() {
  //
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locChkRecursive_Click(e);
  //      }
  //    });
  //
  //    GridBagConstraints gblc = new GridBagConstraints();
  //    gblc.fill = GridBagConstraints.HORIZONTAL;
  //    gblc.gridx = 0;
  //    gblc.gridy = 3;
  //    pane.add(chk, gblc);
  //  }

  //  @SuppressWarnings("unused")
  //  private void creaButtonExec(Container pane) {
  //    m_btExec = new JButton("Esegui");
  //    m_btExec.addActionListener(new ActionListener() {
  //
  //      @Override
  //      public void actionPerformed(ActionEvent e) {
  //        locBtEsegui_Click(e);
  //      }
  //    });
  //
  //    m_btExec.setEnabled(false);
  //
  //    GridBagConstraints gblc = new GridBagConstraints();
  //    gblc.fill = GridBagConstraints.HORIZONTAL;
  //    gblc.gridx = 1;
  //    gblc.gridy = 3;
  //    pane.add(m_btExec, gblc);
  //  }

  //  @SuppressWarnings("unused")
  //  private void creaLogArea(Container pane) {
  //    m_lblLogs = new JLabel();
  //    m_lblLogs.setHorizontalAlignment(SwingConstants.CENTER);
  //    m_lblLogs.setForeground(Color.BLUE);
  //
  //    GridBagConstraints gblc = new GridBagConstraints();
  //    gblc.fill = GridBagConstraints.HORIZONTAL;
  //    gblc.gridx = 0;
  //    gblc.gridy = 4;
  //    gblc.gridwidth = 3;
  //    pane.add(m_lblLogs, gblc);
  //
  //  }

  private void initPos(AppProperties prop) {
    int posX = prop.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_X);
    int posY = prop.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_Y);
    int dimX = prop.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_X);
    int dimY = prop.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_Y);

    // Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
    // Dimension screen = tk.getScreenSize();
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    // Returns an array of all of the screen GraphicsDevice objects.
    GraphicsDevice[] devices = env.getScreenDevices();
    int numberOfScreens = devices.length;

    if ( (dimX * dimY) > 0) {
      m_winDim = new Dimension(dimX, dimY);
      this.setPreferredSize(m_winDim);
    } else {
      m_winDim = new Dimension(400, 300);
      setPreferredSize(m_winDim);
    }
    if ( (posX * posY) != 0) {
      if (posX < 0 && numberOfScreens == 1)
        posX = 10;
      m_winPos = new Point(posX, posY);
      this.setLocation(m_winPos);
    }
  }

  @Override
  public void paint(Graphics p_g) {
    //    TimeThis tt = new TimeThis("paint");
    Graphics2D g2 = (Graphics2D) p_g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    super.paint(g2);
  }

  protected void locWindowClosing(WindowEvent p_e) {
    var siz = getSize();
    var pos = getLocation();
    AppProperties prop = AppProperties.getInst();

    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_X, siz.width);
    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_Y, siz.height);
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_X, pos.x);
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_Y, pos.y);

    prop.setLastDir(m_txDir.getText());
    prop.saveProperties();
    dispose();
  }

  protected void locTxDir_Click(ActionEvent e) {
    /*-
     * ProvaJFrame3.locTxDir_Click:
     * java.awt.event.ActionEvent[
     *    ACTION_PERFORMED,
     *    cmd=F:\My Foto\2017\2017-06-10 Motwertggwetrwe,
     *    when=1617553956216,
     *    modifiers=]
     *
     *    on javax.swing.JTextField[,54,6,246x20,
     *      layout=javax.swing.plaf.basic.BasicTextUI$UpdateHandler,
     *      alignmentX=0.0,
     *      alignmentY=0.0,
     *      border=com.sun.java.swing.plaf.windows.XPStyle$XPFillBorder@4e2cc0d4,
     *      flags=296,
     *      maximumSize=,
     *      minimumSize=java.awt.Dimension[width=80,height=20],
     *      preferredSize=,
     *      caretColor=javax.swing.plaf.ColorUIResource[r=0,g=0,b=0],
     *      disabledTextColor=javax.swing.plaf.ColorUIResource[r=109,g=109,b=109],
     *      editable=true,margin=javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2],selectedTextColor=javax.swing.plaf.ColorUIResource[r=255,g=255,b=255],selectionColor=javax.swing.plaf.ColorUIResource[r=0,g=120,b=215],columns=30,columnWidth=8,command=,horizontalAlignment=LEADING]
    
     */
    // System.out.println("ProvaJFrame3.locTxDir_Click:" + e.toString());
    String szDir = e.getActionCommand();
    System.out.printf("ProvaJFrame3.locTxDir_Click(\"%s\")\n", szDir);
    m_model.setDirectory(szDir);
    checkFattibilita();
  }

  protected void locBtChooseDir(ActionEvent e) {
    try {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      File fi = apriFileChooser();
      if (fi != null) {
        String szPth = fi.getAbsolutePath();
        System.out.println("Il dir :" + szPth);
        m_txDir.setText(szPth);
        m_model.setDirectory(szPth);
        checkFattibilita();
      }
    } finally {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  protected void locRadioPrio_Click(ActionEvent e) {
    String act = e.getActionCommand();
    System.out.printf("ProvaJFrame3.locRadioPrio_Click(\"%s\")\n", act);
    EExifPriority pp = EExifPriority.valueOf(act);
    if (pp != null) {
      m_model.setPriority(pp);
      checkFattibilita();
    }
  }

  protected void locChkRecursive_Click(ActionEvent e) {
    JCheckBox chk = (JCheckBox) e.getSource();
    boolean bSel = chk.isSelected();
    m_model.setRecursive(bSel);
    checkFattibilita();
  }

  protected void locBtAnalizza_Click(ActionEvent e) {
    try {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      clear();
      m_model.analizza();
      caricaGriglia();
    } finally {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  private void intestaTabella() {
    DefaultTableModel mod = new DefaultTableModel();

    mod.addColumn("Attuale");
    mod.addColumn("Percorso");
    mod.addColumn("Errore");
    mod.addColumn("Nuovo Nome");
    mod.addColumn("dt Nome File");
    mod.addColumn("dt Creazione");
    mod.addColumn("dt Ult Modif");
    mod.addColumn("dt Acquisizione");
    mod.addColumn("dt Parent Dir");
    mod.addColumn("dt Assunta");

    table.setModel(mod);
    // tbl.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    // tbl.getTableHeader().resizeAndRepaint();
  }

  private void caricaGriglia() {
    List<FSFile> li = m_model.getListFiles();
    intestaTabella();
    if (li == null || li.size() == 0) {
      sparaMess("Nessun file trovato !");
      return;
    }
    DefaultTableModel mod = (DefaultTableModel) table.getModel();
    for (FSFile fsf : li) {
      Object[] arr = new Object[10];
      arr[0] = fsf.getPath().getFileName().toString();
      arr[1] = fsf.getParent().toAbsolutePath().toString();
      if (fsf instanceof FSFoto) {
        FSFoto fot = (FSFoto) fsf;
        arr[2] = fot.isFileInError() ? "E" : "";
        arr[3] = fot.creaNomeFile();
        arr[4] = formatDt(fot.getDtNomeFile());
        arr[5] = formatDt(fot.getDtCreazione());
        arr[6] = formatDt(fot.getDtUltModif());
        arr[7] = formatDt(fot.getDtAcquisizione());
        arr[8] = formatDt(fot.getDtParentDir());
        arr[9] = formatDt(fot.getDtAssunta());
      }
      mod.addRow(arr);
    }
  }

  private String formatDt(LocalDateTime dt) {
    Date dt2 = null;
    Instant ll = null;
    try {
      if (dt != null) {
        ll = dt.atZone(ZoneId.systemDefault()).toInstant();
        if (ll.toString().indexOf("9999-") < 0)
          dt2 = Date.from(ll);
      }
    } catch (Exception e) {
      return e.getMessage();
    }
    return formatDt(dt2);
  }

  private String formatDt(Date dt2) {
    String szRet = "*";
    if (dt2 != null)
      szRet = MainFrame2.s_fmt.format(dt2);
    return szRet;
  }

  @Override
  public void addRow(String att, String nuo, Path loc, Date dt) {

    Object[] arr = new Object[4];
    arr[0] = att;
    arr[1] = nuo;
    arr[2] = loc.toAbsolutePath();
    arr[3] = MainFrame2.s_fmt.format(dt);
    DefaultTableModel mod = (DefaultTableModel) table.getModel();
    mod.addRow(arr);
  }

  protected void locBtEsegui_Click(ActionEvent e) {
    MainFrame2.s_log.error("btEsegui non ancora implementata");
  }

  private void checkFattibilita() {
    m_btAnalizza.setEnabled(m_model.isValoriOk());
    m_btExec.setEnabled(m_model.isValoriOk());
  }

  private File apriFileChooser() {
    File retFi = null;
    JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    jfc.setDialogTitle("Scegli il direttorio da scannerizzare");
    jfc.setMultiSelectionEnabled(false);
    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    AppProperties props = AppProperties.getInst();
    String sz = props.getLastDir();
    if (sz != null)
      jfc.setCurrentDirectory(new File(sz));
    int returnValue = 0;
    returnValue = jfc.showOpenDialog(this);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      retFi = jfc.getSelectedFile();
      props.setLastDir(retFi.getAbsolutePath());
      MainFrame2.s_log.info("Scelto file: {}", retFi.getAbsolutePath());
    }
    return retFi;
  }

  private void clear() {
    sparaMess(MainFrame2.s_fmt.format(new Date()) + " Inizio esecuzione");
    intestaTabella();
    m_model.clear();
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        /* MainFrame2 fra = */ new MainFrame2();
      }
    });
  }

  @Override
  public void sparaMess(String p_msg) {
    String szOut = "";
    if (p_msg != null)
      szOut = p_msg;
    m_lblLogs.setText(szOut);
  }

}
