package com.template.states;

import com.template.contracts.CarStateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


@BelongsToContract(CarStateContract.class)
public class CarState implements LinearState {

    private String brand;
    private String model;
    private int year;
    private String color;
    private UniqueIdentifier carId;
    private Party manufacturer;
    private Party owner;

    public CarState(String brand, String model, int year, String color, UniqueIdentifier carId, Party manufacturer, Party owner) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.carId = carId;
        this.manufacturer = manufacturer;
        this.owner = owner;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(manufacturer,owner);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UniqueIdentifier getCarId() {
        return carId;
    }

    public void setCarId(UniqueIdentifier carId) {
        this.carId = carId;
    }

    public Party getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Party manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Party getOwner() {
        return owner;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }
}