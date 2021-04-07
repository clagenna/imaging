package sm.claudio.imaging.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.claudio.imaging.sys.AppProperties;

public class MainFrame extends JFrame implements PropertyChangeListener {

  /** serialVersionUID long */
  private static final long   serialVersionUID = -6465538027465008499L;

  private static final Logger s_log            = LogManager.getLogger(MainFrame.class);

  private AppProperties       prop;
  private Dimension           m_winDim;
  private Point               m_winPos;

  public MainFrame() {
    initialize();
  }

  public MainFrame(String p_tit) {
    super(p_tit);
    initialize();
  }

  private void initialize() {

    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception l_e) {
      s_log.error("Set Look and Feel", l_e);
    }

    prop = new AppProperties();
    prop.openProperties();

    setMinimumSize(new Dimension(400, 300));
    setTitle("Retagging di foto");
    
    int posX = prop.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_X);
    int posY = prop.getPropIntVal(AppProperties.CSZ_PROP_POSFRAME_Y);
    int dimX = prop.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_X);
    int dimY = prop.getPropIntVal(AppProperties.CSZ_PROP_DIMFRAME_Y);

    if ( (dimX * dimY) > 0) {
      m_winDim = new Dimension(dimX, dimY);
      this.setPreferredSize(m_winDim);
    } else {
      m_winDim = new Dimension(400, 300);
      setPreferredSize(m_winDim);
    }
    if ( (posX * posY) != 0) {
      m_winPos = new Point(posX, posY);
      this.setLocation(m_winPos);
    }

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent p_e) {
        locComponentResized(p_e);
      }

      @Override
      public void componentMoved(ComponentEvent p_e) {
        locComponentMoved(p_e);
      }
    });

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent p_e) {
        locWindowClosing(p_e);
      }
    });
    creaComponents();
    pack();
    setLocationRelativeTo(null);
    if (m_winPos != null)
      setLocation(m_winPos);
    else
      System.out.println("winPos e' *NULL* !?!?");

    var frame = this;
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        frame.setVisible(true);
      }
    });
  }

  private void creaComponents() {
    // TODO Auto-generated method stub

  }

  protected void locComponentMoved(ComponentEvent p_e) {
    m_winPos = p_e.getComponent().getLocation();
    // System.out.println("MainJFrame.locComponentMoved():" + p_e.toString());
  }

  protected void locComponentResized(ComponentEvent p_e) {
    m_winDim = p_e.getComponent().getSize();
    // System.out.println("MainJFrame.locComponentResized():" + m_winDim.toString());
  }

  protected void locWindowClosing(WindowEvent p_e) {
    var siz = getSize();
    var pos = getLocation();

    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_X, siz.width);
    prop.setPropVal(AppProperties.CSZ_PROP_DIMFRAME_Y, siz.height);
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_X, pos.x);
    prop.setPropVal(AppProperties.CSZ_PROP_POSFRAME_Y, pos.y);
    prop.saveProperties();
    dispose();

  }

  @Override
  public void propertyChange(PropertyChangeEvent p_evt) {
    // TODO Auto-generated method stub

  }

  public static void main(String[] args) {
    JFrame frame = new MainFrame("Rinomina JPEG");
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        frame.setVisible(true);
      }
    });
  }

}
