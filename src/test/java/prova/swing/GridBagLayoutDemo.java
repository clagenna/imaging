package prova.swing;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GridBagLayoutDemo {
  final boolean shouldFill    = true;
  final boolean shouldWeightX = true;
  final boolean RIGHT_TO_LEFT = false;

  public void addComponentsToPane(Container pane) {
    if (RIGHT_TO_LEFT) {
      pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    pane.setLayout(new GridBagLayout());
    GridBagConstraints gblc = new GridBagConstraints();
    if (shouldFill) {
      //natural height, maximum width
      gblc.fill = GridBagConstraints.HORIZONTAL;
    }
    JButton button = new JButton("Button 1");

    if (shouldWeightX) {
      gblc.weightx = 0.2;
    }
    gblc.fill = GridBagConstraints.HORIZONTAL;
    gblc.gridx = 0;
    gblc.gridy = 0;
    pane.add(button, gblc);

    button = new JButton("Button 2");
    gblc.fill = GridBagConstraints.HORIZONTAL;
    if (shouldWeightX) {
      gblc.weightx = 0.4;
    }
    gblc.gridx = 1;
    gblc.gridy = 0;
    pane.add(button, gblc);

    button = new JButton("Button 3");
    gblc.fill = GridBagConstraints.HORIZONTAL;
    gblc.weightx = 0.6;
    gblc.gridx = 2;
    gblc.gridy = 0;
    pane.add(button, gblc);

    button = new JButton("Long-Named Button 4");
    gblc.fill = GridBagConstraints.HORIZONTAL;
    gblc.ipady = 40; //make this component tall
    gblc.weightx = 0.0;
    gblc.gridwidth = 3;
    gblc.gridx = 0;
    gblc.gridy = 1;
    pane.add(button, gblc);

    button = new JButton("5");
    gblc.fill = GridBagConstraints.HORIZONTAL;
    gblc.ipady = 0; //reset to default
    gblc.weighty = 1.0; //request any extra vertical space
    gblc.anchor = GridBagConstraints.PAGE_END; //bottom of space
    gblc.insets = new Insets(10, 0, 0, 0); //top padding
    gblc.gridx = 1; //aligned with button 2
    gblc.gridwidth = 2; //2 columns wide
    gblc.gridy = 2; //third row
    pane.add(button, gblc);
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  private void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("GridBagLayoutDemo");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    //Set up the content pane.
    addComponentsToPane(frame.getContentPane());

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GridBagLayoutDemo fra = new GridBagLayoutDemo();
        fra.createAndShowGUI();
      }
    });
  }
}
