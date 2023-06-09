package com.example.listatareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText et_username, et_password, et_confirmar;
    Button btn_crearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        et_username = (EditText) findViewById(R.id.et_registro_username);
        et_password = (EditText) findViewById(R.id.et_registro_password);
        et_confirmar = (EditText) findViewById(R.id.et_registro_confirmar);
        btn_crearCuenta = (Button) findViewById(R.id.btn_crearCuenta);

        btn_crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearCuenta();
            }
        });
    }

    private void CrearCuenta(){
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmPassword = et_confirmar.getText().toString().trim();

        // Realizar la validación de los campos
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Ingrese un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Confirme la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "La contraseña y la confirmación no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("usuarios").document(username);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documento = task.getResult();
                    if (documento != null && documento.exists()){
                        Toast.makeText(Registro.this, "El nombre de usuario ya está registrado", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Map<String, Object> usuario = new HashMap<>();
                        usuario.put("username", username);
                        usuario.put("password", password);

                        db.collection("usuarios").document(username)
                                .set(usuario)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                }
            }
        });
    }
}