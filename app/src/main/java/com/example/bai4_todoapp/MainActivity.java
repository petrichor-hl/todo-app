package com.example.bai4_todoapp;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.getColor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.example.bai4_todoapp.adapter.TodoAdapter;
import com.example.bai4_todoapp.databinding.ActivityMainBinding;
import com.example.bai4_todoapp.model.TodoItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kotlinx.coroutines.Delay;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private ActivityMainBinding binding;
    public static List<TodoItem> fullTodoList;
    public static List<TodoItem> filterTodoList;
    InputMethodManager inputMethodManager;
    public static TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        /* Change status bar color */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get a reference to the activity's window
            Window window = getWindow();

            // change StatusBarColor
            window.setStatusBarColor(getColor(R.color.banner));
        }

        /* Options Menu */
        binding.optionMenuButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(this, view);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.options_menu);
            popup.show();
        });

        /* Recycler View */
        fullTodoList = dummyTodoItemData();
        filterTodoList = fullTodoList;

        todoAdapter = new TodoAdapter(this, fullTodoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        binding.rcvTodoList.setAdapter(todoAdapter);
        binding.rcvTodoList.setLayoutManager(linearLayoutManager);

        registerForContextMenu(binding.rcvTodoList);

        /* Search Bar */
        binding.searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString();

                if (!newText.isEmpty()) {
                    binding.clearTextButton.setVisibility(View.VISIBLE);
                    filterTodoList = fullTodoList.stream().filter(item -> item.getTitle().toLowerCase().contains(newText.toLowerCase())).collect(Collectors.toList());
                } else {
                    binding.clearTextButton.setVisibility(View.INVISIBLE);
                    filterTodoList = fullTodoList;
                }

                todoAdapter.setData(filterTodoList);
            }
        });

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        /* Clear Text Button */
        binding.clearTextButton.setOnClickListener(view -> {
            binding.searchEditText.setText("");
            binding.searchEditText.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(binding.clearTextButton.getWindowToken(), 0);
        });

        /* Hide Keyboard when click outside */
        binding.MainScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(binding.clearTextButton.getWindowToken(), 0);
        });

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_option:

                Intent intent = new Intent(this, EditToDoActivity.class);
                startActivity(intent);

                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = todoAdapter.getPositionOfLongpressItem();

        System.out.println("POSITION " + position);

        switch (item.getItemId()) {
            case R.id.edit_option:
                editItem(position);
                return true;
            case R.id.delete_option:
                deleteItem(position);
                return true;
            case R.id.mark_as_done_option:
                markAsDone(position);
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void editItem(int position) {
        Intent intent = new Intent(this, EditToDoActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void deleteItem(int position) {
        /*
         case: Người dùng tìm todoItem muốn xoá và xoá nó

         Ở ngữ cảnh này, item cần xoá là item nằm ở vị trí "position" trong filterTodoList

         Đầu tiên: Lấy ra item cần xoá selectedItem = filterTodoList.get(position);

         Xoá item ở vị trí position, cần làm 2 việc:
         -  Xoá trong filterTodoList bằng remove(position) vì nó nhanh (kh cần duyệt mảng)
         -  Khi user xoá content của search bar thì filterTodoList = fullTodoList
            mà fullTodoList vẫn còn tồn tại item cần xoá
            => Xoá item đó trong fullTodoList bằng remove(selectedItem)
               vì mình có biết vị trí item cần xoá nó nằm ở đâu trong fullTodoList đâu

         Note ngữ cảnh: filterTodoList được lọc từ fullTodoList
         Tức là: những phần tử trong filterTodoList thực chất là reference của những phầm tử trong fullTodoList
         Vì vậy, xoá phần từ trong filterTodoList chỉ là xoá cái reference đến phần tử gốc trong fullTodoList mà thoi
         => Muốn xoá triệt để phải xoá phần tử cần xoá trong cả fullTodoList
         */

        TodoItem selectedItem = filterTodoList.get(position);

        filterTodoList.remove(position);
        fullTodoList.remove(selectedItem);
        todoAdapter.notifyDataSetChanged();
    }

    private void markAsDone(int position) {
        filterTodoList.get(position).setDone(true);
        todoAdapter.notifyItemChanged(position);
    }

    private List<TodoItem> dummyTodoItemData() {
        return new ArrayList<>(Arrays.asList(
                new TodoItem(
                        "Đi xem phim",
                        new GregorianCalendar(2023, Calendar.APRIL, 21).getTime(),
                        "There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...",
                        false
                ),
                new TodoItem(
                        "Sinh nhật",
                        new GregorianCalendar(2023, Calendar.APRIL, 17).getTime(),
                        "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
                        true
                ),
                new TodoItem(
                        "Ai lấy miếng pho mát của tôi",
                        new GregorianCalendar(2022, Calendar.OCTOBER, 24).getTime(),
                        "Cách diệu kỳ giúp bạn đối đầu và vượt qua những thay đổi, khó khăn, thử thách trong công việc và cuộc sống.",
                        true
                ),
                new TodoItem(
                        "You are the apple of my eye",
                        new GregorianCalendar(2017, Calendar.APRIL, 1).getTime(),
                        "Cô gái năm ấy chúng ta cùng theo đuổi.",
                        false
                ),
                new TodoItem(
                        "Nhà giả kim",
                        new GregorianCalendar(2003, Calendar.FEBRUARY, 5).getTime(),
                        "Khi bạn khao khát một điều gì đó, cả vũ trụ sẽ hợp lực giúp bạn đạt được điều đó.",
                        true
                ),
                new TodoItem(
                        "Không gia đình",
                        new GregorianCalendar(2001, Calendar.AUGUST, 12).getTime(),
                        "Gia đình không phải là việc cháu mang dòng máu của ai. Mà là việc cháu yêu thương, chia sẻ, cảm thông và quan tâm đến ai.",
                        true
                )
        ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
    }
}