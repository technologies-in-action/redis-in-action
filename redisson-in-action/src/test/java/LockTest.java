import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CountDownLatch;

public class LockTest {
    @Test
    @DisplayName("测试redisson锁")
    void testFairLock() throws InterruptedException {
        Counter counter = new Counter();
        int threads = 100;
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> counter.add(latch)).start();
        }
        latch.await();
        Assertions.assertEquals(threads, counter.get());
    }

    static class Counter {
        private int count;
        private final RLock lock;

        public Counter() {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");
            RedissonClient client = Redisson.create(config);
            lock = client.getFairLock("lock");
        }

        public int add(CountDownLatch latch) {
            try {
                lock.lock();
                this.count++;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                latch.countDown();
            }
            return count;
        }

        public int get() {
            return this.count;
        }
    }
}
