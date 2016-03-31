package nl.devgames.utils;

public class Tuple<K,V> {

    private K k;
    private V v;

    public Tuple(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }
    public V getV() {
        return v;
    }

}
