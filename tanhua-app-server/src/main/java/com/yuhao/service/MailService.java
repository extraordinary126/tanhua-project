package com.yuhao.service;


public interface MailService {
    void sendSimpleMail(String from, String to, String subject, String text);
}
