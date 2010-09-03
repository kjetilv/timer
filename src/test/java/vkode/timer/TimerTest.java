package vkode.timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TimerTest {

    @Test
    public void testSimple() throws InterruptedException {
        Timer timer = new Timer(1L, 2L);
        timer.start();
        Thread.sleep(2);
        timer.end();
        assertEquals(1, timer.getCriticalsCount());
        assertEquals(1, timer.getWarningsCount());
        assertTrue(timer.getAverageTimeNanos() + " micros", timer.getAverageTimeNanos() >= 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFalseStart() {
        Timer timer = new Timer();
        timer.start();
        timer.start();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnhappyEndO() {
        new Timer().end();
    }
}
