package com.jlisok.youtube_activity_manager.cloudData.services;

import java.util.UUID;

public interface WritingAndSendingService<T> {

    void writeAndSendData(T data, UUID userId);
}
