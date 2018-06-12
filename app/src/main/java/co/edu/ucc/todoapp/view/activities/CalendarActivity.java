package co.edu.ucc.todoapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.edu.ucc.todoapp.R;

public class CalendarActivity extends AppCompatActivity {

    Button btnSave, btnCancel;
    EditText txtTask;
    CalendarView _calendarView;
    long selectedDateInLong;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);

        // Caja de texto del nombre de tarea
        txtTask = (EditText)findViewById(R.id.txtTask);

        // Vista de calendario
        _calendarView = (CalendarView)findViewById(R.id.calendarView);
        _calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDateInLong = c.getTimeInMillis();
            }
        });

        // Botones
        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);

      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtTask.getText().length()!=0) {
                    // Obtiene la fecha como tipo de dato string
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String selectedDate = sdf.format(new Date(selectedDateInLong));

                    // Obtiene nombre de tarea
                    String taskName = txtTask.getText().toString();

                    // Obtiene el origen del actual intento
                    Intent i = getIntent();

                    // Establece los valores a retornar
                    i.putExtra("taskName", taskName);
                    i.putExtra("selectedDate", selectedDate);

                    // Establece el tipo de retorno como existoso
                    setResult(RESULT_OK, i);

                    // Termina actual intento y retorna lo establecido anteriormente
                    finish();
                } else {
                    Toast.makeText(CalendarActivity.this,
                            "Datos inválidos, por favor ingrese nombre de tarea y fecha",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
            }
        });
    }

}
