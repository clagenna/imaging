package prova.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.swing.ImgModel;
import sm.claudio.imaging.sys.AppProperties;

public class ProvaJFrame2 extends JFrame {

  /** long serialVersionUID */
  private static final long   serialVersionUID = -2259901874713949787L;
  private static final Logger s_log            = LogManager.getLogger(ProvaJFrame2.class);
  private Dimension           m_winDim;
  private Point               m_winPos;
  private JTextField          m_txDir;
  private ImgModel            m_model;
  private JTable              table;
  private JButton             m_btExec;
  private JLabel              m_lblLogs;

  public ProvaJFrame2() {
    initialize();
    creaComponents();
  }

  private void initialize() {
    AppProperties prop = new AppProperties();
    // prop.setSwingLogger(this);
    prop.setPropertyFile(new File("provajfram2.properties"));
    prop.openProperties();
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
    m_rdbPrioEFD.setSelected(true);
    m_rdbPrioEFD.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locRadioPrio_Click(e);
      }
    });

    JRadioButton m_rdbPrioFDE = new JRadioButton("File Dir Exif");
    panRadioB.add(m_rdbPrioFDE);
    m_rdbPrioGroup.add(m_rdbPrioFDE);
    m_rdbPrioFDE.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locRadioPrio_Click(e);
      }
    });

    JRadioButton m_rdbPrioDFE = new JRadioButton("New radio button");
    panRadioB.add(m_rdbPrioDFE);
    m_rdbPrioGroup.add(m_rdbPrioDFE);
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
    ckbRecurse.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        locChkRecursive_Click(e);
      }
    });
    panExec.add(ckbRecurse);

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

  }

  protected void locBtEsegui_Click(ActionEvent e) {
    // TODO Auto-generated method stub

  }

  protected void locChkRecursive_Click(ActionEvent e) {
    // TODO Auto-generated method stub

  }

  protected void locTxDir_Click(ActionEvent e) {
    // TODO Auto-generated method stub

  }

  protected void locRadioPrio_Click(ActionEvent e) {
    // TODO Auto-generated method stub

  }

  private void initPos(AppProperties prop) {
    int posX = prop.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_X);
    int posY = prop.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_Y);
    int dimX = prop.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_X);
    int dimY = prop.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_Y);

    Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    // Returns an array of all of the screen GraphicsDevice objects.
    GraphicsDevice[] devices = env.getScreenDevices();
    int numberOfScreens = devices.length;
    Dimension screen = tk.getScreenSize();

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

  private void creaComponents() {

  }

  private void intestaTabella() {
    DefaultTableModel mod = new DefaultTableModel();
    mod.addColumn("Attuale");
    mod.addColumn("Nuovo Nome");
    mod.addColumn("Percorso");
    mod.addColumn("Data");
    table.setModel(mod);

    // table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Attuale", "Nuovo", "Percorso", "Data" }));
    table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    table.getTableHeader().resizeAndRepaint();
    Path pth = Paths.get("c:", "java", "photon");
    for (int i = 1; i < 20; i++)
      addRow(String.valueOf(i), "Bbbbb", pth, new Date());
  }

  public void addRow(String att, String nuo, Path loc, Date dt) {
    final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Object[] arr = new Object[4];
    arr[0] = att;
    arr[1] = nuo;
    arr[2] = loc.toAbsolutePath();
    arr[3] = fmt.format(dt);
    DefaultTableModel mod = (DefaultTableModel) table.getModel();
    mod.addRow(arr);
  }

  protected void locWindowClosing(WindowEvent p_e) {
    var siz = getSize();
    var pos = getLocation();
    AppProperties prop = AppProperties.getInst();

    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_X, siz.width);
    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_Y, siz.height);
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_X, pos.x);
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_Y, pos.y);
    prop.saveProperties();
    dispose();

  }

  public static void main(String[] args) {
    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception l_e) {
      l_e.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        ProvaJFrame2 fra = new ProvaJFrame2();
        fra.pack();
        fra.setVisible(true);
      }
    });
  }

}
