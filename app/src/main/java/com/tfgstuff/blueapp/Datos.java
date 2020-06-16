package com.tfgstuff.blueapp;

public class Datos {
    private String temperature;
    private String people;
    private String co2;
    private String lux;

    public Datos() {
    }

    public Datos(String temperature, String co2, String people, String lux) {
        this.temperature = temperature;
        this.people = people;
        this.co2 = co2;
        this.lux = lux;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
        this.co2 = co2;
    }

    public String getLux() {
        return lux;
    }

    public void setLux(String lux) {
        this.lux = lux;
    }

    public int temperatureToInt() {
        return Integer.parseInt(this.temperature);
    }

    public int co2ToInt() {
        return Integer.parseInt(this.co2);
    }

    public int peopleToInt() {
        return Integer.parseInt(this.people);
    }

    public int luxToInt() {
        return Integer.parseInt(this.lux);
    }

}
