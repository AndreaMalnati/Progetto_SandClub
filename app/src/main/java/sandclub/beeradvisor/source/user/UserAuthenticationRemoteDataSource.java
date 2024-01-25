package sandclub.beeradvisor.source.user;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;
import static sandclub.beeradvisor.util.Constants.UNEXPECTED_ERROR;
import static sandclub.beeradvisor.util.Constants.USER_COLLISION_ERROR;
import static sandclub.beeradvisor.util.Constants.WEAK_PASSWORD_ERROR;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import sandclub.beeradvisor.model.User;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource{

        private static final String TAG = UserAuthenticationRemoteDataSource.class.getSimpleName();
        private final FirebaseAuth firebaseAuth;
        public UserAuthenticationRemoteDataSource() {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        @Override
        public User getLoggedUser() {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser == null){
                return null;
            }else{//TODO: Fare funzione per ottenere i dati dell'utente da firebase in file UserDataRemoteDataSource.java
                return new User();
            }
        }

        @Override
        public void logout() {

        }

        @Override
        public void signUp(String email, String password) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        userResponseCallback.onSuccessFromAuthentication(
                                new User() //TODO: Fare funzione per ottenere i dati dell'utente da firebase in file UserDataRemoteDataSource.java
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
                                new User() //TODO: Fare funzione per ottenere i dati dell'utente da firebase in file UserDataRemoteDataSource.java
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
