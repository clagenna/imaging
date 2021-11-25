package prova.swing;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ProvaJFrame3 extends JFrame {
  private static final long serialVersionUID = -5944233386505472137L;

  public ProvaJFrame3() {
    initComponents();
  }

  private void initComponents() {
    Container contp = getContentPane();
    contp.setLayout(new BorderLayout());
    PanProvaScegliDir pan = new PanProvaScegliDir();
    contp.add(pan, BorderLayout.NORTH);

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
        ProvaJFrame3 fra = new ProvaJFrame3();
        fra.pack();
        fra.setVisible(true);
      }
    });
  }

}
