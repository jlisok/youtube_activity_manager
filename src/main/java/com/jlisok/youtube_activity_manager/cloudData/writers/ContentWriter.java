package com.jlisok.youtube_activity_manager.cloudData.writers;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ContentWriter<T> {

    String writeContent(T content) throws JsonProcessingException;

}
