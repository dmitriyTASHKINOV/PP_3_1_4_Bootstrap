package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UsersRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User findByUsername(String username){
        return usersRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUsers() {
        return usersRepository.findAll();
    }

    @Transactional
    @Override
    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    public User findById(Long id){
        return usersRepository.getById(id);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (usersRepository.findById(id).isPresent()) usersRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }
    @PostConstruct
    @Transactional
    public void initializeAdminUser() {
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role("USER");
            roleRepository.save(userRole);
        }

        Role adminRole = roleRepository.findByName("ADMIN");
        if (adminRole == null) {
            adminRole = new Role("ADMIN");
            roleRepository.save(adminRole);
        }

        String encodedPassword = passwordEncoder.encode("admin");
        User adminUser = new User("admin", encodedPassword, "admin@example.com", 30, Arrays.asList(userRole, adminRole));

        usersRepository.save(adminUser);
    }
}
