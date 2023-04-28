package com.example.bai4_todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.bai4_todoapp.databinding.ActivityEditToDoBinding;
import com.example.bai4_todoapp.databinding.ActivityMainBinding;
import com.example.bai4_todoapp.model.TodoItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditToDoActivity extends AppCompatActivity {

    private ActivityEditToDoBinding binding;

    final Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private TodoItem selectedTodoItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditToDoBinding.inflate(getLayoutInflater());

        /* Change status bar color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get a reference to the activity's window
            Window window = getWindow();

            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.white));
        }

        binding.backButton.setOnClickListener(view -> {
            finish();
        });

        int pos = getIntent().getIntExtra("position", -1);

        if (pos == -1) {
            binding.label.setText("New Todo");
            binding.selectedDate.setText(dateFormat.format(new Date()));
        }
        else {

            binding.label.setText("Edit Todo");

            selectedTodoItem = MainActivity.filterTodoList.get(pos);

            binding.todoTitle.setText(selectedTodoItem.getTitle());
            binding.todoDescription.setText(selectedTodoItem.getDescription());
            binding.selectedDate.setText(dateFormat.format(selectedTodoItem.getCreatedDate()));
            binding.isDoneCheckbox.setChecked(selectedTodoItem.isDone());
        }

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.editScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(binding.editScreen.getWindowToken(), 0);
        });

        binding.chooseDateButton.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(binding.editScreen.getWindowToken(), 0);
            openDatePicker();
        });

        binding.isDoneCheckbox.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(binding.editScreen.getWindowToken(), 0);
        });

        binding.saveButton.setOnClickListener(view -> {

            if (binding.todoTitle.getText().toString().isEmpty()) {
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            TodoItem newTodo = new TodoItem(
                    String.valueOf(binding.todoTitle.getText()),
                    calendar.getTime(),
                    String.valueOf(binding.todoDescription.getText()),
                    binding.isDoneCheckbox.isChecked()
            );

            if (pos == -1) {

                MainActivity.fullTodoList.add(newTodo);
                MainActivity.todoAdapter.notifyDataSetChanged();
            }
            else {

                selectedTodoItem.setTitle(newTodo.getTitle());
                selectedTodoItem.setDescription(newTodo.getDescription());
                selectedTodoItem.setCreatedDate(newTodo.getCreatedDate());
                selectedTodoItem.setDone(newTodo.isDone());

                MainActivity.todoAdapter.notifyItemChanged(pos);
            }
            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
            finish();

        });

        setContentView(binding.getRoot());
    }

    private void openDatePicker() {
        int initDay = calendar.get(Calendar.DATE);
        int initMonth = calendar.get(Calendar.MONTH);
        int initYear = calendar.get(Calendar.YEAR);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                binding.selectedDate.setText(dateFormat.format(calendar.getTime()));
            }
        }, initYear, initMonth, initDay);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}