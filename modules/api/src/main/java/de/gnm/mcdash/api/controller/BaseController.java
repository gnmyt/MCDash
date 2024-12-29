package de.gnm.mcdash.api.controller;

public abstract class BaseController {

    protected final String database;

    public BaseController(String database) {
        this.database = database;
    }


}
