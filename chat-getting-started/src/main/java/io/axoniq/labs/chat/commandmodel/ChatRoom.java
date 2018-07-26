package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import java.util.HashSet;
import java.util.Set;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

public class ChatRoom {

    @AggregateIdentifier
    private String roomId;

    private Set<String> participants = new HashSet<>();

    public ChatRoom() {

    }

    @CommandHandler
    public ChatRoom(CreateRoomCommand cmd) {
        apply(new RoomCreatedEvent(cmd.getRoomId(), cmd.getName()));
    }

    @CommandHandler
    public void handle(JoinRoomCommand cmd) {
        if (!participants.contains(cmd.getParticipant())){
        apply(new ParticipantJoinedRoomEvent(cmd.getParticipant(), cmd.getRoomId()));
        }
    }

    @CommandHandler
    public void handle(LeaveRoomCommand cmd) {
        if (participants.contains(cmd.getParticipant())){
            apply(new ParticipantLeftRoomEvent(cmd.getParticipant(), cmd.getRoomId()));
        }
    }

    @CommandHandler
    public void handle(PostMessageCommand cmd) {
        if (!participants.contains(cmd.getParticipant())) {
            throw new IllegalStateException("You must join a room before posting!");
        }
            apply(new MessagePostedEvent(cmd.getParticipant(), cmd.getRoomId(), cmd.getMessage()));

    }

    @EventSourcingHandler
    public void on (RoomCreatedEvent event) {
        roomId = event.getRoomId();
    }

    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent event) {
        participants.add(event.getParticipant());
    }

    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent event) {
        participants.remove(event.getParticipant());
    }
}
