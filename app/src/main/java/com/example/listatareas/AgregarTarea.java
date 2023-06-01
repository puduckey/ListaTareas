package com.example.listatareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgregarTarea extends AppCompatActivity {
    FirebaseFirestore db;
    EditText et_tituloAgregar, et_descripcionAgregar;
    Button btn_btn_agregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarea);

        db = FirebaseFirestore.getInstance();

        et_tituloAgregar = (EditText) findViewById(R.id.et_tituloAgregar);
        et_descripcionAgregar = (EditText) findViewById(R.id.et_descripcionAgregar);
        btn_btn_agregar = (Button) findViewById(R.id.btn_agregar);

        btn_btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = et_tituloAgregar.getText().toString();
                String descripcion = et_descripcionAgregar.getText().toString();

                if (titulo.isEmpty()){
                    Toast.makeText(getApplicationContext(), "ERROR: La tarea debe al menos tener un titulo", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (descripcion.isEmpty()){
                    descripcion = "";
                }

                // Obtener el dia formateado
                Calendar calendario = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
                String fechaFormateada = dateFormat.format(calendario.getTime());

                // Obtener la hora formateada
                SimpleDateFormat formato = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String horaFormateada = formato.format(new Date());

                RegistrarTarea(titulo, descripcion, "pendiente", fechaFormateada, horaFormateada);
            }
        });
    }

    private void RegistrarTarea(String titulo, String descripcion, String estado, String fecha, String hora){
        Map<String, Object> tarea = new HashMap<>();
        tarea.put("titulo", titulo);
        tarea.put("descripcion", descripcion);
        tarea.put("estado", estado);
        tarea.put("creacion_fecha", fecha);
        tarea.put("creacion_hora", hora);

        db.collection("usuarios")
                .document(Usuario.username)
                .collection("tareas")
                .add(tarea)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Tarea registrada", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Hubo un error de registro", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}