package com.example.a2048;

public class User
{
    String login;
    String password;
    byte[] photo;
    int bestScore;

    public User(String login, byte[] photo, int bestScore)
    {
        this.login = login;
        this.photo = photo;
        this.bestScore = bestScore;
    }
}
