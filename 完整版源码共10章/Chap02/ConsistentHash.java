
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash<T> {
	 private final HashFunction hashFunction;// hash�㷨
	 private final int numberOfReplicas;// ����ڵ���Ŀ
	 private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();
	 public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
	     Collection<T> nodes){ // ����ڵ�
	   this.hashFunction = hashFunction;
	   this.numberOfReplicas = numberOfReplicas;
	   for (T node : nodes) {
	     add(node);
	   }
	 }

	 public void add(T node) {
	   for (int i = 0; i < numberOfReplicas; i++) {
	     circle.put(hashFunction.hash(node.toString() + i), node);
	   }
	 }

	 public void remove(T node) {
	   for (int i = 0; i < numberOfReplicas; i++) {
	     circle.remove(hashFunction.hash(node.toString() + i));
	   }
	 }

	 public T get(Object key) {// �ؼ��㷨
	   if (circle.isEmpty()) {
	     return null;
	   }
	   // ����hashֵ
	   int hash = hashFunction.hash(key);
	   // ������������hashֵ
	   if (!circle.containsKey(hash)) {
	     SortedMap<Integer, T> tailMap = circle.tailMap(hash);
	     hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
	   }
	   return circle.get(hash);
	 }
}
	 
