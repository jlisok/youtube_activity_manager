package com.jlisok.youtube_activity_manager.cloudData.writers;

public interface ContentWriter<T> {

    String writeContent(T content);

}
