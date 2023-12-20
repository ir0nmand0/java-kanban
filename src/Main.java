public class Main {

    public static void main(String[] args) {

/*        //тесты
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        taskManager.addTasks(task1);
        taskManager.addTasks(task2);
        Epic epic1 = new Epic("Эпик1", "Описание Эпика 1");
        Epic epic2 = new Epic("Эпик2", "Описание Эпика 2");

        taskManager.addEpics(epic1);
        taskManager.addEpics(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, epic1);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.DONE, epic2);

        taskManager.addSubtasks(epic1, subtask1);
        taskManager.addSubtasks(epic1, subtask2);
        taskManager.addSubtasks(epic2, subtask3);

        System.out.println("status epic1=" + epic1.getStatus());
        System.out.println("status epic2=" + epic2.getStatus());
        System.out.println("taskManager = " + taskManager);

        Subtask subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 4", Status.NEW, epic1);
        Subtask subtask5 = new Subtask("Подзадача 5", "Описание подзадачи 5", Status.NEW, epic1);
        Subtask subtask6 = new Subtask("Подзадача 6", "Описание подзадачи 6", Status.DONE, epic2);

        taskManager.updateSubtasks(epic1, subtask1, subtask4);
        taskManager.updateSubtasks(epic1, subtask2, subtask5);
        taskManager.updateSubtasks(epic2, subtask3, subtask6);

        System.out.println("status epic1=" + epic1.getStatus());
        System.out.println("status epic2=" + epic2.getStatus());
        System.out.println("taskManager = " + taskManager);

        taskManager.removeTask(task1);
        taskManager.removeSubtask(epic2, subtask6);

        System.out.println("taskManager = " + taskManager);

        taskManager.clearTasks();
        taskManager.clearEpics();

        System.out.println("taskManager = " + taskManager);*/

    }
}
