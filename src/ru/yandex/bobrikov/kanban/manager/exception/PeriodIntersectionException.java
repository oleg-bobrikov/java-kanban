package ru.yandex.bobrikov.kanban.manager.exception;

public class PeriodIntersectionException extends RuntimeException{
    public PeriodIntersectionException(String message) {
        super(message);
    }
}
