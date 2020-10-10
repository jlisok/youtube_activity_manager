package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;

import java.util.List;

public interface SubscriptionService {

    List<String> retrieveChannelId(List<Subscription> subscriptionList);

}
