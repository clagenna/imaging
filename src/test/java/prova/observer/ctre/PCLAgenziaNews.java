package prova.observer.ctre;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCLAgenziaNews {

  private String                news;
  private PropertyChangeSupport pclSupp;

  public PCLAgenziaNews() {
    //
  }

  public void addPropertyChangeListener(PropertyChangeListener pcl) {
    if (pclSupp == null)
      pclSupp = new PropertyChangeSupport(this);
    pclSupp.addPropertyChangeListener(pcl);
  }

  public void removePropertyChangeListener(PropertyChangeListener pcl) {
    if (pclSupp == null)
      return;
    pclSupp.removePropertyChangeListener(pcl);
  }

  /**
   * Nella chiamata: <pre>
   * pclSupp.firePropertyChange("news", this.news, p_sz);
   * </pre>
   * <ul>
   * <li>il primo argomento è il nome della proprietà osservata.</li>
   * <li>Il secondo è il suo vecchio valore</li>
   * <li>Il terzo è il suo nuovo valore</li>
   * </ul>
   * di conseguenza gli osservatori dovrebbero implementare
   * {@link PropertyChangeListener }
   *
   * @param p_sz
   *          il valore della news
   */
  public void setNews(String p_sz) {
    pclSupp.firePropertyChange("news", this.news, p_sz);
    this.news = p_sz;
  }
}
