package sm.claudio.imaging.gpx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class RicercaDicotomica<T extends IDistance<T>> implements IDicot<T> {

  private List<T> m_li;

  public RicercaDicotomica() {
  }

  @Override
  public void inizializza() {
    m_li = new LinkedList<>();
  }

  @Override
  public void add(T p_cosa) {
    if (m_li == null) {
      m_li = new LinkedList<>();
    }
    if ( !m_li.contains(p_cosa))
      m_li.add(p_cosa);
  }

  public void addAll(List<T> p_li) {
    if (m_li == null) {
      m_li = new LinkedList<>();
    }
    for (T ge : p_li) {
      add(ge);
    }
    // m_li.addAll(p_li);
    sort();
  }

  public void addAll(RicercaDicotomica<T> p_lLiDi) {
    addAll(p_lLiDi.m_li);
  }

  @Override
  public T cercaDicot(T p_cosa) {
    int qta = m_li.size();
    // passo al multiplo di 2
    double log2 = Math.round( (Math.log(qta) / Math.log(2)) + 0.5f);
    int qta2 = (int) Math.pow(2f, log2);
    int k = qta2 / 2;
    int kMax = qta - 1;
    // ----------------------------------------------------------------
    int incr = k;
    T ref = k > kMax ? m_li.get(kMax) : m_li.get(k);
    long diff = Math.abs(ref.distance(p_cosa));
    while (incr > 0) {
      incr /= 2;
      T l_ref = k > kMax ? m_li.get(kMax) : m_li.get(k);
      long l_diff = Math.abs(l_ref.distance(p_cosa));
      if (l_diff < diff) {
        ref = l_ref;
        diff = l_diff;
      }
      if (l_ref.compareTo(p_cosa) > 0) {
        k -= incr;
      } else {
        k += incr;
      }
    }
    // System.out.printf("cosa(%d) -> v(%d) diff(%d)\n", p_cosa, ref, diff);
    return ref;
  }

  public Stream<T> stream() {
    if (m_li == null)
      return null;
    return m_li.stream();
  }

  public long size() {
    if (m_li == null)
      return -1;
    return m_li.size();
  }

  public void sort() {
    if (size() <= 1)
      return;
    Collections.sort(m_li);
  }

  public void clear() {
    if (m_li != null)
      m_li.clear();
    inizializza();
  }

}
