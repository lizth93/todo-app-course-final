package co.edu.ucc.todoapp.view.presenter;

import java.util.List;

import co.edu.ucc.todoapp.domain.model.Task;
import co.edu.ucc.todoapp.domain.usecase.IInteractorTask;
import co.edu.ucc.todoapp.domain.usecase.InteractorTask;
import co.edu.ucc.todoapp.domain.usecase.task.AddTaskUseCase;
import co.edu.ucc.todoapp.domain.usecase.task.GetAllTaskUseCase;
import co.edu.ucc.todoapp.view.activities.IMainView;
import co.edu.ucc.todoapp.view.viewmodels.mapper.TaskViewModelMapper;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class MainPresenter implements IMainPresenter {

    private IMainView view;
    private final AddTaskUseCase addTaskUseCase;
    private final GetAllTaskUseCase getAllTaskUseCase;
    private final IInteractorTask interactorTask;
    private TaskViewModelMapper mapper;

    public MainPresenter(IMainView view,
                         AddTaskUseCase addTaskUseCase,
                         GetAllTaskUseCase getAllTaskUseCase,
                         IInteractorTask interactorTask,
                         TaskViewModelMapper mapper) {
        this.view = view;
        this.addTaskUseCase = addTaskUseCase;
        this.getAllTaskUseCase = getAllTaskUseCase;
        this.interactorTask = interactorTask;
        this.mapper = mapper;
    }


    @Override
    public void addTask(String name) {
        try {
            Task task = new Task();
            task.setName(name);
            task.setDone(false);

            addTaskUseCase.setTask(task);
            addTaskUseCase.execute(new TaskObserver());

            interactorTask.add(task);
            List<Task> tasks = interactorTask.getAll();
            view.showTasks(mapper.map(tasks));
            view.showSuccessful();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }

    }
    private class TaskObserver extends DisposableObserver<Boolean> {

        @Override
        public void onNext(Boolean aBoolean) {
            view.showSuccessful();
            getAllTaskUseCase.execute(new TaskGetObserver());
        }

        @Override
        public void onError(Throwable e) {
            view.showError(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    }

    @Override
    public void getAllTask() {
        try {
            List<Task> tasks = interactorTask.getAll();
            view.showTasks(mapper.map(tasks));
            view.showSuccessful();
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    private class TaskGetObserver extends DisposableObserver<List<Task>> {


        @Override
        public void onNext(List<Task> tasks) {
            view.showTasks(mapper.map(tasks));
        }

        @Override
        public void onError(Throwable e) {
            view.showError(e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    }

}