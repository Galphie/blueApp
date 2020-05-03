package com.tfgstuff.blueapp;

public class Datos {
    private int temperature;
    private int people;
    private int co2;
    private int lux;

    public int getPeople() {
        return people;
    }

    public String getStringPeople(){
        String stringPeople = String.valueOf(people);
        return stringPeople;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public void setPeople(String people) {
        this.people = Integer.parseInt(people);
    }

    public int getCo2() {
        return co2;
    }

    public String getStringCo2(){
        String stringCo2 = String.valueOf(co2);
        return stringCo2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public void setCo2(String co2) {
        this.co2 = Integer.parseInt(co2);
    }

    public int getLux() {
        return lux;
    }

    public String getStringLux(){
        String stringLux = String.valueOf(lux);
        return stringLux;
    }

    public void setLux(int lux) {
        this.lux = lux;
    }

    public void setLux(String lux) {
        this.lux = Integer.parseInt(lux);
    }

    public int getTemperature() {
        return temperature;
    }

    public String getStringTemperature() {
        String stringTemperature = String.valueOf(temperature);
        return stringTemperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = Integer.parseInt(temperature);
    }

    public Datos() {
    }

    public Datos (String temperature, String people, String co2, String lux) {
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
