package org.adaschool.api.exception;

public class UserWithEmailAlreadyRegisteredException extends ServerErrorException {
    public UserWithEmailAlreadyRegisteredException() {
        super("User with email already registered");
    }
}
