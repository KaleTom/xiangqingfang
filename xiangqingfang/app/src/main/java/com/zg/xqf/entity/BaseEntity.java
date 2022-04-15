package com.zg.xqf.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseEntity {

    protected final static GsonBuilder builder = new GsonBuilder();
    protected final static Gson gson = builder.create();
}
