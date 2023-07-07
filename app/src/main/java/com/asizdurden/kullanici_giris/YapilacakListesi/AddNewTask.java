package com.asizdurden.kullanici_giris.YapilacakListesi;

import static com.asizdurden.kullanici_giris.YapilacakListesi.MainActivityTask.email;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.asizdurden.kullanici_giris.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.asizdurden.kullanici_giris.Model.ToDoModel;
import com.asizdurden.kullanici_giris.Utils.JsonHelper;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DialogCloseListener dialogCloseListener;
    private JsonHelper jsonHelper;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        jsonHelper = new JsonHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if (task != null && !task.isEmpty()) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
            }
        }

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                if (finalIsUpdate) {
                    int taskId = bundle.getInt("id");
                    jsonHelper.updateTask(taskId, text, email);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    jsonHelper.insertTask(task, email);
                }
                dismiss();
                if (dialogCloseListener != null) {
                    dialogCloseListener.handleDialogClose();
                }
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dialogCloseListener != null) {
            dialogCloseListener.handleDialogClose();
        }
    }

    public void setDialogCloseListener(DialogCloseListener listener) {
        this.dialogCloseListener = listener;
    }

    public interface DialogCloseListener {
        void handleDialogClose();
    }
}
