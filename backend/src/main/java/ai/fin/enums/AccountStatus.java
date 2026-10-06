package ai.fin.enums;

public enum AccountStatus {

    /**
     * Initially, all accounts are inactive.
     */
    INACTIVE,

    /**
     * Account will be only active after verification.
     */
    ACTIVE,

    /**
     * Account users complete his signup process;
     * his account status will be marked as pending verification.
     */
    PENDING_VERIFICATION,

    /**
     * Account is deleted.
     */
    DELETED
}