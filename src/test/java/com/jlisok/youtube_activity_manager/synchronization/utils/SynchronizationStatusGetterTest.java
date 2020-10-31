package com.jlisok.youtube_activity_manager.synchronization.utils;

import com.jlisok.youtube_activity_manager.registration.exceptions.RegistrationException;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationState;
import com.jlisok.youtube_activity_manager.synchronization.domain.SynchronizationStatus;
import com.jlisok.youtube_activity_manager.synchronization.repositories.SynchronizationRepository;
import com.jlisok.youtube_activity_manager.testutils.TestProfile;
import com.jlisok.youtube_activity_manager.testutils.UserUtils;
import com.jlisok.youtube_activity_manager.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
    void getLastSynchronization(SynchronizationStatus status) {
        // given

        when(repository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Optional.of(status));

        // when
        var actualStatus = getter.getLastSynchronization(user.getId());

        //then

        Assertions.assertEquals(status, actualStatus);
        Assertions.assertEquals(status.getState(), actualStatus.getState());
        Assertions.assertEquals(status.getCreatedAt(), actualStatus.getCreatedAt());
    }

    Stream<Arguments> inputData() {
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();
        return Stream.of(
                Arguments.arguments(new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now, user)),
                Arguments.arguments(new SynchronizationStatus(id, SynchronizationState.IN_PROGRESS, now, user)),
                Arguments.arguments(new SynchronizationStatus(id, SynchronizationState.FAILED, now, user)),
                Arguments.arguments(new SynchronizationStatus(id, null, now, user)),
                Arguments.arguments(new SynchronizationStatus())
        );
    }


    @ParameterizedTest
    @MethodSource("inputDataWithState")
    void getLastSynchronizationWithState(SynchronizationStatus status) {
        // given
        var state = SynchronizationState.SUCCEEDED;

        when(repository.findFirstByUserIdAndStateOrderByCreatedAtDesc(user.getId(), state))
                .thenReturn(Optional.of(status));

        // when
        var actualInstant = getter.getLastSynchronizationTimeWithState(user.getId(), state);

        //then

        Assertions.assertEquals(status.getCreatedAt().truncatedTo(ChronoUnit.MINUTES), actualInstant);
    }

    Stream<Arguments> inputDataWithState() {
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();
        return Stream.of(
                Arguments.arguments(new SynchronizationStatus(id, SynchronizationState.SUCCEEDED, now, user)),
                Arguments.arguments(new SynchronizationStatus(id, null, now, user))
        );
    }

    @Test
    void getLastSynchronizationWithState_NoSync() {
        // given
        var state = SynchronizationState.SUCCEEDED;

        when(repository.findFirstByUserIdAndStateOrderByCreatedAtDesc(user.getId(), state))
                .thenReturn(Optional.empty());

        // when
        var actualInstant = getter.getLastSynchronizationTimeWithState(user.getId(), state);

        //then

        Assertions.assertNull(actualInstant);
    }

}