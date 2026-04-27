package repository.user;

import enums.UserType;
import model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);
    User findById(long id);
    User findByLogin(String login);
    List<User> findByType(UserType type);
    List<User> findAll();
    void delete(long id);
}
