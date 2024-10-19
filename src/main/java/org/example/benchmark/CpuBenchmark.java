package org.example.benchmark;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class CpuBenchmark {
    public long testCpu(int threads, long timeMillis) {
        System.out.println("Benchmarking CPU with threads=" + threads + " and time=" + timeMillis);

        AtomicBoolean isRunning = new AtomicBoolean(true);

        PriorityBlockingQueue<Score> scores = new PriorityBlockingQueue<>(threads);

        CountDownLatch startLatch = new CountDownLatch(threads);

        try (ExecutorService executor = Executors.newFixedThreadPool(threads, Thread.ofPlatform().factory())) {
            executor.submit(() -> {
                startLatch.countDown();
                compute(0, isRunning, scores);
            });

            for (int i = 1; i < threads; i++) {
                int threadNumber = i;
                executor.submit(() -> {
                    startLatch.countDown();
                    compute(threadNumber, isRunning, scores);
                });
            }

            startLatch.await();
            Thread.sleep(timeMillis);

            isRunning.set(false);
            executor.awaitTermination(3, TimeUnit.SECONDS);
            executor.shutdown();

            long total = 0;

            Score score;
            while ((score = scores.poll()) != null) {
                System.out.println("Thread #" + score.threadNumber() + " score = " + score.score());
                total += score.score();
            }

            System.out.println("Average per thread score = " + Math.round((double) total / threads));
            System.out.println("Total score = " + total);
            return total;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compute(int threadNumber, AtomicBoolean isRunning, PriorityBlockingQueue<Score> scores) {
        long count = 0;

        while (isRunning.get()) {
            runOperation();
            count++;
        }

        scores.add(new Score(threadNumber, count));
    }

    private void runOperation() {
        for (int i = 0; i < 1000; i++) {
            double ignored = Math.sin(i) * Math.cos(i);
        }
    }

    private record Score(int threadNumber, long score) implements Comparable<Score> {
        @Override
        public int compareTo(Score o) {
            return Comparator
                    .comparingInt(Score::threadNumber)
                    .compare(this, o);
        }
    }
}
