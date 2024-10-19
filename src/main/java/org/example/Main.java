package org.example;

import org.example.benchmark.CpuBenchmark;

public class Main {
    public static void main(String[] args) {
        CpuBenchmark benchmark = new CpuBenchmark();
        benchmark.testCpu(1, 5_000);
        benchmark.testCpu(4, 5_000);
        benchmark.testCpu(8, 5_000);
        benchmark.testCpu(12, 5_000);
        benchmark.testCpu(16, 5_000);
        benchmark.testCpu(24, 5_000);
        benchmark.testCpu(32, 5_000);
        benchmark.testCpu(64, 5_000);
    }
}