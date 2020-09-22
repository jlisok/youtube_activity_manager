package com.jlisok.youtube_activity_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YoutubeActivityManagerApplication {

    public static void main(String[] args) {

        System.getenv().forEach((asd, adsa) ->
                System.out.println(asd + ": " + adsa)
        );

        SpringApplication.run(YoutubeActivityManagerApplication.class, args);
    }

}
