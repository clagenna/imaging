package prova.observer.bdue;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class ObsAgenziaNews extends Observable {

  private String news;

  public ObsAgenziaNews() {
    System.out.println("Nata l'agenzia ObsAgenziaNews");
  }

  public void setNews(String p_sz) {
    news = p_sz;
    setChanged();
    notifyObservers(news);
  }

}
