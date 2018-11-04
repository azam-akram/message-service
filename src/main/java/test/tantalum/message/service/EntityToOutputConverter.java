package test.tantalum.message.service;

import test.tantalum.message.model.entity.MessageEntity;
import test.tantalum.message.model.output.MessageOutput;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Converts from JPA Entity classes (model.entity.*Entity) into output classes (model.output.*).
 */
public class EntityToOutputConverter {

    private EntityToOutputConverter() {
        super();
    }

    public static List<MessageOutput> convertMessageEntitiesToOutput(
            Iterable<MessageEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(c -> convertMessageEntityToOutput(c))
                .collect(Collectors.toList());
    }

    public static MessageOutput convertMessageEntityToOutput(MessageEntity entity) {
        return MessageOutput.builder()
            .messageKey(entity.getMessageKey())
            .sender(entity.getSender())
            .savedAt(entity.getSavedAt())
            .build();
    }
}