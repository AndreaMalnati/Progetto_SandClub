package sandclub.beeradvisor.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import sandclub.beeradvisor.ui.main.BeerViewModel;
import sandclub.beeradvisor.data.repository.beer.IBeerRepositoryWithLiveData;

public class BeerViewModelFactory implements ViewModelProvider.Factory{
    private final IBeerRepositoryWithLiveData iBeerRepositoryWithLiveData;

    public BeerViewModelFactory(IBeerRepositoryWithLiveData iBeerRepositoryWithLiveData) {
        this.iBeerRepositoryWithLiveData = iBeerRepositoryWithLiveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BeerViewModel(iBeerRepositoryWithLiveData);
    }

}
