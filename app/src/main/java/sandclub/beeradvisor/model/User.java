package sandclub.beeradvisor.model;

import androidx.lifecycle.ViewModel;

public class User{

    static String userId;
    String nome;
    String cognome;
    String email;
    String password;
    String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public User() {
    }

    //User google
    public User(String userId, String nome, String cognome, String email, String password, String photoUrl) {
        this.userId = userId;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
    }
    //User normale
    public User(String userId, String nome, String cognome, String email, String password) {
        this.userId = userId;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static String getUserId() {
        return userId;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
