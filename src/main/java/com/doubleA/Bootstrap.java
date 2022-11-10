package com.doubleA;

import com.doubleA.user.role.ERole;
import com.doubleA.user.role.Role;
import com.doubleA.user.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap {

    @Bean
    CommandLineRunner runner(RoleRepository roleRepository) {
        roleRepository.deleteAll();

        return args -> {
            Role role1 = new Role(ERole.ROLE_USER);
            Role role2 = new Role(ERole.ROLE_MODERATOR);
            Role role3 = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(role1);
            roleRepository.save(role2);
            roleRepository.save(role3);
        };
    }

}
