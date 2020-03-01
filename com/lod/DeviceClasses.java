package com.lod;

import java.util.Hashtable;

import static com.lod.Labeles.LAPTOP;
import static com.lod.Labeles.*;

public class DeviceClasses {
    protected static Hashtable<String, Labeles> classes = new Hashtable<>();

    static {
        classes.put("256", COMPUTER);
        classes.put("512", PHONE);
        classes.put("25612", LAPTOP);
    }
}
