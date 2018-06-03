package com.clayton.smarthome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Util {
static final Gson GSON;
  
  static {
    GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .serializeNulls()
        .setPrettyPrinting()
        .create();
  }
  
}
