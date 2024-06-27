package com.radynamics.xrplservermgr.sshapi;

import java.util.function.Supplier;

public class PasswordRequest {
    private char[] password;
    private char[] tempPassword;
    private final Supplier<char[]> getPassword;

    public PasswordRequest(char[] password) {
        this(() -> password);
        this.password = password;
    }

    public PasswordRequest(Supplier<char[]> getPassword) {
        this.getPassword = getPassword;
    }

    public char[] password() {
        if (password != null) {
            return password;
        }

        tempPassword = getPassword.get();
        return tempPassword;
    }

    public void passwordAccepted(boolean passwordAccepted) {
        password = passwordAccepted && tempPassword != null ? tempPassword : password;
        tempPassword = null;
    }

    public boolean hasPassword() {
        return password != null && password.length > 0;
    }
}
