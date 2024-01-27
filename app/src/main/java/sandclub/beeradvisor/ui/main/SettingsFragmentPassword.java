package sandclub.beeradvisor.ui.main;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;

public class SettingsFragmentPassword extends Fragment {

    public SettingsFragmentPassword() {
        // Required empty public constructor
    }

    public static SettingsFragmentPassword newInstance() {
        SettingsFragmentPassword fragment = new SettingsFragmentPassword();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button confirmChangePw = view.findViewById(R.id.confirmChangePasswordBtn);
        User user = new User (".", ".", ".", ".", ".", ".", ".");//UserViewModel.getInstance().getUser();

        TextInputEditText oldPw = view.findViewById(R.id.oldPw);
        TextInputEditText newPw = view.findViewById(R.id.newPw);
        TextInputEditText repeatPw = view.findViewById(R.id.repeatPw);
        String userId = user.getUserId();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("user").child(userId);

        confirmChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference.addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String passwordDatabase = dataSnapshot.child("password").getValue(String.class);
                        String password = oldPw.getText().toString();

                        if(!passwordDatabase.equals("")) {
                            if (!password.equals(passwordDatabase)) {
                                Snackbar.make(view, "Password vecchia errata: " + password + " deve essere " + passwordDatabase, Snackbar.LENGTH_SHORT).show();
                            }else {
                                if (newPw.getText().toString().equals(repeatPw.getText().toString())) {
                                    databaseReference.child("password").setValue(newPw.getText().toString());
                                    User updateUser = new User (".", ".", ".", ".", ".", ".", ".");//UserViewModel.getInstance().getUser();
                                    updateUser.setPassword(newPw.getText().toString());
                                    //UserViewModel.getInstance().setUser(updateUser);
                                    user.updatePassword(newPw.getText().toString());

                                } else {
                                    Snackbar.make(view, getResources().getString(R.string.wrong_password), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Snackbar.make(view, getResources().getString(R.string.already_google_login), Snackbar.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Gestisci l'errore
                    }
                });
            }
        });

    }
}