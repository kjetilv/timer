package vkode.timer;

public interface TimerMBean {

    double getAverageTimeNanos();

    double getAverageTimeMicros();

    long getWarningsCount();

    long getCriticalsCount();
}
