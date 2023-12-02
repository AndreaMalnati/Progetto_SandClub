package sandclub.beeradvisor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sandclub.beeradvisor.model.User;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextPassword, editTextPassword2;
    Button btnConfirmRegister;
    FirebaseAuth mAuth;
    String databaseUrl = "https://progetto-sandclub-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        editTextNome = findViewById(R.id.nameRg);
        editTextCognome = findViewById(R.id.surnameRg);
        editTextEmail = findViewById(R.id.emailRg);
        editTextPassword = findViewById(R.id.passwordRg);
        editTextPassword2 = findViewById(R.id.password2Rg);
        btnConfirmRegister = findViewById(R.id.Confirm_Registration);

        mDatabase = FirebaseDatabase.getInstance(databaseUrl).getReference();

        btnConfirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Nome, Cognome, Email, Password, Password2;
                Nome = String.valueOf(editTextNome.getText());
                Cognome = String.valueOf(editTextCognome.getText());
                Email = String.valueOf(editTextEmail.getText());
                Password = String.valueOf(editTextPassword.getText());
                Password2 = String.valueOf(editTextPassword2.getText());



            //CONTROLLI campi vuoti e/o campi non corretti
                if(TextUtils.isEmpty(Nome)){
                    Toast.makeText(RegisterActivity.this, "Inserisci nome", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Cognome)){
                    Toast.makeText(RegisterActivity.this, "Inserisci cognome", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check email corretta
                if(TextUtils.isEmpty(Email) || isValidEmail(Email) == false){
                    Toast.makeText(RegisterActivity.this, "Email non valida", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password)){
                    Toast.makeText(RegisterActivity.this, "Inserisci Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password2)){
                    Toast.makeText(RegisterActivity.this, "Inserisci Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check se ripeti password uguale a password
                if(Password.equals(Password2) == false){
                    Toast.makeText(RegisterActivity.this, "Password non valida", Toast.LENGTH_SHORT).show();
                    return;
                }

                //CREAZIONE UTENTE
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registrazione Completata.",
                                            Toast.LENGTH_SHORT).show();
                                            writeNewUser();

                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    String errorMessage = "Authentication fallita";
                                    if (task.getException() != null) {
                                        errorMessage += " " + task.getException().getMessage();
                                    }
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });



            }


        });

    }

    public void sendData(View view){
        writeNewUser();
    }

    public void writeNewUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            User newUser = new User(userId, editTextNome.getText().toString(),
                    editTextCognome.getText().toString(),
                    editTextEmail.getText().toString(),
                    editTextPassword.getText().toString());

            mDatabase.child("user").child(User.getUserId()).setValue(newUser);
        }
    }
    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }


}