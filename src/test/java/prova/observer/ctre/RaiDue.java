package prova.observer.ctre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RaiDue implements PropertyChangeListener {

  public RaiDue() {
    var cln = getClass().getSimpleName();
    System.out.println("Nata la Tv :" + cln);
  }

  @Override
  public void propertyChange(PropertyChangeEvent p_evt) {
    var cln = getClass().getSimpleName();
    System.out.printf("%s.aggiorna(%s)\n", cln, p_evt.getNewValue());
  }

}
