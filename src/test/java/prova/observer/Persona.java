package prova.observer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Il test per capire come funziona una {@link StringProperty}<br/>
 * Il concetto alla base è che le property di una classe possono essere
 * osservabili dal punto di vista del {@link ChangeListener} e quindi
 * seguite/tracciabili.<br/>
 * Cosi posso avere un metodo {@link #nameChanged(String, String)} che riceve il
 * segnale di cambio.
 *
 * @author claudio
 *
 */
public class Persona {

  private final StringProperty name = new SimpleStringProperty();

  public final String getName() {
    return name.get();
  }

  public final void setName(String value) {
    name.set(value);
  }

  public static void main(String[] args) {
    Persona person = new Persona();
    person.init();
    person.doTheJob();
  }

  private void init() {
    name.addListener(new ChangeListener<String>() {

      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        nameChanged(oldValue, newValue);
      }

    });
  }

  private void doTheJob() {

    setName("Anakin Skywalker");
    setName("Darth Vader");
  }

  protected void nameChanged(String p_oldValue, String p_newValue) {
    System.out.printf("Il nome e' cambiato da \"%s\" verso \"%s\"\n", p_oldValue, p_newValue);
  }

}
