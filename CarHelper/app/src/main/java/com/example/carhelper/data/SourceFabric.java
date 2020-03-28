package com.example.carhelper.data;

public class SourceFabric {

    public static DataGenerator buildGenerator(int max_speed, int max_prm) {
        return new SinusGenerator(max_speed, max_prm);
    }
}
