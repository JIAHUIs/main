package seedu.task.testutil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;

import com.google.common.io.Files;

import guitests.guihandles.EventCardHandle;
import guitests.guihandles.TaskCardHandle;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import junit.framework.AssertionFailedError;
import seedu.task.TestApp;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.commons.util.FileUtil;
import seedu.task.commons.util.XmlUtil;
import seedu.task.model.TaskBook;
import seedu.task.model.item.Description;
import seedu.task.model.item.Name;
import seedu.task.model.item.ReadOnlyEvent;
import seedu.task.model.item.ReadOnlyTask;
import seedu.task.model.item.Task;
import seedu.task.model.item.UniqueEventList;
import seedu.task.model.item.UniqueTaskList;
import seedu.task.storage.XmlSerializableTaskBook;

/**
 * A utility class for test cases.
 */
public class TestUtil {

    public static String LS = System.lineSeparator();

    public static void assertThrows(Class<? extends Throwable> expected, Runnable executable) {
        try {
            executable.run();
        }
        catch (Throwable actualException) {
            if (!actualException.getClass().isAssignableFrom(expected)) {
                String message = String.format("Expected thrown: %s, actual: %s", expected.getName(),
                        actualException.getClass().getName());
                throw new AssertionFailedError(message);
            } else return;
        }
        throw new AssertionFailedError(
                String.format("Expected %s to be thrown, but nothing was thrown.", expected.getName()));
    }

    /**
     * Folder used for temp files created during testing. Ignored by Git.
     */
    public static String SANDBOX_FOLDER = FileUtil.getPath("./src/test/data/sandbox/");

    public static final Task[] sampleTaskData = getSampleTaskData();

    private static Task[] getSampleTaskData() {
        try {
            return new Task[]{
                    new Task(new Name("CS1010 CodeCrunch Practices"), new Description("20 Practices up to Lecture 7 syllabus"), null,false),
                    new Task(new Name("CS1020 CodeCrunch Practices"), new Description("20 Practices up to Lecture 7 syllabus"), null,false),
                    new Task(new Name("Computing Project 1"), new Description("Complete my part before meeting"),null, false),
                    new Task(new Name("Science Project 1"), new Description("Complete my part before meeting"), null,false),
                    new Task(new Name("Biz Project 1"), new Description("Complete my part before meeting"),null,false),
                    new Task(new Name("Engineering Project 1"), new Description("Complete my part before meeting"),null, false),
                    new Task(new Name("Music Project 1"), new Description("Complete my part before meeting"),null,false),
                    new Task(new Name("Arts Project 1"), new Description("Complete my part before meeting"),null,false),
                    new Task(new Name("Social Science Project 1"), new Description("Complete my part before meeting"),null,false),
            };
        } catch (IllegalValueException e) {
            //not possible
            return null;
        }
    }

    public static List<Task> generateSampleTaskData() {
        return Arrays.asList(sampleTaskData);
    }

