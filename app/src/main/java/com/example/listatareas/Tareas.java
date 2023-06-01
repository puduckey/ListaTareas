package com.example.listatareas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Tareas extends AppCompatActivity {
    FirebaseFirestore db;
    TextView tv_bienvenida;
    Button btn_agregarTarea;
    ListView lv_listaTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        db = FirebaseFirestore.getInstance();

        tv_bienvenida = (TextView) findViewById(R.id.tv_bienvenida);
        btn_agregarTarea = (Button) findViewById(R.id.btn_agregarTarea);
        lv_listaTareas = (ListView) findViewById(R.id.lv_listaTareas);

        tv_bienvenida.setText("Bienvenido/a " + Usuario.username);
        cargarLista();

        btn_agregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tareas.this, AgregarTarea.class);
                launcher.launch(i);
            }
        });
    }

    private void cargarLista(){
        cargarListaTareas(new OnListaCargadaListener() {
            @Override
            public void onListaCargada(ArrayList<String> lista) {
                ArrayList<String> arrTareas = new ArrayList<String>();
                arrTareas = lista;
                ArrayAdapter adapter = new ArrayAdapter(Tareas.this, android.R.layout.simple_list_item_1, arrTareas);

                lv_listaTareas.setAdapter(adapter);
            }
        });


    }

    private void cargarListaTareas(final OnListaCargadaListener listener){
        ArrayList<String> lista = new ArrayList<>();

        db.collection("usuarios")
                .document(Usuario.username)
                .collection("tareas")
                .whereEqualTo("estado", "pendiente") // Obtiene solo las tareas marcadas como pendiente
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String line = document.getString("titulo");
                                lista.add(line);
                                listener.onListaCargada(lista);
                            }

                        }else{
                            Toast.makeText(Tareas.this,"Hubo un error al obtener las tareas", Toast.LENGTH_SHORT).show();
                            listener.onListaCargada(null);
                        }
                    }
                });
    }

    public interface OnListaCargadaListener {
        void onListaCargada(ArrayList<String> lista);
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // volver a cargar los datos desde Firebase
                    cargarLista();
                }
            }
    );
}