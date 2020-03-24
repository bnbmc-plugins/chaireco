package tech.cheating.chaireco.exceptions;

public class EconomyError extends Error {
    String description;

    EconomyError(String description) {
        this.description = description;
    }

    String getDescription() {
        return this.description;
    }
}
