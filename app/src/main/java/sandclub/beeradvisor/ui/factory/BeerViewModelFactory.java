package sandclub.beeradvisor.ui.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.repository.IBeerRepositoryWithLiveData;

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
