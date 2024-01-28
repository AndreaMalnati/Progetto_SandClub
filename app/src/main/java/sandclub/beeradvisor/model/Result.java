package sandclub.beeradvisor.model;

public class Result {
    private Result() {}

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isSuccessUser() {
        return this instanceof UserResponseSuccess;
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class Success extends Result {
        private final BeerResponse beerResponse;
        public Success(BeerResponse newsResponse) {
            this.beerResponse = newsResponse;
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
    /**
     * Class that represents an error occurred during the interaction
     * with a Web Service or a local database.
     */
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