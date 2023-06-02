package com.example.listatareas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    ArrayList<Tarea> lista;

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

    private void cargarLista() {
        cargarListaTareas(new OnListaCargadaListener() {
            @Override
            public void onListaCargada(ArrayList<Tarea> lista) {
                if (lista != null) {
                    ArrayList<String> arrTareas = new ArrayList<String>();
                    for (Tarea tarea : lista) {
                        arrTareas.add(tarea.getTitulo());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Tareas.this, android.R.layout.simple_list_item_1, arrTareas);

                    lv_listaTareas.setAdapter(adapter);
                    lv_listaTareas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Tarea tarea = lista.get(position);
                            String idTarea = tarea.getId();

                            Intent intent = new Intent(Tareas.this, VerTarea.class);
                            intent.putExtra("idTarea", idTarea);
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(Tareas.this, "Hubo un error al obtener las tareas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void cargarListaTareas(final OnListaCargadaListener listener){
        lista = new ArrayList<>();

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
                                String id = document.getId();
                                String titulo = document.getString("titulo");
                                Tarea tarea = new Tarea(id, titulo);
                                lista.add(tarea);
                            }
                            listener.onListaCargada(lista);
                        } else {
                            Toast.makeText(Tareas.this, "Hubo un error al obtener las tareas", Toast.LENGTH_SHORT).show();
                            listener.onListaCargada(null);
                        }
                    }
                });
    }

    public interface OnListaCargadaListener {
        void onListaCargada(ArrayList<Tarea> lista);
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
