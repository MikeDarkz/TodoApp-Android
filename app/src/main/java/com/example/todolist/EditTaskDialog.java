package com.example.todolist;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class EditTaskDialog extends AppCompatDialogFragment {
    private EditText editTextTaskTitle;
    private CheckBox checkBoxCompleted;
    private EditTaskDialogListener listener;
    private Task task;

    public static EditTaskDialog newInstance(Task task) {
        EditTaskDialog dialog = new EditTaskDialog();
        Bundle args = new Bundle();
        args.getSerializable("task");
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditTaskDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Si esto sale es porqué algo está muy mal");
        }
    }

    @NonNull
    @SuppressLint("MissingInflatedId")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_task_dialog, null);

        editTextTaskTitle = view.findViewById(R.id.editTextTitle);
        checkBoxCompleted = view.findViewById(R.id.checkBox);

        task = (Task) getArguments().getSerializable("task");
        if (task != null) {
            editTextTaskTitle.setText(task.getTitle());
            checkBoxCompleted.setChecked(task.isCompleted());
        }
        try{
            builder.setView(view)
                    .setTitle("Editar tarea")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String title = editTextTaskTitle.getText().toString().trim();
                        boolean completed = checkBoxCompleted.isChecked();

                        if (task != null) {
                            task.setTitle(title);
                            task.setCompleted(completed);
                            listener.onTaskUpdated(task);

                        }

                    });
        }catch (Exception e){
            e.printStackTrace();
        }


        return builder.create();
    }

    public interface EditTaskDialogListener {
        void onTaskUpdated(Task task);

    }
}
