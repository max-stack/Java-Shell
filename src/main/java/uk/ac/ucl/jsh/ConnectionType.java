package uk.ac.ucl.jsh;

enum ConnectionType  {
    PIPE ("|"),
    SEQUENCE (";"),
    REDIRECT_TO (">"),
    REDIRECT_FROM ("<");

    private final String name;

    private ConnectionType(String s){
        name = s;
    }

    public static boolean connectionExists(String s){
        for(ConnectionType c : ConnectionType.values()){
            if(c.toString().equals(s)){
                return true;
            }
        }
        return false;
    }

    public String toString(){
        return this.name;
    }
}