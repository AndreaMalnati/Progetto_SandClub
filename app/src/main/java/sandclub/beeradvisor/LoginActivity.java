package sandclub.beeradvisor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView textView;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.emailLg);
        editTextPassword = findViewById(R.id.passwordLg);
        buttonLogin = findViewById(R.id.confirmLoginBtn);

    buttonLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Email, Password;
            Email = String.valueOf(editTextEmail.getText());
            Password = String.valueOf(editTextPassword.getText());

            if(TextUtils.isEmpty(Email) || isValidEmail(Email) == false){
                Toast.makeText(LoginActivity.this, "Email non valida", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(Password)){
                Toast.makeText(LoginActivity.this, "Inserisci Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Autenticazione Completata", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish(); //chiude loginActivity e apre la mainActivity


                            } else {

                                Toast.makeText(LoginActivity.this, "Autenticazione Fallita",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    });

    }
    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}
