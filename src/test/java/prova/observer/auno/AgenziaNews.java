package prova.observer.auno;

import java.util.ArrayList;
import java.util.List;

public class AgenziaNews {
  private String          news;
  private List<ICanaliTV> canali;

  public AgenziaNews() {
    System.out.println("Nata l'agenzia NEWS!");
  }

  public void setNews(String p_sz) {
    news = p_sz;
    if (canali == null)
      return;
    for (ICanaliTV cn : canali)
      cn.aggiorna(news);
  }

  public void addCanale(ICanaliTV chan) {
    if (canali == null)
      canali = new ArrayList<>();
    canali.add(chan);
    var cnNam = chan.getClass().getSimpleName();
    System.out.printf("AgenziaNews.addCanale(%s)\n", cnNam);
  }

  public void removeCanale(ICanaliTV chan) {
    if (canali == null)
      return;
    canali.remove(chan);
  }

}
