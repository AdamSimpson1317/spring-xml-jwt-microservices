package com.example.service_a.service;

import com.example.service_a.exception.InvalidMessageException;
import com.example.service_a.exception.MalformedXmlException;
import com.example.service_a.model.Message;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;

public class XmlUnmarshallingService {

    public Message unmarshal(String xml){
        try {

            JAXBContext context = JAXBContext.newInstance(Message.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            Message message = (Message) unmarshaller.unmarshal(new StringReader(xml));

            if(message.getContent() == null || message.getContent().isBlank()){
                throw new InvalidMessageException("Message content is required");
            }

            return message;
        } catch(JAXBException exception){
            throw new MalformedXmlException("Failed to unmarshal XML", exception);
        }
    }
}
