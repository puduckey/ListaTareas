package com.example.listatareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
                                tv_creacion.setText("Última modificación: " +
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

        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> actualizacion = new HashMap<>();
                actualizacion.put("titulo", et_titulo.getText().toString());
                actualizacion.put("descripcion", et_descripcion.getText().toString());

                // Obtener el dia formateado
                Calendar calendario = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
                String fechaFormateada = dateFormat.format(calendario.getTime());

                // Obtener la hora formateada
                SimpleDateFormat formato = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String horaFormateada = formato.format(new Date());

                actualizacion.put("creacion_fecha", fechaFormateada);
                actualizacion.put("creacion_hora", horaFormateada);

                db.collection("usuarios")
                        .document(Usuario.username)
                        .collection("tareas")
                        .document(idTarea)
                        .update(actualizacion)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(VerTarea.this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(VerTarea.this, "Hubo un error al actualizar la tarea", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btn_realizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> actualizacion = new HashMap<>();
                actualizacion.put("estado", "realizada");
                db.collection("usuarios")
                        .document(Usuario.username)
                        .collection("tareas")
                        .document(idTarea)
                        .update(actualizacion)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(VerTarea.this, "Tarea realizada", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(VerTarea.this, "Hubo un error al actualizar la tarea", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}