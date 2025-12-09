package com.example.eam.Exception;


public class AssetIdAlreadyExistsException extends RuntimeException {

    public AssetIdAlreadyExistsException(String message) {
        super(message);
    }
}

