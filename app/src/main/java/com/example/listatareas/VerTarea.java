package com.example.listatareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class VerTarea extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idTarea;

    TextView tv_creacion;
    EditText et_titulo, et_descripcion;
    Button btn_actualizar, btn_realizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_tarea);

        tv_creacion = (TextView) findViewById(R.id.tv_creacion);
        et_titulo = (EditText) findViewById(R.id.et_titulo2);
        et_descripcion = (EditText) findViewById(R.id.et_descripcion2);
        btn_actualizar = (Button) findViewById(R.id.btn_actualizar);
        btn_realizar = (Button) findViewById(R.id.btn_realizar);

        Intent intent = getIntent();
        idTarea = intent.getStringExtra("idTarea");

        db.collection("usuarios")
                .document(Usuario.username)
                .collection("tareas")
                .document(idTarea)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                tv_creacion.setText("Tarea creada el " +
                                                document.getString("creacion_fecha") +
                                                " a las " +
                                                document.getString("creacion_hora")
                                        );
                                et_titulo.setText(document.getString("titulo"));
                                et_descripcion.setText(document.getString("descripcion"));
                            }
                        }
                        else{
                            Toast.makeText(VerTarea.this, "Hubo un error al cargar la tarea", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}