package com.heliam1.HowToBeFit;

import com.heliam1.HowToBeFit.models.Workout;
import com.heliam1.HowToBeFit.presenters.WorkoutsPresenter;
import com.heliam1.HowToBeFit.repositories.WorkoutRepository;
import com.heliam1.HowToBeFit.ui.WorkoutsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WorkoutPresenterTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    WorkoutRepository workoutRepository;

    @Mock
    WorkoutsView workoutsView;

    WorkoutsPresenter workoutsPresenter;
    private final List<Workout> MANY_WORKOUTS = Arrays.asList(
            new Workout(1, "Chest", 0, "Sunday",75),
            new Workout(2, "Back", 0, "Monday", 42),
            new Workout(3, "Shoulders", 0, "Wednesday", 60),
            new Workout(4, "Arms", 0, "Thursday", 52),
            new Workout(5, "Legs/Abs", 0, "Friday", 54));

    @Before
    public void setUp() {
        workoutsPresenter = new WorkoutsPresenter(workoutsView, workoutRepository, Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void passWorkoutsToView() {
        when(workoutRepository.getWorkouts()).thenReturn(Single.just(MANY_WORKOUTS)); // given, find the view

        workoutsPresenter.loadWorkouts(); // when, - do something to the view

        Mockito.verify(workoutsView).displayWorkouts(MANY_WORKOUTS);
    }
    /*
    // should also test edge cases
    @Test
    public void shouldHandleNoTasksFound() {
        Mockito.when(taskRepository.getTasks()).thenReturn(Single.<List<Task>>just(EMPTY_LIST));

        presenter.loadTasks();

        Mockito.verify(view).displayNoTasks();
    }

    @Test
    public void shouldHandleError() {
        when(TasksRepositories.getTasksReactively()).thenReturn(Single.<List<Task>>error(new Throwable("boom")));

        presenter.loadTasks();

        verify(view).// Remember verify is " did we call this method on the mock"
    }*/
}