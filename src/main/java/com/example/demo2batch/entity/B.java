package com.example.demo2batch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "collectionB")
public class B {
    @Id
    private String id;
    private String corrId;
}
