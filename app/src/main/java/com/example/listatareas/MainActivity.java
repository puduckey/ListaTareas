package com.example.listatareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    EditText et_username, et_password;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_iniciarSesion);

        db = FirebaseFirestore.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                db.collection("usuarios")
                        .document(username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String passwordDocument = document.getString("password");

                                        if (passwordDocument != null && passwordDocument.equals(password)){
                                            Toast.makeText(MainActivity.this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();

                                            // guardar el nombre de usuario en una variable estatica
                                            Usuario.username = document.getString("username");

                                            // Redirigir a la actividad "Tareas"
                                            Intent intent = new Intent(MainActivity.this, Tareas.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "ERROR: Revisa tu conexion", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}