package com.asizdurden.kullanici_giris.Utils;

import android.content.Context;
import android.util.Log;

import com.asizdurden.kullanici_giris.Model.ToDoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    private static final String TAG = "JsonHelper";
    private static final String JSON_FILE_NAME = "tasks.json";

    private Context context;

    public JsonHelper(Context context) {
        this.context = context;
    }

    public void updateStatusInJson(int taskId, int newStatus, String email) {
        try {
            FileInputStream fileInputStream = context.openFileInput("tasks_" + email + ".json");
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();

            String jsonString = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject taskObject = jsonArray.getJSONObject(i);
                int id = taskObject.getInt("id");
                if (id == taskId) {
                    taskObject.put("status", newStatus);
                    break;
                }
            }

            String updatedJsonString = jsonArray.toString();

            FileOutputStream fileOutputStream = context.openFileOutput("tasks_" + email + ".json", Context.MODE_PRIVATE);
            fileOutputStream.write(updatedJsonString.getBytes());
            fileOutputStream.close();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    public List<ToDoModel> loadTasksFromJson(String userEmail) {
        List<ToDoModel> taskList = new ArrayList<>();

        try {
            String jsonString = loadJsonFromFile(userEmail);
            if (jsonString != null) {
                JSONArray jsonArray = new JSONArray(jsonString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ToDoModel task = new ToDoModel();
                    task.setId(jsonObject.getInt("id"));
                    task.setTask(jsonObject.getString("task"));
                    task.setStatus(jsonObject.getInt("status"));
                    taskList.add(task);
                }
            } else {
                createEmptyJsonFile(userEmail); // Eğer JSON dosyası yoksa boş bir JSON dosyası oluştur
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON dosyası yüklenirken hata oluştu: " + e.getMessage());
            return taskList;
        }

        return taskList;
    }

    private void createEmptyJsonFile(String userEmail) {
        JSONArray jsonArray = new JSONArray();

        String jsonString = jsonArray.toString();
        writeJsonToFile(jsonString, userEmail);
    }

    public void saveTasksToJson(List<ToDoModel> taskList, String userEmail) {
        JSONArray jsonArray = new JSONArray();

        for (ToDoModel task : taskList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", task.getId());
                jsonObject.put("task", task.getTask());
                jsonObject.put("status", task.getStatus());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "JSON nesnesi oluşturulurken hata oluştu: " + e.getMessage());
            }
        }

        writeJsonToFile(jsonArray.toString(), userEmail);
    }


    public void close(String userEmail) {
        // Görevleri JSON dosyasına kaydetmeden önce kapatma işlemi yapılır
        List<ToDoModel> taskList = loadTasksFromJson(userEmail);
        saveTasksToJson(taskList, userEmail);

        // Ek temizlik veya kaynakları serbest bırakma işlemleri burada gerçekleştirilebilir
        // Örneğin, açık olan dosya veya veritabanı bağlantıları kapatılabilir

        // taskList ve context referanslarını temizleme
        taskList = null;
        context = null;
    }


    public void insertTask(ToDoModel task, String userEmail) {
        List<ToDoModel> taskList = loadTasksFromJson(userEmail);
        taskList.add(task);
        saveTasksToJson(taskList, userEmail);
    }


    public void updateTask(int taskId, String newTask, String userEmail) {
        List<ToDoModel> taskList = loadTasksFromJson(userEmail);

        for (ToDoModel task : taskList) {
            if (task.getId() == taskId) {
                task.setTask(newTask);
                break;
            }
        }

        saveTasksToJson(taskList, userEmail);
    }

    public void deleteTask(int position, String userEmail) {
        // JSON dosyasındaki ilgili pozisyondaki görevi silin
        List<ToDoModel> taskList = loadTasksFromJson(userEmail);
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            saveTasksToJson(taskList, userEmail);
        }
    }

    public boolean isFileExists(String fileName) {
        String[] files = context.fileList();
        for (String file : files) {
            if (file.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private String loadJsonFromFile(String userEmail) {
        String json = null;
        try {
            String fileName = "tasks_" + userEmail + ".json";
            InputStream inputStream;
            if (isFileExists(fileName))
                inputStream = context.openFileInput(fileName);
            else
                inputStream = context.getAssets().open(fileName);

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "JSON dosyası okunurken hata oluştu: " + e.getMessage());
        }
        return json;
    }


    private void writeJsonToFile(String json, String userEmail) {
        String fileName = "tasks_" + userEmail + ".json";

        try {
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing JSON file: " + e.getMessage());
        }
    }

}