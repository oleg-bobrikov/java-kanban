package ru.yandex.bobrikov.kanban.exceptions;

public class PeriodIntersectionException extends RuntimeException{
    public PeriodIntersectionException(String message) {
        super(message);
    }
}
