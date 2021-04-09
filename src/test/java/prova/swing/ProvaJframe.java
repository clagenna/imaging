package prova.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileSystemView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.sys.AppProperties;

public class ProvaJframe extends JFrame {

  /** serialVersionUID long */
  private static final long   serialVersionUID = 2520976559763357552L;

  private static final Logger s_log            = LogManager.getLogger(ProvaJframe.class);
  private ButtonGroup         m_rdbPrioGroup;
  private JTextField          m_txDir;

  public ProvaJframe() {
    initialize();
  }

  private void initialize() {
    AppProperties prop = new AppProperties();
    prop.openProperties();

    getContentPane().setPreferredSize(new Dimension(400, 300));

    JPanel panGeneral = new JPanel();
    panGeneral.setPreferredSize(new Dimension(600, 400));
    panGeneral.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
    getContentPane().add(panGeneral, BorderLayout.CENTER);

    GridBagLayout gblPanGen = new GridBagLayout();
    GridBagConstraints gbcPanGen = new GridBagConstraints();
    gbcPanGen.fill = GridBagConstraints.HORIZONTAL;
    gbcPanGen.ipady = 5;
    gbcPanGen.ipadx = 5;
    gbcPanGen.gridy = 0;
    gblPanGen.columnWidths = new int[] { 80, 80, 89, 0 };
    gblPanGen.rowHeights = new int[] { 89, 0, 0, 0 };
    gblPanGen.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
    gblPanGen.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
    panGeneral.setLayout(gblPanGen);

    // ------------ Choose Directory  ----------------
    JPanel panChooseDir = new JPanel();
    panChooseDir.setPreferredSize(new Dimension(50, 50));
    panChooseDir.setMinimumSize(new Dimension(50, 50));
    gbcPanGen.gridx = 0;
    panGeneral.add(panChooseDir, gbcPanGen);
    BorderLayout bl_panChooseDir = new BorderLayout();
    bl_panChooseDir.setVgap(5);
    bl_panChooseDir.setHgap(5);
    panChooseDir.setLayout(bl_panChooseDir);

    JLabel m_lbChoseDir = new JLabel("Directory");
    panChooseDir.add(m_lbChoseDir, BorderLayout.WEST);

    m_txDir = new JTextField();
    // m_txDir.setPreferredSize(new Dimension(120, 20));
    panChooseDir.add(m_txDir, BorderLayout.CENTER);
    m_txDir.setColumns(30);

    JButton m_btChooseDir = new JButton("Scegli...");
    m_btChooseDir.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        locBtChooseDir(e);
      }
    });
    panChooseDir.add(m_btChooseDir, BorderLayout.EAST);

    // ---------------   Radio Buttons   ---------------------
    JPanel panRadioB = new JPanel();
    m_rdbPrioGroup = new ButtonGroup();
    GridBagConstraints gbcPanGen2 = new GridBagConstraints();
    gbcPanGen2.fill = GridBagConstraints.HORIZONTAL;
    gbcPanGen2.gridy = 0;
    gbcPanGen2.gridx = 1;
    panGeneral.add(panRadioB, gbcPanGen2);

    GridBagLayout gblRadio = new GridBagLayout();
    gblRadio.columnWidths = new int[] { 312, 0 };
    gblRadio.rowHeights = new int[] { 69, 0 };
    gblRadio.columnWeights = new double[] { 1.0, 0.0 };
    gblRadio.rowWeights = new double[] { 0.0, 0.0, 1.0 };
    panRadioB.setLayout(gblRadio);

    GridBagConstraints gbcRadio1 = new GridBagConstraints();
    gbcRadio1.gridheight = 3;

    JRadioButton m_rdbPrioEFD = new JRadioButton("Exif File Dir");
    m_rdbPrioEFD.setSelected(true);
    m_rdbPrioGroup.add(m_rdbPrioEFD);
    gbcRadio1.gridx = 0;
    gbcRadio1.gridy = 0;
    panRadioB.add(m_rdbPrioEFD, gbcRadio1);

    JRadioButton m_rdbPrioFDE = new JRadioButton("File Dir Exif");
    m_rdbPrioGroup.add(m_rdbPrioFDE);
    GridBagConstraints gbcRadio2 = new GridBagConstraints();
    gbcRadio2.gridx = 0;
    gbcRadio2.gridy = 1;
    panRadioB.add(m_rdbPrioFDE, gbcRadio2);

    JRadioButton m_rdbPrioDFE = new JRadioButton("Dir File Exif");
    m_rdbPrioGroup.add(m_rdbPrioDFE);
    GridBagConstraints gbcRadio3 = new GridBagConstraints();
    gbcRadio3.gridx = 0;
    gbcRadio3.gridy = 2;
    panRadioB.add(m_rdbPrioDFE, gbcRadio3);

    JButton btnNewButton_1 = new JButton("Elabora");
    GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
    gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
    gbc_btnNewButton_1.gridx = 1;
    gbc_btnNewButton_1.gridy = 1;
    panGeneral.add(btnNewButton_1, gbc_btnNewButton_1);

    JList<String> list = new JList<>();
    GridBagConstraints gbc_list = new GridBagConstraints();
    gbc_list.gridwidth = 3;
    gbc_list.fill = GridBagConstraints.BOTH;
    gbc_list.gridx = 0;
    gbc_list.gridy = 2;
    panGeneral.add(list, gbc_list);

  }

  protected void locBtChooseDir(ActionEvent p_e) {
    try {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      File fi = apriFileChooser();
      if (fi != null) {
        System.out.println("Il dir :" + fi.getAbsolutePath());
        m_txDir.setText(fi.getAbsolutePath());
      }
    } finally {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

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
      s_log.info("Scelto file: {}", retFi.getAbsolutePath());
    }
    return retFi;
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
        ProvaJframe fra = new ProvaJframe();
        fra.pack();
        fra.setVisible(true);
      }
    });
  }
}
