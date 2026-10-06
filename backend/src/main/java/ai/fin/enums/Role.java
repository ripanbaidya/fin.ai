package ai.fin.enums;

public enum Role {
    USER,
    ADMIN;

    /**
     * Returns the Spring Security authority string for this role.
     *
     * @return authority in the format ROLE_{ROLE_NAME}
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}