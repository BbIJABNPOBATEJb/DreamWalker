package me.bbijabnpobatejb.dreamwalker.config;

import java.io.File;

public interface IDataHandler {

    File createFolder();
    void load();
    void save();
}
