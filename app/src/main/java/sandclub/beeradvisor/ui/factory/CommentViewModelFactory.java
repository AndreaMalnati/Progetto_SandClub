package sandclub.beeradvisor.ui.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import sandclub.beeradvisor.model.CommentViewModel;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.comment.ICommentRepository;

public class CommentViewModelFactory implements ViewModelProvider.Factory{
    private final ICommentRepository commentRepository;

    public CommentViewModelFactory(ICommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CommentViewModel(commentRepository);
    }
}
