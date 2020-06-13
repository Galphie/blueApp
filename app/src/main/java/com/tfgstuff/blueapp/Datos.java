package com.tfgstuff.blueapp;

public class Datos {
    private int temperature;
    private int people;
    private int co2;
    private int lux;


    public void setPeople(String people) {
        this.people = Integer.parseInt(people);
    }

    public void setCo2(String co2) {
        this.co2 = Integer.parseInt(co2);
    }

    public void setLux(String lux) {
        this.lux = Integer.parseInt(lux);
    }

    public void setTemperature(String temperature) {
        this.temperature = Integer.parseInt(temperature);
    }

    public int getCo2() {
        return co2;
    }

    public int getPeople() {
        return people;
    }

    public int getLux() {
        return lux;
    }

    public int getTemperature() {
        return temperature;
    }

    public Datos() {
    }

    public Datos(String temperature, String people, String co2, String lux) {
        this.temperature = Integer.parseInt(temperature);
        this.people = Integer.parseInt(people);
        this.co2 = Integer.parseInt(co2);
        this.lux = Integer.parseInt(lux);
    }

    public Datos(int temperature, int people, int co2, int lux) {
        this.temperature = temperature;
        this.people = people;
        this.co2 = co2;
        this.lux = lux;
    }
}
