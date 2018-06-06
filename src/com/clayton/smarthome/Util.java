package com.clayton.smarthome;

import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Util {
static final Gson GSON;
static final DateTimeFormatter DATE_FMT = DateTimeFormatter
    .ofPattern("hh:mm:ss a EEEE, MMMM dd, yyyy");
  
  static {
    GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .serializeNulls()
        .setPrettyPrinting()
        .create();
  }
  
}
