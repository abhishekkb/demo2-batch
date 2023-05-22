package com.example.demo2batch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
