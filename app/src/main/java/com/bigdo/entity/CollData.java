package com.bigdo.entity;

/**
 * @作者： 唐京伟
 * @创建时间： 2022/12/1
 * @版本： 1.0
 * @描述：
 */
public class CollData {

    long id;
    String temp;
    String hum;
    String distance;
    String light;
    String noise;
    String people;

    public CollData(long id, String temp, String hum, String distance, String light, String noise, String people) {
        this.id = id;
        this.temp = temp;
        this.hum = hum;
        this.distance = distance;
        this.light = light;
        this.noise = noise;
        this.people = people;
    }

    public CollData() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getNoise() {
        return noise;
    }

    public void setNoise(String noise) {
        this.noise = noise;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "CollData{" +
                "id=" + id +
                ", temp='" + temp + '\'' +
                ", hum='" + hum + '\'' +
                ", distance='" + distance + '\'' +
                ", light='" + light + '\'' +
                ", noise='" + noise + '\'' +
                ", people='" + people + '\'' +
                '}';
    }
}
