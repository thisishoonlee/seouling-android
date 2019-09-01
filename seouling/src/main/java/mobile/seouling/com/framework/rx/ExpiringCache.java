package mobile.seouling.com.framework.rx;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ExpiringCache<K, V> {

    private final Object lock = new Object();
    private final Map<K, V> map = new HashMap<>();
    private final Map<K, Disposable> subscriptions = new HashMap<>();
    private final long timeout;
    private final TimeUnit timeUnit;

    public ExpiringCache(long timeout, TimeUnit timeunit) {
        this.timeout = timeout;
        this.timeUnit = timeunit;
    }

    public int size() {
        return map.size();
    }

    public void put(K key, V value) {
        synchronized (lock) {
            V oldVal = map.put(key, value);
            if (oldVal != null) {
                Disposable oldSub = subscriptions.remove(key);
                if (oldSub != null) oldSub.dispose();
            }
            subscriptions.put(key, Observable.timer(timeout, timeUnit)
                    .subscribe(time -> {
                        remove(key);
                    }, error -> {}));
        }
    }

    public V remove(K key) {
        synchronized (lock) {
            V removed = map.remove(key);
            if (removed != null) {
                Disposable sub = subscriptions.remove(key);
                if (sub != null) sub.dispose();
            }
            return removed;
        }
    }

    public V get(K key) {
        synchronized (lock) {
            return map.get(key);
        }
    }
}
