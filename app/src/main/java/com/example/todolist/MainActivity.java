package com.example.todolist;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EditTaskDialog.EditTaskDialogListener {
    private static final String API_URL = "http://IP MÁQUINA VIRTUAL/index.php";
    private RequestQueue requestQueue;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private EditText editTextTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        editTextTitle = findViewById(R.id.editTextTitle);
        ListView listViewTasks = findViewById(R.id.listViewTasks);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, R.layout.item_task, taskList);
        listViewTasks.setAdapter(taskAdapter);
        listViewTasks.setClickable(true);


        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Task task = taskList.get(position);
                loadTask(position);
                //showEditTaskDialog(taskUpdate);

            }

        });
        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = taskList.get(position);
                deleteTask(task.getId());
                return true;
            }
        });

        loadTasks();
    }




    private void loadTasks() {
        JsonArrayRequest request = new JsonArrayRequest(API_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        taskList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject taskObject = response.getJSONObject(i);
                                int id = taskObject.getInt("id");
                                String title = taskObject.getString("title");
                                int completed = taskObject.getInt("completed");
                                Task task = new Task(id, title, completed);
                                taskList.add(task);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        taskAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(request);
    }

    private void loadTask(int id) {
        JsonArrayRequest request = new JsonArrayRequest(API_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                            try {
                                JSONObject taskObject = response.getJSONObject(id);
                                int id = taskObject.getInt("id");
                                String title = taskObject.getString("title");
                                int completed = taskObject.getInt("completed");
                                Task task = new Task(id, title, completed);
                                showEditTaskDialog(task);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(request);

    }

    public void onAddTaskClick(View view) {
        String title = editTextTitle.getText().toString().trim();

        if (!title.isEmpty()) {
            createTask(title);
            editTextTitle.setText("");
        } else {
            Toast.makeText(this, "Ingrese una tarea", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshTasks() {
        JsonArrayRequest request = new JsonArrayRequest(API_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        taskList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject taskObject = response.getJSONObject(i);
                                int id = taskObject.getInt("id");
                                String title = taskObject.getString("title");
                                int completed = taskObject.getInt("completed");
                                Task task = new Task(id, title, completed);
                                taskList.add(task);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        taskAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error cargando tareas", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(request);
    }
    private void createTask(String title) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("title", title);
            requestData.put("completed", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Tarea agregada", Toast.LENGTH_SHORT).show();
                        refreshTasks();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error agregando tarea", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(request);
    }

    private void updateTask(Task task) {
        String url = API_URL + "?id=" + task.getId();

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("title", task.getTitle());
            requestData.put("completed", task.isCompleted());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MainActivity.this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        refreshTasks();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error actualizando tarea", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(request);
    }

    private void deleteTask(int taskId) {
        String url = API_URL + "?id=" + taskId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error eliminando tarea", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        requestQueue.add(request);
    }

    private void showEditTaskDialog(Task task) {
        try{
            Bundle args = new Bundle();
            args.putSerializable("task",task);
            EditTaskDialog dialog = EditTaskDialog.newInstance(task);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "edit_task_dialog");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onTaskUpdated(Task task) {
        updateTask(task);
    }

}
