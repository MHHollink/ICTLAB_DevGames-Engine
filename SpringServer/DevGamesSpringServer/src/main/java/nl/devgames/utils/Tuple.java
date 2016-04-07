package nl.devgames.utils;

public class Tuple<K,V> {

    private K k;
    private V v;

    public Tuple(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public Tuple() {
    }

    public K getK() {
        return k;
    }
    public V getV() {
        return v;
    }

    public void setK(K k) {
        this.k = k;
    }
    public void setV(V v) {
        this.v = v;
    }
}
