package service;

import enums.UserType;
import model.User;
import repository.UserRepository;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User login(String login, String password) {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Login not found!");
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        return user;
    }

    public void createUser(String login, String password, UserType userType){
        User user = userRepository.findByLogin(login);
        if (user != null){
            throw new RuntimeException("Login already exists");
        }
        if (login == null || login.isBlank()){
            throw new RuntimeException("The login cannot be empty!");
        }
        if (password == null || password.isBlank()){
            throw new RuntimeException("The password cannot be empty!");
        }
        User newUser = new User(login,password,userType);
        userRepository.save(newUser);
    }

    /*public void updatePassword(String login, String newPassword){
        User user = userRepository.findByLogin(login);
        if (user == null){
            throw new RuntimeException("Login not found")
        }
    }*/
}

