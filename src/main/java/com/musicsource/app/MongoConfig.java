package com.musicsource.app;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongo(@Value("${mongodb.connection.string}") String mongoDbConnectionString) {
        ConnectionString connectionString = new ConnectionString(mongoDbConnectionString);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate(@Value("${mongodb.connection.string}") String mongoDbConnectionString) {
        return new MongoTemplate(mongo(mongoDbConnectionString), "music_source");
    }
}
