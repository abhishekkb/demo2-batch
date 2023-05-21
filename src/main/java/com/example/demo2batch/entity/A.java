package com.example.demo2batch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "collectionA")
public class A {
    @Id
    private String id;
    private String corrId;
    private String name;
    private String address;
    private String email;
    private String phone;
    private Date createdTimestamp;
}
