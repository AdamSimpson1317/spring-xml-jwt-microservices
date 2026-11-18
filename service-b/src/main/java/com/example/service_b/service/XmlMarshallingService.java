package com.example.service_b.service;

import com.example.service_b.exception.MessageMarshallingException;
import com.example.service_b.model.Message;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class XmlMarshallingService {

    public String marshal(Message message){

        if (message == null) {
            throw new MessageMarshallingException(
                    "Message cannot be null",
                    new IllegalArgumentException("Message cannot be null")
            );
        }

        try{
            JAXBContext context = JAXBContext.newInstance(Message.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(message, writer);
            return writer.toString();
        } catch (JAXBException exception){
            throw new MessageMarshallingException("Failed to marshal message to XML", exception);
        }
    }
}
