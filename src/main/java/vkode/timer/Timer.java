package vkode.timer;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Timer implements TimerMBean {

    private final AtomicLong count = new AtomicLong();

    private final AtomicLong time = new AtomicLong();

    private final AtomicLong warnings = new AtomicLong();

    private final AtomicLong criticals = new AtomicLong();

    private final ConcurrentMap<Object, Long> timedObjects = new ConcurrentHashMap<Object,Long>();

    private Long warningThreshold;

    private Long criticalThreshold;

    private boolean logWarnings;

    private boolean logCriticals;

    public Timer() {
        this(null, null);
    }

    public Timer(Long warningThreshold, Long criticalThreshold) {
        this.logWarnings = warningThreshold != null;
        this.warningThreshold = warningThreshold;
        this.logCriticals = criticalThreshold != null;
        this.criticalThreshold = criticalThreshold;
    }

    public StandardMBean getMBean() {
        try {
            return new StandardMBean(this, TimerMBean.class);
        } catch (NotCompliantMBeanException e) {
            throw new IllegalStateException("Hear ye my complaint, " + TimerMBean.class + " should be compliant");
        }
    }

    public void start() {
        start(Thread.currentThread());
    }

    public void start(Object object) {
        Long time = timedObjects.putIfAbsent(object, System.nanoTime());
        if (time != null) {
            throw new IllegalArgumentException
                (this + " already timing object " + object +
                 ", started " + microsecondsSince(time) + " microseconds ago");
        }
    }

    public void end() {
        end(Thread.currentThread());
    }

    public void end(Object object) {
        Long startTime = timedObjects.remove(object);
        if (startTime == null) {
            throw new IllegalArgumentException(this + " is not timing " + object);
        }
        Long time = microsecondsSince(startTime);
        if (logWarnings && time > warningThreshold) {
            this.warnings.incrementAndGet();
        }
        if (logCriticals && time > criticalThreshold) {
            this.criticals.incrementAndGet();
        }
        this.time.addAndGet(time);
        this.count.incrementAndGet();
    }

    @Override
    public long getCriticalsCount() {
        return criticals.get();
    }

    @Override
    public long getWarningsCount() {
        return warnings.get();
    }

    @Override
    public double getAverageTimeNanos() {
        return 1000 * nanosecsAverage() / 1000.0;
    }

    @Override
    public double getAverageTimeMicros() {
        return nanosecsAverage() / 1000.0;
    }

    private long nanosecsAverage() {
        return 1000 * time.get() / count.get();
    }

    private static Long microsecondsSince(Long startTime) {
        return (System.nanoTime() - startTime) / 1000;
    }
}
