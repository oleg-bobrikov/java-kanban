package kanban.managers.taskmanager;

import kanban.exceptions.ManagerSaveException;
import kanban.managers.Managers;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createInstance() {
        File taskManagerFile = new File("taskManager.txt");
        try {
            Files.deleteIfExists(taskManagerFile.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        return Managers.getFileBackedTaskManager(taskManagerFile);

    }

    @Test
    public void fileBackedTaskManagerShouldBeRestored() {
        // Создать несколько разных задач, эпиков и подзадач.
        Task task1 = addTask1();
        Task task2 = addTask2();
        Epic epic1 = addEpic1();
        Subtask subtask1 = addSubTask1(epic1);
        Subtask subtask2 = addSubTask2(epic1);
        Epic epic2 = addEpic2();

        // Запросить некоторые из них, чтобы заполнилась история просмотра.
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getEpic(epic2.getId());
        ArrayList<Task> taskViewHistory = taskManager.getHistory();

        // Создать новый FileBackedTasksManager менеджер из этого же файла.
        File newTaskManagerFile = new File("taskManager_copy.txt");
        try {
            Files.copy(taskManager.getFile().toPath(), newTaskManagerFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        InMemoryTaskManager newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);

        assertArrayEquals(taskViewHistory.toArray(), newTaskManager.getHistory().toArray(),
                "Отличается история просмотра задач.");
        assertEquals((InMemoryTaskManager) taskManager, (InMemoryTaskManager) newTaskManager,
                "Менежеры задач не совпадают.");

        //Проверить сохранение/восстановление при пустом списке задач
        taskManager.deleteTasks();
        try {
            Files.copy(taskManager.getFile().toPath(), newTaskManagerFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);
        assertEquals((InMemoryTaskManager) taskManager, (InMemoryTaskManager) newTaskManager,
                "Менежеры задач не совпадают.");

        //Проверить сохранение/восстановление при наличии эпика без подзадач
        taskManager.deleteEpics();
        addEpic1();
        taskManager.getEpic(epic1.getId());

        try {
            Files.copy(taskManager.getFile().toPath(), newTaskManagerFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);
        assertEquals((InMemoryTaskManager) taskManager, (InMemoryTaskManager) newTaskManager,
                "Менежеры задач не совпадают.");

        //Проверить сохранение/восстановление при пустом списке истории просмотра задач.
        taskManager.deleteEpics();
        addEpic1();
        addSubTask1(epic1);

        try {
            Files.copy(taskManager.getFile().toPath(), newTaskManagerFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);
        assertEquals((InMemoryTaskManager) taskManager, (InMemoryTaskManager) newTaskManager,
                "Менежеры задач не совпадают.");
    }

    @Test
    void main() {
        String[] parameters;
        parameters = new String[]{};
        boolean isFailed = false;
        try {
            FileBackedTaskManager.main(parameters);
        } catch (ManagerSaveException e) {
            isFailed = true;
        }
        assertFalse(isFailed);
    }
}