package com.asizdurden.kullanici_giris.YapilacakListesi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asizdurden.kullanici_giris.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.asizdurden.kullanici_giris.Adapter.ToDoAdapter;
import com.asizdurden.kullanici_giris.Model.ToDoModel;
import com.asizdurden.kullanici_giris.Utils.JsonHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivityTask extends AppCompatActivity implements DialogCloseListener {
    private RecyclerView tasksRecyclerView;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private ToDoAdapter tasksAdapter;
    private JsonHelper jsonHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Objects.requireNonNull(getSupportActionBar()).hide();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        jsonHelper = new JsonHelper(MainActivityTask.this);

        email = firebaseUser.getEmail();
        taskList = jsonHelper.loadTasksFromJson(email);


        tasksAdapter = new ToDoAdapter(MainActivityTask.this, taskList);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter, jsonHelper));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AddNewTask fragment = AddNewTask.newInstance();
            fragment.setDialogCloseListener(() -> {
                // Dialog kapatıldığında yapılacak işlemler
                // Örneğin, RecyclerView'ı güncelleyebilirsiniz
                taskList = jsonHelper.loadTasksFromJson(email);
                tasksAdapter.setTasks(taskList);
                tasksAdapter.notifyDataSetChanged();
                MainActivityTask.this.recreate();
            });
            fragment.show(getSupportFragmentManager(), AddNewTask.TAG);
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = jsonHelper.loadTasksFromJson(email);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jsonHelper.close(email);
    }
}
