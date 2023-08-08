package sm.claudio.imaging.gpx;

public interface IDicot<T> {
  void inizializza();

  void add(T cosa);

  T cercaDicot(T cosa);
}
