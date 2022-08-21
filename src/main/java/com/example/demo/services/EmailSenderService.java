package com.example.demo.services;

public interface EmailSenderService {

    void send(String to, String subject, String email);
}
