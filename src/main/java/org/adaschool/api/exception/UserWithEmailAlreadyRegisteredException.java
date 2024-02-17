package org.adaschool.api.exception;

public class UserWithEmailAlreadyRegisteredException extends ServerErrorException {
    public UserWithEmailAlreadyRegisteredException(String email) {
        super("User with email already registered");
    }
}
