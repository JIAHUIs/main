package seedu.task.logic;

import static seedu.task.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import org.junit.Test;

import seedu.task.logic.commands.AddCommand;
import seedu.task.logic.commands.AddEventCommand;
import seedu.task.logic.commands.AddTaskCommand;
import seedu.task.logic.parser.ArgumentTokenizer;
import seedu.task.model.TaskBook;
import seedu.task.model.item.Deadline;
import seedu.task.model.item.Description;
import seedu.task.model.item.Event;
import seedu.task.model.item.EventDuration;
import seedu.task.model.item.Name;
import seedu.task.model.item.Task;

//@@author A0127570H
/*
 * Logic test for Add Command
 */
public class AddCommandTest extends CommandTest{

	/*
	 * 1) Invalid Add Command
	 *     - Invalid argument format
	 *     - Invalid data field format (Name, description, deadline and duration)
	 *     - Adding duplicate task
	 *         -> Task
	 *         -> Task with desc
	 *         -> Task with deadline
	 *         -> Task with desc and deadline
	 *     - Adding duplicate event
	 *         -> Event with duration and description
	 */ 
	
    // Invalid argument format
    @Test
    public void executeAdd_invalidArgsFormat_Unsuccessful() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandBehavior_task(
                "add", expectedMessage);
    }
    
    //Invalid data field format
    @Test 
    public void executeAddTask_invalidTaskData_Unsuccessful() throws Exception {
    	//Invalid Name 
    	assertCommandBehavior_task(
                "add []\\[;] /desc nil /by 30-12-16", Name.MESSAGE_NAME_CONSTRAINTS);
        assertCommandBehavior_task(
                "add []\\[;] /desc nil", Name.MESSAGE_NAME_CONSTRAINTS);
        
        //Invalid Deadline
        assertCommandBehavior_task(
                "add validName /desc validDesc /by randOmWord123", Deadline.MESSAGE_DEADLINE_CONSTRAINTS);
        
        //invalid abbreviation
        assertCommandBehavior_task(
                "add validName /desc validDesc /by Septem", Deadline.MESSAGE_DEADLINE_CONSTRAINTS);
        
        //empty deadline abbreviation
        assertCommandBehavior_task(
                "add validName /desc validDesc /by   ", ArgumentTokenizer.MESSAGE_EMPTY_VALUE);
        
        //empty desc abbreviation
        assertCommandBehavior_task(
                "add validName /desc    /by 1 September 17 ", ArgumentTokenizer.MESSAGE_EMPTY_VALUE);
        
        //invalid desc
        assertCommandBehavior_task(
                "add validName /desc  //  /by 1 September 17 ", Description.MESSAGE_DESCRIPTION_CONSTRAINTS);
    }
    
    //Invalid data field format
    @Test
    public void executeAddEvent_invalidEventData_unsuccessful() throws Exception {
        assertCommandBehavior_task(
                "add []\\[;] /desc nil /from 30-12-16 31-12-16", Name.MESSAGE_NAME_CONSTRAINTS);
        
        //start time after end time
        assertCommandBehavior_task("add valideventName /desc nil /from today /to yesterday", EventDuration.MESSAGE_DURATION_CONSTRAINTS);
        
        //empty start time not allowed if flag is keyed in. 
        assertCommandBehavior_task("add valideventName /desc nil /from  /to today 5pm", ArgumentTokenizer.MESSAGE_EMPTY_VALUE);
        
        //invalid start time not allowed. 
        assertCommandBehavior_task("add valideventName /desc nil /from  hahaha /to today 5pm", EventDuration.MESSAGE_DURATION_CONSTRAINTS);
    }

    //Duplicate task
    @Test
    public void executeAddTask_taskDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task book

        // execute command and verify result
        assertTaskCommandBehavior(
                helper.generateAddTaskCommand(toBeAdded),
                AddTaskCommand.MESSAGE_DUPLICATE_TASK,
                expectedAB,
                expectedAB.getTaskList());

    }
    
    //Duplicate task with desc
    @Test
    public void executeAddTask_descTaskDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingDescTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task book

        // execute command and verify result
        assertTaskCommandBehavior(
                helper.generateAddDescTaskCommand(toBeAdded),
                AddTaskCommand.MESSAGE_DUPLICATE_TASK,
                expectedAB,
                expectedAB.getTaskList());

    }
    
    //Duplicate task with deadline
    @Test
    public void executeAddTask_deadlineTaskDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingDeadlineTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task book

        // execute command and verify result
        assertTaskCommandBehavior(
                helper.generateAddDeadlineTaskCommand(toBeAdded),
                AddTaskCommand.MESSAGE_DUPLICATE_TASK,
                expectedAB,
                expectedAB.getTaskList());

    }
    
    //Duplicate task with name only
    @Test
    public void executeAddTask_nameTaskDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingNameTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // setup starting state
        model.addTask(toBeAdded); // task already in internal task book

        // execute command and verify result
        assertTaskCommandBehavior(
                helper.generateAddNameTaskCommand(toBeAdded),
                AddTaskCommand.MESSAGE_DUPLICATE_TASK,
                expectedAB,
                expectedAB.getTaskList());

    }
    
    //Duplicate event
    @Test
    public void executeAddEvent_eventDuplicate_notAllowed() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Event toBeAdded = helper.computingUpComingEvent();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addEvent(toBeAdded);

        // setup starting state
        model.addEvent(toBeAdded); // event already in internal task book

        // execute command and verify result
        assertEventCommandBehavior(
                helper.generateAddEventCommand(toBeAdded),
                AddEventCommand.MESSAGE_DUPLICATE_EVENT,
                expectedAB,
                expectedAB.getEventList());

    }
    
    /*
     * 2) Successful adding of tasks
     *  - Task with name only
     *  - Task with desc
     *  - Task with deadline
     *  - Task with desc and deadline in varying order
     *  - Multiple tasks with desc and deadline
     */
    
    //Multiple tasks with desc and deadline
    @Test
    public void executeAddTask_taskWithDescDeadline_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        
        // different argument to cover use cases for deadline as mentioned above
        Task tTarget1 = helper.generateTaskWithDeadline("Friday 11:01");
        Task tTarget2 = helper.generateTaskWithDeadline("next Friday 2pm");
        Task tTarget3 = helper.generateTaskWithDeadline("3 Monday");
        Task tTarget4 = helper.generateTaskWithDeadline("12/29/2017");
        Task tTarget5 = helper.generateTaskWithDeadline("12/30/2017 11:12");
        Task tTarget6 = helper.generateTaskWithDeadline("November 11 2018");
        
        TaskBook expectedAB = new TaskBook();
        List<Task> targetList = helper.generateTaskList(tTarget1, tTarget2, tTarget3, tTarget4, tTarget5, tTarget6);
        
        for(Task target: targetList) {
        	expectedAB.addTask(target);
        	assertTaskCommandBehavior(helper.generateAddTaskCommand(target),
                    String.format(AddTaskCommand.MESSAGE_SUCCESS, target),
                    expectedAB,
                    expectedAB.getTaskList());
        }
    }
    
    //Task with desc and deadline in varying order
    @Test
    public void executeAddTask_taskInVaryingOrder_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // execute 1st add task command and verify result
        assertTaskCommandBehavior(helper.generateAddTaskCommand(toBeAdded),
                String.format(AddTaskCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());
        
        Task toBeAdded2 = helper.computingDiffOrderedTask();
        expectedAB.addTask(toBeAdded2);
        
        // execute 2nd add task command and verify result
        assertTaskCommandBehavior(helper.generateDiffOrderedAddTaskCommand(toBeAdded2),
                String.format(AddTaskCommand.MESSAGE_SUCCESS, toBeAdded2),
                expectedAB,
                expectedAB.getTaskList());

    }    
    
    //Task with desc
    @Test
    public void executeAddTask_descTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingDescTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertTaskCommandBehavior(helper.generateAddDescTaskCommand(toBeAdded),
                String.format(AddTaskCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());

    }
    
    //Task with deadline
    @Test
    public void executeAddTask_deadlineTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingDeadlineTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertTaskCommandBehavior(helper.generateAddDeadlineTaskCommand(toBeAdded),
                String.format(AddTaskCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());

    }
    
    //Task with name only
    @Test
    public void executeAddTask_nameTask_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.computingNameTask();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addTask(toBeAdded);

        // execute command and verify result
        assertTaskCommandBehavior(helper.generateAddNameTaskCommand(toBeAdded),
                String.format(AddTaskCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getTaskList());

    }
    
    /*
     * 2) Successful adding of events
     *  - Event with duration
     *  - Event with desc and duration
     *  - Event with desc and duration in varying order
     *  - Event with end duration only
     */
    
    //Event with duration
    @Test
    public void executeAddEvent_eventWithDuration_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Event toBeAdded = helper.computingNoDescUpComingEvent();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addEvent(toBeAdded);

        // execute command and verify result
        assertEventCommandBehavior(helper.generateAddNoDescEventCommand(toBeAdded),
                String.format(AddEventCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getEventList());

    }
    
    //Event with desc and duration
    @Test
    public void executeAddEvent_descEvent_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Event toBeAdded = helper.computingUpComingEvent();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addEvent(toBeAdded);

        // execute command and verify result
        assertEventCommandBehavior(helper.generateAddEventCommand(toBeAdded),
                String.format(AddEventCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getEventList());

    }
    
    //Event with desc and duration in varying order
    @Test
    public void executeAddEvent_eventFieldInVaryingOrder_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Event toBeAdded = helper.computingUpComingEvent();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addEvent(toBeAdded);

        // execute command and verify result
        assertEventCommandBehavior(helper.generateDiffOrderedAddEventCommand(toBeAdded),
                String.format(AddEventCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getEventList());

    }
    
    //Event with end duration only
    @Test
    public void executeAddEvent_eventWithEndDuration_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Event toBeAdded = helper.computingEndDurationUpComingEvent();
        TaskBook expectedAB = new TaskBook();
        expectedAB.addEvent(toBeAdded);

        // execute command and verify result
        assertEventCommandBehavior(helper.generateAddNoDescEventCommand(toBeAdded),
                String.format(AddEventCommand.MESSAGE_SUCCESS, toBeAdded),
                expectedAB,
                expectedAB.getEventList());

    }

}
