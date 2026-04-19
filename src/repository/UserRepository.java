package repository;

import enums.UserType;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private List<User> users = new ArrayList<>();
    private long nextId = 1;

    public void save(User user){
        user.setId(nextId++);
        users.add(user);
    }

    public User findById(long id){
        for (User u : users){
            if(u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public User findByLogin(String login){
        for (User u : users){
            if (login.equals(u.getLogin())){
                return u;
            }
        }
        return null;
    }

    public List<User> findByType(UserType type){
        List<User> result = new ArrayList<>();
        for (User u : users){
            if (u.getUserType() == type){
                result.add(u);
            }
        }
        return result;
    }

    public List<User> findAll(){
        return new ArrayList<>(users);
    }

    public void delete(long id){
        User user = findById(id);
        if (user != null){
            users.remove(user);
        }
    }

}