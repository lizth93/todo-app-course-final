package co.edu.ucc.todoapp.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.edu.ucc.todoapp.R;
import co.edu.ucc.todoapp.data.entidades.mapper.TaskEntityMapper;
import co.edu.ucc.todoapp.data.repository.task.TaskDataSourceFactory;
import co.edu.ucc.todoapp.data.repository.task.TaskRepository;
import co.edu.ucc.todoapp.domain.usecase.InteractorTask;
import co.edu.ucc.todoapp.domain.usecase.task.AddTaskUseCase;
import co.edu.ucc.todoapp.domain.usecase.task.GetAllTaskUseCase;
import co.edu.ucc.todoapp.view.adapters.TaskAdapter;
import co.edu.ucc.todoapp.view.presenter.IMainPresenter;
import co.edu.ucc.todoapp.view.presenter.MainPresenter;
import co.edu.ucc.todoapp.view.viewmodels.TaskViewModel;
import co.edu.ucc.todoapp.view.viewmodels.mapper.TaskViewModelMapper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements IMainView {

    private FirebaseAuth firebaseAuth;
    private Button logout;

    @BindView(R.id.rvTask)
    RecyclerView rvTask;

    private IMainPresenter presenter;
    private TaskAdapter taskAdapter;
    private final static int taskCalendar = 0;
    EditText Tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenter(this,
            new AddTaskUseCase(
                Schedulers.io(),
                AndroidSchedulers.mainThread(),
                new TaskRepository(
                    new TaskEntityMapper(),
                    new TaskDataSourceFactory()
                )
            ),
            new GetAllTaskUseCase(
                Schedulers.io(),
                AndroidSchedulers.mainThread(),
                new TaskRepository(
                    new TaskEntityMapper(),
                    new TaskDataSourceFactory()
                )
            ),
            new InteractorTask(),
            new TaskViewModelMapper()
        );

        initAdapter();
        initRecyclerView();

        // logout
        firebaseAuth = FirebaseAuth.getInstance();
        logout = (Button) findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.getAllTask();
    }

    private void Logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MainActivity.this, MainEntryPoin.class));
    }

    private void initAdapter() {
        taskAdapter = new TaskAdapter();
    }

    private void initRecyclerView() {
        rvTask.setLayoutManager(new LinearLayoutManager(this));
        rvTask.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        rvTask.setAdapter(taskAdapter);
    }

    @Override
    public void showError(String error) {
        // Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessful() {
        // Toast.makeText(this, R.string.msg_successful, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fbAddTask)
    @Override
    public void addTask() {
        // Crea intento para recolectar datos en otra vista
        Intent i = new Intent(this, CalendarActivity.class);
        startActivityForResult(i,taskCalendar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_CANCELED) {
            // Si es así mostramos mensaje de cancelado por pantalla.
            Toast.makeText(this, "Creación de tarea cancelada", Toast.LENGTH_SHORT).show();
        } else {
            // De lo contrario, recogemos el resultado de la segunda actividad.

            // Obtiene los datos enviados por la actividad creada
            String taskName = data.getExtras().getString("taskName");
            String selectedDate = data.getExtras().getString("selectedDate");

            // Se agrega la tarea a la lista
            // ...
            presenter.addTask(taskName + " / Fecha: " + selectedDate);

            // Trea agregada a la lista, se muestra mensaje confirmado el hecho
            Toast.makeText(this, "Nueva tarea creada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showTasks(List<TaskViewModel> taskViewModels) {
        taskAdapter.addLstTask(taskViewModels);
        taskAdapter.notifyDataSetChanged();
    }
}