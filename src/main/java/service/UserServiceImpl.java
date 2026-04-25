package service;

import enums.UserType;
import exception.BusinessException;
import model.User;
import repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User login(String login, String password) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new BusinessException("Login not found!");
        }
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("Incorrect password");
        }
        return user;
    }

    public void createUser(String login, String password, UserType userType){
        User user = userRepository.findByLogin(login);
        if (login == null || login.isBlank()){
            throw new BusinessException("The login cannot be empty!");
        }
        if (password == null || password.isBlank()){
            throw new BusinessException("The password cannot be empty!");
        }
        if (user != null){
            throw new BusinessException("Login already exists");
        }


        User newUser = new User(login,password,userType);
        userRepository.save(newUser);
    }

    public void updatePassword(String login, String newPassword){
        User user = userRepository.findByLogin(login);
        if (user == null){
            throw new BusinessException("Login not found");
        }
        if (newPassword == null || newPassword.isBlank()){
            throw new BusinessException("The password cannot be empty!");
        }
        user.setPassword(newPassword);
    }

    public void deleteUser(String login){
        User user = userRepository.findByLogin(login);
        if (user == null){
            throw new BusinessException("Login not found");
        }
        if (user.getUserType() == UserType.ADMIN) {
            throw new BusinessException("Admin users cannot be deleted");
        }
        if (user.getUserType() == UserType.CUSTOMER) {
            throw new BusinessException("Customer users cannot be deleted");
        }
        userRepository.delete(user.getId());
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findByType(UserType userType){
        return userRepository.findByType(userType);
    }

    public void addCommission(long id, BigDecimal amount){
        User user = userRepository.findById(id);
        if (user == null){
            throw new BusinessException("User not found!");
        }
        user.setCommission(user.getCommission().add(amount));
    }
}

