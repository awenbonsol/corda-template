package com.template.webserver;

public class Forms {

    public static class Car {

        private String brand;
        private String model;
        private int year;
        private String color;

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }

        public int getYear() {
            return year;
        }

        public String getColor() {
            return color;
        }
    }

    public static class Transfer {
        private String owner;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }
    }

}
