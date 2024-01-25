package sandclub.beeradvisor.source;

public abstract class BaseBeerRemoteDataSource {
    protected BeerCallback beerCallback;

    public void setBeerCallback(BeerCallback beerCallback) {
        this.beerCallback = beerCallback;
    }

    public abstract void getBeer(); //Ottiene birre da back-end
}
