package com.example.bai4_todoapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bai4_todoapp.MainActivity;
import com.example.bai4_todoapp.R;
import com.example.bai4_todoapp.model.TodoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoItemViewHolder>{

    private Context mContext;
    private List<TodoItem> mTodoItems;
    private int positionOfLongpressItem;

    public int getPositionOfLongpressItem() {
        return positionOfLongpressItem;
    }

    public void setPositionOfLongpressItem(int position) {
        this.positionOfLongpressItem = position;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public TodoAdapter(Context context, List<TodoItem> todoItems) {
        mContext = context;
        mTodoItems = todoItems;
    }

    public void setData(List<TodoItem> todoItems) {
        mTodoItems = todoItems;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TodoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_card, parent, false);
        return new TodoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoItemViewHolder holder, int position) {

        TodoItem todoItem = mTodoItems.get(position);

        if (todoItem == null) {
            return;
        }

        holder.order.setText(String.format("#%d", position + 1));
        holder.title.setText(todoItem.getTitle());
        holder.date.setText(dateFormat.format(todoItem.getCreatedDate()));
        holder.checkbox.setChecked(todoItem.isDone());
    }

    @Override
    public int getItemCount() {
        if (mTodoItems == null) {
            return 0;
        }
        return mTodoItems.size();
    }

    public class TodoItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView order;
        private TextView title;
        private TextView date;
        private CheckBox checkbox;

        private Dialog dialog;

        public TodoItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnLongClickListener(view -> {
                int pos = getAdapterPosition();
                setPositionOfLongpressItem(pos);
                return false;
            });

            order = itemView.findViewById(R.id.todo_item_order);
            title = itemView.findViewById(R.id.todo_item_title);
            date = itemView.findViewById(R.id.todo_item_date);
            checkbox = itemView.findViewById(R.id.todo_item_checkbox);

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int pos = getAdapterPosition();
                TodoItem todoItem = mTodoItems.get(pos);
                todoItem.setDone(isChecked);
            });

            /* Setup trước cho Dialog cho click event*/
            {
                dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.todo_dialog);

                Window window = dialog.getWindow();

                if (window == null) {
                    return;
                }

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                /*
                 window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); khá quan trọng. THỬ BỎ ĐI SẼ HIỂU =)))
                 nếu bỏ dòng này đi thì các thuộc tính của LinearLayout mẹ trong todo_dialog.xml sẽ mất hết
                 thay vào đó sẽ là thuộc tính mặc định của dialog, còn nội dung vẫn giữ nguyên
                 */
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button todo_button = dialog.findViewById(R.id.close_button);
                todo_button.setOnClickListener(view -> {
                    dialog.dismiss();
                });
            }

            /* Xử lý sự kiện onClick -> set content cho dialog */
            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                TodoItem todoItem = mTodoItems.get(pos);

                TextView todo_title = dialog.findViewById(R.id.todo_title);
                TextView todo_date = dialog.findViewById(R.id.todo_date);
                TextView todo_description = dialog.findViewById(R.id.todo_description);
                CheckBox todo_checkbox = dialog.findViewById(R.id.todo_checkbox);

                todo_title.setText(todoItem.getTitle());
                todo_date.setText(dateFormat.format(todoItem.getCreatedDate()));
                todo_description.setText(todoItem.getDescription());
                todo_checkbox.setChecked(todoItem.isDone());

                todo_checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    todoItem.setDone(isChecked);
                    notifyItemChanged(pos);
                });

                dialog.show();
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = new MenuInflater(mContext);
            inflater.inflate(R.menu.item_longpress_menu, contextMenu);

            /*
             Disable "mark_as_done_option" đối với những todoItem đã có isDone = true
             Khi Longpress todoItem thì "itemView.setOnLongClickListener" ở dòng 101 sẽ kích hoạt trước
             -> update giá trị cho positionOfLongpressItem
             -> Get todoItem từ mTodoItems nhờ vào positionOfLongpressItem
             -> Check xem todoItem này đã Done chưa
             -> Done rồi thì Disable "mark_as_done_option"
             */
            TodoItem selectedTodoItem = mTodoItems.get(positionOfLongpressItem);
            if (selectedTodoItem.isDone()) {
                contextMenu.findItem(R.id.mark_as_done_option).setEnabled(false);
            }
        }
    }
}
