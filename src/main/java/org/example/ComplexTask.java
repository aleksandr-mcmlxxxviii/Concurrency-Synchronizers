package org.example;

public class ComplexTask {
    private final int taskId;

    public ComplexTask(int taskId) {
        this.taskId = taskId;
    }

    public int execute(String testThreadName) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        int executionTime =  (int) (Math.random() * 9000) + 1000;
        int result = (int) (Math.random() * 100);

        System.out.println("В родительском потоке: " + testThreadName + "   поток: " + threadName + " - начал выполнение задачи: " + taskId + "   возвращаемый результат: " + result + "   длительность ms: " + executionTime);

        if (taskId == 5 || taskId == 1){
            Thread.currentThread().interrupt();
        }
        if (taskId == 3){
            throw new IllegalArgumentException("Не валидные данные");
        }

        Thread.sleep(executionTime);

        System.out.println("В родительском потоке: " + testThreadName + "   поток: " + threadName + " - завершилось выполнение задачи: " + taskId);
        return result;
    }
}
