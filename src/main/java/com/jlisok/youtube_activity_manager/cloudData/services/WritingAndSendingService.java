package com.jlisok.youtube_activity_manager.cloudData.services;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface WritingAndSendingService<T> {

    void writeAndSendData(T data) throws JsonProcessingException;
}
