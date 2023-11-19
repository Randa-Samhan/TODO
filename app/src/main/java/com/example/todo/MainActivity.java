package com.example.todo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyTasks";
    private static final String KEY_DUE_TASKS = "dueTasks";
    private static final String KEY_DONE_TASKS = "doneTasks";

    private EditText editTextTask;
    private Button btnAddTask;
    private Button btnClearTask;
    private ListView listViewDueTasks;
    private ListView listViewDoneTasks;
    private TextView textViewDueTasksCaption;
    private TextView textViewDoneTasksCaption;

    private ArrayList<String> dueTasks;
    private ArrayList<String> doneTasks;

    private ArrayAdapter<String> dueTasksAdapter;
    private ArrayAdapter<String> doneTasksAdapter;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnClearTask = findViewById(R.id.btnClearTasks);

        listViewDueTasks = findViewById(R.id.listViewDueTasks);
        listViewDoneTasks = findViewById(R.id.listViewDoneTasks);
        textViewDueTasksCaption = findViewById(R.id.textViewDueTasksCaption);
        textViewDoneTasksCaption = findViewById(R.id.textViewDoneTasksCaption);

        dueTasks = new ArrayList<>();
        doneTasks = new ArrayList<>();

        dueTasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dueTasks);
        doneTasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, doneTasks);

        listViewDueTasks.setAdapter(dueTasksAdapter);
        listViewDoneTasks.setAdapter(doneTasksAdapter);

        textViewDueTasksCaption.setText("Due Tasks");
        textViewDoneTasksCaption.setText("Done Tasks");

        gson = new Gson();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // clearTasks();
        // Load tasks from SharedPreferences
        loadTasks();

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskDescription = editTextTask.getText().toString().trim();
                if (!taskDescription.isEmpty()) {
                    Task newTask = new Task(taskDescription);
                    dueTasks.add(newTask.getDescription());
                    dueTasksAdapter.notifyDataSetChanged();
                    saveTasks();
                    editTextTask.setText("");
                }
            }
        });

        btnClearTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTasks();
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskDescription = editTextTask.getText().toString().trim();
                if (!taskDescription.isEmpty()) {
                    Task newTask = new Task(taskDescription);
                    dueTasks.add(newTask.getDescription());
                    dueTasksAdapter.notifyDataSetChanged();
                    saveTasks();
                    editTextTask.setText("");
                }
            }
        });

        // Handle item click for transferring tasks from due to done
        listViewDueTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task task = new Task(dueTasks.get(position));
            doneTasks.add(task.getDescription());
            doneTasksAdapter.notifyDataSetChanged();
            dueTasks.remove(position);
            dueTasksAdapter.notifyDataSetChanged();
            saveTasks();
        });
    }

    private void loadTasks() {
        String dueTasksJson = sharedPreferences.getString(KEY_DUE_TASKS, null);
        String doneTasksJson = sharedPreferences.getString(KEY_DONE_TASKS, null);

        Type taskListType = new TypeToken<ArrayList<String>>() {}.getType();

        // Clear existing tasks
        dueTasks.clear();
        doneTasks.clear();

        if (dueTasksJson != null) {
            ArrayList<String> loadedDueTasks = gson.fromJson(dueTasksJson, taskListType);
            dueTasks.addAll(loadedDueTasks);
        }

        if (doneTasksJson != null) {
            ArrayList<String> loadedDoneTasks = gson.fromJson(doneTasksJson, taskListType);
            doneTasks.addAll(loadedDoneTasks);
        }

        // Notify adapters after loading
        dueTasksAdapter.notifyDataSetChanged();
        doneTasksAdapter.notifyDataSetChanged();
    }

    private void saveTasks() {
        String dueTasksJson = gson.toJson(dueTasks);
        String doneTasksJson = gson.toJson(doneTasks);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DUE_TASKS, dueTasksJson);
        editor.putString(KEY_DONE_TASKS, doneTasksJson);
        editor.apply();
    }

    private void clearTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_DUE_TASKS);
        editor.remove(KEY_DONE_TASKS);
        editor.apply();

        // Clear the local lists and notify the adapters
        dueTasks.clear();
        doneTasks.clear();
        dueTasksAdapter.notifyDataSetChanged();
        doneTasksAdapter.notifyDataSetChanged();
    }
}
