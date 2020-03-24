package tech.cheating.chaireco.exceptions;

public class EconomyException extends Exception {
    String description;

    EconomyException(String description) {
        this.description = description;
    }

    String getDescription() {
        return this.description;
    }
}
