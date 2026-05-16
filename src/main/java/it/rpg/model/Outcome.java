package it.rpg.model;

/**
 * The narrative outcome associated with a {@link Scene}.
 * Non-ending scenes always use {@link #NONE}.
 */
public enum Outcome {
    /** The story continues; not an ending. */
    NONE,
    /** The player reached a successful ending. */
    VICTORY,
    /** The player reached a failure ending. */
    DEFEAT
}
