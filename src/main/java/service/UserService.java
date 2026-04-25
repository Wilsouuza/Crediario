package service;

import enums.UserType;
import model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    User login(String login, String password);
    void createUser(String login, String password, UserType userType);
    void updatePassword(String login, String newPassword);
    void deleteUser(String login);
    List<User> findAll();
    List<User> findByType(UserType userType);
    void addCommission(long id, BigDecimal amount);
}
