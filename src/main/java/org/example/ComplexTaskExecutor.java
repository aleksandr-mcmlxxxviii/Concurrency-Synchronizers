package org.example;

import java.util.Optional;
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
        ConcurrentLinkedQueue<Optional<Integer>> results = new ConcurrentLinkedQueue<>();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfTasks);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfTasks, () -> {
            sumOfResults(results, testThreadName);
        });

        for (int i = 1; i <= numberOfTasks; i++){
            final int taskId = i;
            executorService.submit(() ->{
                String localThreadName = Thread.currentThread().getName();
                try {
                    ComplexTask task = new ComplexTask(taskId);
                    int result = task.execute(testThreadName);
                    results.add(Optional.of(result));
                } catch (InterruptedException e) {
                    results.add(Optional.empty());
                    System.err.println("Прерывание в потоке " + localThreadName + ": " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    results.add(Optional.empty());
                    System.err.println("Ошибка в потоке " + localThreadName + ": " + e.getMessage());
                } finally {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        System.err.println("Поток: " + localThreadName + " был прерван в момент ожидания, ломаем барьер");
                    }catch (BrokenBarrierException e) {
                        System.err.println("Барьер сломался, поток: " + localThreadName + " выходит из ожидания");
                    }
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

    private void sumOfResults (ConcurrentLinkedQueue<Optional<Integer>> results, String tradName){
        System.out.println(results);
        long sum = 0;
        for (Optional<Integer> r: results){
            if (r.isPresent()){
                sum += r.orElse(0);
            }
        }
        System.out.println(tradName + " Результат работы: " + sum);
    }
}
