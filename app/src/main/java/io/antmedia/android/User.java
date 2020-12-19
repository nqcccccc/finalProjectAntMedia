package io.antmedia.android;

public class User {
    private String fullName ,email,avatar;

    public User(){}

    public User(String email, String fullName, String avatar) {
        this.fullName = fullName;
        this.email = email;
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
