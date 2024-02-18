package ydb.kafka.connector.utils;

public class Record<K, V> {
    public K key;
    public V value;

    public Record(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
