package com.asizdurden.kullanici_giris.Adapter;


import static com.asizdurden.kullanici_giris.YapilacakListesi.MainActivityTask.email;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.asizdurden.kullanici_giris.Model.ToDoModel;
import com.asizdurden.kullanici_giris.R;
import com.asizdurden.kullanici_giris.Utils.JsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> taskList;
    private Context context;
    private JsonHelper jsonHelper;

    public ToDoAdapter(Context context, List<ToDoModel> taskList) {
        this.context = context;
        this.taskList = taskList;
        jsonHelper = new JsonHelper(context);
    }

    public Context getContext() {
        // Adapterinizin bağlı olduğu context'i döndürün
        return context;
    }

    public void deleteItem(int position) {
        // İlgili pozisyondaki öğeyi listeden silin
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position, String email) {
        // İlgili pozisyondaki öğeyi alın
        ToDoModel item = taskList.get(position);

        // TODO: İlgili pozisyondaki öğeyi düzenlemek için gerekli işlemleri yapın

        // Örneğin, bir dialog göstererek kullanıcıdan yeni veri girmesini isteyebilirsiniz
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");
        final EditText editText = new EditText(context);
        editText.setText(item.getTask());
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newText = editText.getText().toString();
                // Yeni metni ilgili öğeye atayın
                item.setTask(newText);
                // Güncelleme işlemini gerçekleştirin
                jsonHelper.updateTask(item.getId(), newText, email);
                // RecyclerView'e değişiklikleri bildirin
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ToDoModel item = taskList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));

        holder.task.setOnCheckedChangeListener(null); // Önceki dinleyiciyi kaldır

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int newStatus = isChecked ? 1 : 0;
                item.setStatus(newStatus);
                jsonHelper.updateStatusInJson(item.getId(),newStatus, email);
            }
        });
    }


    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.taskList = todoList;
        notifyDataSetChanged();
    }

    private void loadTasksFromJson(String userEmail, String fileName) {

        fileName = "tasks_" + userEmail + ".json";
        try {
            InputStream inputStream;
            if (jsonHelper.isFileExists(fileName)) {

                inputStream = context.openFileInput(fileName);
            } else {
                inputStream = context.getAssets().open("tasks.json");
            }
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ToDoModel>>() {
            }.getType();
            taskList = gson.fromJson(json, listType);

            if (taskList == null) {
                taskList = new ArrayList<>();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasksToJson(String email) {
        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        try {
            OutputStream outputStream = context.openFileOutput("tasks_" + email + ".json", Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
