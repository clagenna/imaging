package prova.collatz;

import java.util.ArrayList;
import java.util.List;

public class Collatz {

  private long MAX_NO = 56;

  public static void main(String[] args) {
    var app = new Collatz();
    app.doTheJob();
  }

  private void doTheJob() {
    for (long v = 5; v < MAX_NO; v++) {
      long k = v;
      List<Long> li = new ArrayList<>();
      while (k != 1) {
        li.add(k);
        if ( (k & 1) == 0)
          k = k / 2;
        else
          k = k * 3 + 1;
      }
      // System.out.printf("(%d) %s\n", li.size(), li);
      li.stream().forEach(s -> { System.out.printf("%.2f\t", Math.log( (double)s)); });
      System.out.println();
    }

  }

}
