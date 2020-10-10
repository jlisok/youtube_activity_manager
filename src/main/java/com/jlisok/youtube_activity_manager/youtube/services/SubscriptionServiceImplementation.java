package com.jlisok.youtube_activity_manager.youtube.services;

import com.google.api.services.youtube.model.Subscription;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImplementation implements SubscriptionService {

    @Override
    public List<String> retrieveChannelId(List<Subscription> subscriptionList) {
        return subscriptionList.stream()
                               .map(subscription -> subscription.getSnippet().getResourceId().getChannelId())
                               .collect(Collectors.toList());
    }
}
