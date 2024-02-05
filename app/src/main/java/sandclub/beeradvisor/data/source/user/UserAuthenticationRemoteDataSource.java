package sandclub.beeradvisor.data.source.user;

import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;
import static sandclub.beeradvisor.util.Constants.UNEXPECTED_ERROR;
import static sandclub.beeradvisor.util.Constants.USER_COLLISION_ERROR;
import static sandclub.beeradvisor.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import sandclub.beeradvisor.model.User;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource{

        private static final String TAG = UserAuthenticationRemoteDataSource.class.getSimpleName();
        private final FirebaseAuth firebaseAuth;
        private UserDataRemoteDataSource userDataRemoteDataSource;
        public UserAuthenticationRemoteDataSource() {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        @Override
        public User getLoggedUser() {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser == null){
                return null;
            }else{
                return new User(firebaseUser.getUid(), firebaseUser.getEmail());
            }
        }

        @Override
        public void logout() {
            FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        firebaseAuth.removeAuthStateListener(this);
                        Log.d(TAG, "User logged out");
                        userResponseCallback.onSuccessLogout();
                    }
                }
            };
            firebaseAuth.addAuthStateListener(authStateListener);
            firebaseAuth.signOut();
        }

        @Override
        public void signUp(String nome, String cognome, String email, String password) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        userResponseCallback.onSuccessFromAuthentication(
                                new User(firebaseUser.getUid(), nome, cognome, email, password,"", "", null )
                        );
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }

        @Override
        public void signIn(String email, String password) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {

                        userResponseCallback.onSuccessFromAuthentication(
                                new User(firebaseUser.getUid(), email, password)
                        );
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }

        @Override
        public void signInWithGoogle(String idToken) {
            if (idToken !=  null) {
                // Got an ID token from Google. Use it to authenticate with Firebase.
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String[] parts = firebaseUser.getDisplayName().split(" ");
                            String nome = parts[0];  // Il primo elemento è il nome
                            String cognome = parts.length > 1 ? parts[1] : "";  // Il secondo elemento è il cognome, se presente

                            userResponseCallback.onSuccessFromAuthentication(
                                    new User(firebaseUser.getUid(),
                                    nome, cognome,
                                    firebaseUser.getEmail(),
                                    "",
                                    "",
                                    firebaseUser.getPhotoUrl().toString(),
                                            null));
                        } else {
                            userResponseCallback.onFailureFromAuthentication(
                                    getErrorMessage(task.getException()));
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                });
            }
        }

        //Gestore di errori
    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }
        return UNEXPECTED_ERROR;
    }
}
