package org.adaschool.api.data.user;

import java.util.Optional;

public interface UserService {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(String id);

    UserEntity save(UserEntity user);

    void delete(UserEntity user);
    boolean validateUser(String email, String password);

}
