package com.example.service_a.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
public class Message {

    private String content;

    public Message(){}

    @XmlElement(name="content")
    public String getContent(){return content;}

    public void setContent(String content){this.content = content;}
}
