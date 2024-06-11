package passwordhasher;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {
    public static String password_hash(String password) {
        String salt = BCrypt.gensalt();

        return BCrypt.hashpw(password, salt);
    }

    public static boolean password_verify(String password, String hashedPassowrd) {
        return BCrypt.checkpw(password, hashedPassowrd);
    }
}
