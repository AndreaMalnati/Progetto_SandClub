package sandclub.beeradvisor.data.source.user;

import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.data.repository.user.UserResponseCallback;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String nome, String cognome, String email, String password);
    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);
}
