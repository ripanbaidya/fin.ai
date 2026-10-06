package ai.fin.enums;

/**
 * Represents the current lifecycle status of a savings goal.
 */
public enum GoalStatus {

    /**
     * The goal is active and contributions can still be made.
     */
    IN_PROGRESS,

    /**
     * The goal target amount has been reached successfully.
     */
    ACHIEVED,

    /**
     * The goal deadline passed before the target amount was reached.
     */
    FAILED
}