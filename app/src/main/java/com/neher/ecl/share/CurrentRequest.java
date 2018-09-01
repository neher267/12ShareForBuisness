package com.neher.ecl.share;

public class CurrentRequest {
    private int userId;
    private double lat;
    private double lng;
    private double desLat;
    private double desLng;
    private float latLngDis;
    private float desLatLngDis;

    public CurrentRequest(int userId, double lat, double lng, double desLat, double desLng, float latLngDis, float desLatLngDis) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.desLat = desLat;
        this.desLng = desLng;
        this.latLngDis = latLngDis;
        this.desLatLngDis = desLatLngDis;
    }

    public CurrentRequest() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDesLat() {
        return desLat;
    }

    public void setDesLat(double desLat) {
        this.desLat = desLat;
    }

    public double getDesLng() {
        return desLng;
    }

    public void setDesLng(double desLng) {
        this.desLng = desLng;
    }

    public float getLatLngDis() {
        return latLngDis;
    }

    public void setLatLngDis(float latLngDis) {
        this.latLngDis = latLngDis;
    }

    public float getDesLatLngDis() {
        return desLatLngDis;
    }

    public void setDesLatLngDis(float desLatLngDis) {
        this.desLatLngDis = desLatLngDis;
    }
}
