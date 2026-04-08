package org.example;

import java.util.concurrent.*;

public class ComplexTaskExecutor {
    private final int quantityTasks;

    public ComplexTaskExecutor(int quantity) {

        this.quantityTasks = quantity;
    }

    public void executeTasks(){
        performGeneralTasks(quantityTasks);
    }

    public void executeTasks(int numberOfTasks){
        performGeneralTasks(numberOfTasks);
    }

    public void performGeneralTasks(int numberOfTasks){
        String testThreadName = Thread.currentThread().getName();
        ConcurrentLinkedQueue<Integer> results = new ConcurrentLinkedQueue<>();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfTasks);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfTasks, () -> {
            sumOfResults(results, testThreadName);
        });

        for (int i = 0; i < numberOfTasks; i++){
            final int taskId = i;
            executorService.submit(() ->{
                try {
                    ComplexTask task = new ComplexTask(taskId);
                    int result = task.execute();
                    results.add(result);
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Задача: " + taskId + " прервалась: " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        try {
            if(!executorService.awaitTermination(10, TimeUnit.SECONDS)){
                System.out.println("Остались незавершенные задачи");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Ожидание было прервано");
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void sumOfResults (ConcurrentLinkedQueue<Integer> results, String tradName){
        long sum = 0;
        for (int r: results){
            sum += r;
        }
        System.out.println(tradName + " Результат работы: " + sum);
    }
}
