package dk.dtu.compute.se.pisd.roborally.exceptions;

// given IP is not valid

public class IPNotValidException extends Exception {
    public IPNotValidException() {
        super("Your IP Address is not valid");
    }
}
