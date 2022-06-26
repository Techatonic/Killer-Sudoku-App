package com.techatonic.sudokututorial.backend.killer;

public class CageDuplicateValueException extends Exception{
    public CageDuplicateValueException(String message){
        super(message);
    }
    public CageDuplicateValueException(){
        super();
    }
}