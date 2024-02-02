package sandclub.beeradvisor.model;

public class Comment {
    private int id;
    private int idBeer;
    private String nameUser;
    private float rating;
    private String comment;

    public Comment(int id, int idBeer, String nameUser, float rating, String comment) {
        this.id = id;
        this.idBeer = idBeer;
        this.nameUser = nameUser;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