    /**
     * Appends the file name to the sandbox folder path.
     * Creates the sandbox folder if it doesn't exist.
     * @param fileName
     * @return
     */
    public static String getFilePathInSandboxFolder(String fileName) {
        try {
            FileUtil.createDirs(new File(SANDBOX_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return SANDBOX_FOLDER + fileName;
    }

    public static void createDataFileWithSampleData(String filePath) {
        createDataFileWithData(generateSampleStorageTaskBook(), filePath);
    }

    public static <T> void createDataFileWithData(T data, String filePath) {
        try {
            File saveFileForTesting = new File(filePath);
            FileUtil.createIfMissing(saveFileForTesting);
            XmlUtil.saveDataToFile(saveFileForTesting, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... s) {
        createDataFileWithSampleData(TestApp.SAVE_LOCATION_FOR_TESTING);
    }

    public static TaskBook generateEmptyTaskBook() {
        return new TaskBook(new UniqueTaskList(), new UniqueEventList());
    }

    public static XmlSerializableTaskBook generateSampleStorageTaskBook() {
        return new XmlSerializableTaskBook(generateEmptyTaskBook());
    }

    /**
     * Tweaks the {@code keyCodeCombination} to resolve the {@code KeyCode.SHORTCUT} to their
     * respective platform-specific keycodes
     */
    public static KeyCode[] scrub(KeyCodeCombination keyCodeCombination) {
        List<KeyCode> keys = new ArrayList<>();
        if (keyCodeCombination.getAlt() == KeyCombination.ModifierValue.DOWN) {
            keys.add(KeyCode.ALT);
        }
        if (keyCodeCombination.getShift() == KeyCombination.ModifierValue.DOWN) {
            keys.add(KeyCode.SHIFT);
        }
        if (keyCodeCombination.getMeta() == KeyCombination.ModifierValue.DOWN) {
            keys.add(KeyCode.META);
        }
        if (keyCodeCombination.getControl() == KeyCombination.ModifierValue.DOWN) {
            keys.add(KeyCode.CONTROL);
        }
        keys.add(keyCodeCombination.getCode());
        return keys.toArray(new KeyCode[]{});
    }

    public static boolean isHeadlessEnvironment() {
        String headlessProperty = System.getProperty("testfx.headless");
        return headlessProperty != null && headlessProperty.equals("true");
    }

    public static void captureScreenShot(String fileName) {
        File file = GuiTest.captureScreenshot();
        try {
            Files.copy(file, new File(fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String descOnFail(Object... comparedObjects) {
        return "Comparison failed \n"
                + Arrays.asList(comparedObjects).stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    public static void setFinalStatic(Field field, Object newValue) throws NoSuchFieldException, IllegalAccessException{
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        // ~Modifier.FINAL is used to remove the final modifier from field so that its value is no longer
        // final and can be changed
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    public static void initRuntime() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.hideStage();
    }

    public static void tearDownRuntime() throws Exception {
        FxToolkit.cleanupStages();
    }

    /**
     * Gets private method of a class
     * Invoke the method using method.invoke(objectInstance, params...)
     *
     * Caveat: only find method declared in the current Class, not inherited from supertypes
     */
    public static Method getPrivateMethod(Class objectClass, String methodName) throws NoSuchMethodException {
        Method method = objectClass.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method;
    }

    public static void renameFile(File file, String newFileName) {
        try {
            Files.copy(file, new File(newFileName));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Gets mid point of a node relative to the screen.
     * @param node
     * @return
     */
    public static Point2D getScreenMidPoint(Node node) {
        double x = getScreenPos(node).getMinX() + node.getLayoutBounds().getWidth() / 2;
        double y = getScreenPos(node).getMinY() + node.getLayoutBounds().getHeight() / 2;
        return new Point2D(x,y);
    }

    /**
     * Gets mid point of a node relative to its scene.
     * @param node
     * @return
     */
    public static Point2D getSceneMidPoint(Node node) {
        double x = getScenePos(node).getMinX() + node.getLayoutBounds().getWidth() / 2;
        double y = getScenePos(node).getMinY() + node.getLayoutBounds().getHeight() / 2;
        return new Point2D(x,y);
    }

    /**
     * Gets the bound of the node relative to the parent scene.
     * @param node
     * @return
     */
    public static Bounds getScenePos(Node node) {
        return node.localToScene(node.getBoundsInLocal());
    }

    public static Bounds getScreenPos(Node node) {
        return node.localToScreen(node.getBoundsInLocal());
    }

    public static double getSceneMaxX(Scene scene) {
        return scene.getX() + scene.getWidth();
    }

    public static double getSceneMaxY(Scene scene) {
        return scene.getX() + scene.getHeight();
    }

    public static Object getLastElement(List<?> list) {
        return list.get(list.size() - 1);
    }

    /**
     * Removes a subset from the list of tasks.
     * @param tasks The list of tasks
     * @param tasksToRemove The subset of tasks.
     * @return The modified tasks after removal of the subset from tasks.
     */
    public static TestTask[] removeTasksFromList(final TestTask[] tasks, TestTask... tasksToRemove) {
        List<TestTask> listOfTasks = asList(tasks);
        listOfTasks.removeAll(asList(tasksToRemove));
        return listOfTasks.toArray(new TestTask[listOfTasks.size()]);
    }
    
    /**
     * Removes a subset from the list of events.
     * @param events The list of events
     * @param eventsToRemove The subset of events.
     * @return The modified events after removal of the subset from events.
     */
    public static TestEvent[] removeEventsFromList(final TestEvent[] events, TestEvent... eventsToRemove) {
        List<TestEvent> listOfEvents = asList(events);
        listOfEvents.removeAll(asList(eventsToRemove));
        return listOfEvents.toArray(new TestEvent[listOfEvents.size()]);
    }


    /**
     * Returns a copy of the list with the tasks at specified index removed.
     * @param list original list to copy from
     * @param targetIndexInOneIndexedFormat e.g. if the first element to be removed, 1 should be given as index.
     */
    public static TestTask[] removeTaskFromList(final TestTask[] list, int targetIndexInOneIndexedFormat) {
        return removeTasksFromList(list, list[targetIndexInOneIndexedFormat-1]);
    }
    
    /**
     * Returns a copy of the list with the events at specified index removed.
     * @param list original list to copy from
     * @param targetIndexInOneIndexedFormat e.g. if the first element to be removed, 1 should be given as index.
     */
    public static TestEvent[] removeEventFromList(final TestEvent[] list, int targetIndexInOneIndexedFormat) {
        return removeEventsFromList(list, list[targetIndexInOneIndexedFormat-1]);
    }

    /**
     * Replaces tasks[i] with a task.
     * @param tasks The array of tasks.
     * @param task The replacement task
     * @param index The index of the task to be replaced.
     * @return
     */
    public static TestTask[] replaceTaskFromList(TestTask[] tasks, TestTask task, int index) {
        tasks[index] = task;
        return tasks;
    }

    /**
     * Appends tasks to the array of tasks.
     * @param tasks A array of tasks.
     * @param tasksToAdd The tasks that are to be appended behind the original array.
     * @return The modified array of tasks.
     */
    public static TestTask[] addTasksToList(final TestTask[] tasks, TestTask... tasksToAdd) {
        List<TestTask> listOfTasks = asList(tasks);
        listOfTasks.addAll(asList(tasksToAdd));
        return listOfTasks.toArray(new TestTask[listOfTasks.size()]);
    }
    
    /**
     * Appends tasks to the array of tasks at certain index.
     * @param tasks A array of tasks.
     * @param tasksToAdd The tasks that are to be appended behind the original array.
     * @return The modified array of tasks.
     */
    public static TestTask[] addTasksToListAtIndex(final TestTask[] tasks,int index, TestTask... tasksToAdd) {
        List<TestTask> listOfTasks = asList(tasks);
        listOfTasks.addAll(index,asList(tasksToAdd));
        return listOfTasks.toArray(new TestTask[listOfTasks.size()]);
    }
    
    /**
     * Replaces a task in the array of tasks at certain index.
     * @param tasks A array of tasks.
     * @param taskToEdit The tasks that are to be edited.
     * @return The modified array of tasks.
     */
    public static TestTask[] editTasksToListAtIndex(final TestTask[] tasks,int index, TestTask taskToEdit) {
        List<TestTask> listOfTasks = asList(tasks);
        listOfTasks.set(index, taskToEdit);
        return listOfTasks.toArray(new TestTask[listOfTasks.size()]);
    }
    
    /**
     * Appends events to the array of events.
     * @param events A array of events.
     * @param eventsToAdd The events that are to be appended behind the original array.
     * @return The modified array of events.
     */
    public static TestEvent[] addEventsToList(final TestEvent[] events, TestEvent... eventsToAdd) {
        List<TestEvent> listOfEvents = asList(events);
        listOfEvents.addAll(asList(eventsToAdd));
        return listOfEvents.toArray(new TestEvent[listOfEvents.size()]);
    }
    
    /**
     * Appends events to the array of events.
     * @param events A array of events.
     * @param eventsToAdd The events that are to be appended behind the original array.
     * @return The modified array of events.
     */
    public static TestEvent[] addEventsToListAtIndex(final TestEvent[] events, int index, TestEvent... eventsToAdd) {
        List<TestEvent> listOfEvents = asList(events);
        listOfEvents.addAll(index, asList(eventsToAdd));
        return listOfEvents.toArray(new TestEvent[listOfEvents.size()]);
    }
    
    /**
     * Edits task according to index in the array of tasks.
     * @param tasks A array of tasks.
     * @param tasksToEdit The tasks that are to be edited in the original array.
     * @param index Integer of task index to edit
     * @return The modified array of tasks.
     */
    public static TestTask[] editTasksToList(final TestTask[] tasks, int index, TestTask taskToEdit) {
        List<TestTask> listOfTasks = asList(tasks);
        listOfTasks.set(index, taskToEdit);
        return listOfTasks.toArray(new TestTask[listOfTasks.size()]);
    }
    
    /**
     * Edits events according to index in the array of events.
     * @param events A array of events.
     * @param eventsToEdit The events that are to be edited in the original array.
     * @param index Integer of event index to edit
     * @return The modified array of events.
     */
    public static TestEvent[] editEventsToList(final TestEvent[] events, int index, TestEvent eventToEdit) {
        List<TestEvent> listOfEvents = asList(events);
        listOfEvents.set(index, eventToEdit);
        return listOfEvents.toArray(new TestEvent[listOfEvents.size()]);
    }

    private static <T> List<T> asList(T[] objs) {
        List<T> list = new ArrayList<>();
        for(T obj : objs) {
            list.add(obj);
        }
        return list;
    }

    public static boolean compareCardAndTask(TaskCardHandle card, ReadOnlyTask task) {
        return card.isSameTask(task);
    }
    
    public static boolean compareCardAndEvent(EventCardHandle card, ReadOnlyEvent event) {
        return card.isSameEvent(event);
    }

}
