package prova.swing;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanProvaScegliDir extends JPanel {
  private static final long serialVersionUID = 8377443539414968465L;
  private JTextField        textField;

  /**
   * Create the panel.
   */
  public PanProvaScegliDir() {
    setLayout(new BorderLayout(0, 0));

    JLabel lblNewLabel = new JLabel("Cerca Dir");
    add(lblNewLabel, BorderLayout.WEST);

    textField = new JTextField();
    add(textField, BorderLayout.CENTER);
    textField.setColumns(10);

    JButton btCercaDir = new JButton(". . .");
    add(btCercaDir, BorderLayout.EAST);

  }

}
