package org.example;

public class ComplexTask {
    private final int taskId;

    public ComplexTask(int taskId) {
        this.taskId = taskId;
    }

    public int execute() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " - начал выполнение задачи: " + taskId);
        int result = (int) (Math.random() * 100);
        try {
            Thread.sleep((int) (Math.random() * 9000) + 1000);
        } catch (InterruptedException e) {
            System.out.println(threadName + " - был прерван при выполнении задачи: " + taskId);
            Thread.currentThread().interrupt();
            return -1;
        }
        System.out.println(threadName + " - завершил выполнение задачи: " + taskId);
        return result;
    }
}
