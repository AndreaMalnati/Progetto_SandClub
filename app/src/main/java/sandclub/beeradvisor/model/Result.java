package sandclub.beeradvisor.model;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isSuccessUser() {
        return this instanceof UserResponseSuccess;
    }

    public boolean isSuccessComment() {
        return this instanceof CommentResponseSuccess;
    }



    public static final class Success extends Result {
        private final BeerResponse beerResponse;
        public Success(BeerResponse beerResponse) {
            this.beerResponse = beerResponse;
        }
        public BeerResponse getData() {
            return beerResponse;
        }
    }


    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }

    public static final class CommentResponseSuccess extends Result {
        private final CommentResponse commentResponse;
        public CommentResponseSuccess(CommentResponse commentResponse) {
            this.commentResponse = commentResponse;
        }
        public CommentResponse getData() {
            return commentResponse;
        }
    }

    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
