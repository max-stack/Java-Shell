package uk.ac.ucl.jsh;


enum ConnectionType {
    PIPE("|"),
    SEQUENCE(";"),
    REDIRECT_TO(">"),
    REDIRECT_FROM("<"),
    END_COMMAND("£"),
    SUBSTITUTION("`");

    private final String name;

    ConnectionType(String s) {
        name = s;
    }

    /**
     * This method compares an inputted string and checks whether it is a
     * ConnectionType or not.
     *
     * @param s The String to be compared with all other ConnectionTypes.
     *
     * @return a boolean denoting of the input string is equivalent to a ConnectonType
     *
     */

    public static boolean connectionExists(String s) {
        for (ConnectionType c : ConnectionType.values()) {
            if (c.toString().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return this.name;
    }
}
