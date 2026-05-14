package com.smartCity.complaintSystem.service;

import com.smartCity.complaintSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private WorkerRepository workerRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var user = userRepo.findByEmail(email);
        if (user.isPresent()) {
            return new User(
                user.get().getEmail(),
                user.get().getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }


        var admin = adminRepo.findByEmail(email);
        if (admin.isPresent()) {
            return new User(
                admin.get().getEmail(),
                admin.get().getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

     
        var worker = workerRepo.findByEmail(email);
        if (worker.isPresent()) {
            return new User(
                worker.get().getEmail(),
                worker.get().getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_WORKER"))
            );
        }

        throw new UsernameNotFoundException("User not found");
    }
}