package sandclub.beeradvisor.model;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;


public class User{

    String userId;
    String nome;
    String cognome;
    String email;
    String password;
    String photoUrl;
    String photoUrlGoogle;
    List<Beer> birrePreferite;
    HashMap<Beer, String> birreBevute;

    public String getPhotoUrlGoogle() {
        return photoUrlGoogle;
    }

    public void setPhotoUrlGoogle(String photoUrlGoogle) {
        this.photoUrlGoogle = photoUrlGoogle;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public User() {
    }



    //User google
    public User(String userId, String nome, String cognome, String email, String password, String photoUrl, String photoUrlGoogle) {
        this.userId = userId;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
        this.photoUrlGoogle = photoUrlGoogle;

    }
    //User normale
    public User(String userId, String nome, String cognome, String email, String password) {
        this.userId = userId;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        photoUrl = "";
    }

    //Costruttore per login
    public User(String userId, String email, String password){
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    //Costruttore per getLoggedUser
    public User(String userId, String email){
        this.userId = userId;
        this.email = email;
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

    public  String getUserId() {
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
