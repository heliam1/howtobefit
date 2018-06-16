package com.heliam1.HowToBeFit.repositories;

import com.heliam1.HowToBeFit.models.ExerciseSet;
import com.heliam1.HowToBeFit.models.StartTimeExerciseSetListPreviousExerciseSet;
import com.heliam1.HowToBeFit.models.PreviousExerciseSet;

import java.util.List;

import io.reactivex.Single;

public interface ExerciseSetRepository {
    // public Single<List<ExerciseSet>> getExerciseSetsByWorkoutId(long id);

    Single<List<StartTimeExerciseSetListPreviousExerciseSet>> getExerciseSetsByWorkoutIdandPreviousSets(long id, long date);

    public Single<List<PreviousExerciseSet>> getPreviousSets(String name, int setNumber);

    public Single<Long> saveExerciseSets(long time);

    public Single<Long> deleteExerciseSet(ExerciseSet exerciseSet);

    public Single<Long> deleteWorkout(long id);

    public Single<Long> updateWorkout(long id, long time);

    public void addStartExsetListprevset(StartTimeExerciseSetListPreviousExerciseSet element);

    public void replaceStartExsetListprevset(StartTimeExerciseSetListPreviousExerciseSet prev,
                                             StartTimeExerciseSetListPreviousExerciseSet present);

    public void removeStartExsetListprevset(StartTimeExerciseSetListPreviousExerciseSet element);

    public List<StartTimeExerciseSetListPreviousExerciseSet> getListStartExsetListprevset();

    public List<Long> getStartTimes();

    public void swapExerciseSetUp(StartTimeExerciseSetListPreviousExerciseSet startExsetPrev);

    public void swapExerciseSetDown(StartTimeExerciseSetListPreviousExerciseSet startExsetPrev);

    // Single<List<Task>> searchTasks(String title);
}
