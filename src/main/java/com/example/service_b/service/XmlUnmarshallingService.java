package com.example.service_b.service;

import com.example.service_b.model.Message;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class XmlUnmarshallingService {

    public Message unmarshal(String xml)
    {
        try {
            JAXBContext context = JAXBContext.newInstance(Message.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            return (Message) unmarshaller.unmarshal(new StringReader(xml));

        } catch (JAXBException exception){
            throw new RuntimeException("Failed to unmarshal XML", exception);
        }
    }
}
