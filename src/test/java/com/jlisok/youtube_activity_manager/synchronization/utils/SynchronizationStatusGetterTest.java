package com.jlisok.youtube_activity_manager.synchronization.utils;

import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SynchronizationStatusGetterTest implements TestProfile {

    @Autowired
    private SynchronizationStatusGetter getter;

    @MockBean
    private SynchronizationRepository repository;

    @Autowired
    private UserUtils utils;

    private User user;

    @BeforeEach
    void prepareInitialConditions() throws RegistrationException {
        user = utils.createUser(utils.createRandomEmail(), utils.createRandomPassword());
    }

    @ParameterizedTest
    @MethodSource("inputData")
    void getLastSynchronization(List<SynchronizationStatus> statuses) {
        // given

        when(repository.findByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(statuses);

        // when
        var actualStatus = getter.getLastSynchronization(user.getId());

        //then
        if (!statuses.isEmpty()) {
            Assertions.assertEquals(statuses.get(0), actualStatus);
        } else {
            Assertions.assertNull(actualStatus.getCreatedAt());
            Assertions.assertNull(actualStatus.getStatus());
        }
    }

    Stream<Arguments> inputData() {
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();
        return Stream.of(
                Arguments.arguments(List.of(new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now, user))),
                Arguments.arguments(List.of(new SynchronizationStatus(id, SynchronizationState.IN_PROGRESS, now, user))),
                Arguments.arguments(List.of(new SynchronizationStatus(id, SynchronizationState.FAILED, now, user))),
                Arguments.arguments(List.of(new SynchronizationStatus(id, null, now, user))),
                Arguments.arguments(Lists.emptyList()),
                Arguments.arguments(List.of(new SynchronizationStatus(id, SynchronizationState.FAILED, now, user), new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now
                        .minus(Duration.ofMinutes(30)), user)))
        );
    }
}