package com.jimmy.swiftwheels.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndSchemaName(String username, String schemaName);

    boolean existsByUsernameAndSchemaName(String username, String schemaName);
}
