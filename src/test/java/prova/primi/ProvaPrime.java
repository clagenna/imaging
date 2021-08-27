package prova.primi;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ProvaPrime {
  List<BigInteger> li;

  @Test
  public void doTheJob() {
    int fine = 20000;
    li = new ArrayList<BigInteger>();
    Instant t1 = Instant.now();
    for (int i = 2; i < fine; i++) {
      BigInteger b1 = BigInteger.valueOf(6 * i - 1);
      BigInteger b2 = BigInteger.valueOf(6 * i + 1);
      if (b1.isProbablePrime(10000))
        li.add(b1);
      if (b2.isProbablePrime(10000))
        li.add(b2);
    }
    Instant t2 = Instant.now();
    double f = Duration.between(t1, t2).toMillis() / 1000F;
    System.out.printf("Passato %.4f, qta=%d\n", f, li.size());
    controlla(li);
    // li.stream().forEach(s -> System.out.printf("%d\n", s.intValue()));
    Instant t3 = Instant.now();
    f = Duration.between(t2, t3).toMillis() / 1000F;
    System.out.printf("Durata controllo %.4f\n", f);
  }

  private void controlla(List<BigInteger> li2) {
    for (BigInteger ii : li2) {
      int v = ii.intValue();
      if ( !sePrimo(v))
        System.err.printf("No primo=%d\n", v);
    }
  }

  private boolean sePrimo(int v) {
    if (v <= 1)
      return false;
    if (v == 2)
      return true;
    if ( (v % 2) == 0)
      return false;
    if ( (v % 3) == 0)
      return false;
    if ( (v % 5) == 0)
      return false;
    int k = (int) Math.sqrt(v);
    for (int n = 7; n <= k; n += 2)
      if ( (v % n) == 0)
        return false;
    return true;
  }
}
