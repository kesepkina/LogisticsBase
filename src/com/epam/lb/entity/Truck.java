package com.epam.lb.entity;

import com.epam.lb.exception.LogisticsBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Truck extends Thread {

    private static final Logger log = LogManager.getLogger();

    private final int truckId;
    private TypeOfGoods typeOfGoods;
    private boolean isEmpty = true;

    public Truck(int truckId) {
        this.truckId = truckId;
    }

    public Truck(int truckId, TypeOfGoods typeOfGoods) {
        this.truckId = truckId;
        this.typeOfGoods = typeOfGoods;
        isEmpty = false;
        if (typeOfGoods.equals(TypeOfGoods.PERISHABLE)) {
            this.setPriority(Thread.MAX_PRIORITY);
        }
    }

    public int getTruckId() {
        return truckId;
    }

    public void setTypeOfGoods(TypeOfGoods typeOfGoods) {
        this.typeOfGoods = typeOfGoods;
        if (typeOfGoods.equals(TypeOfGoods.PERISHABLE)) {
            this.setPriority(Thread.MAX_PRIORITY);
        }
        isEmpty = false;
    }

    @Override
    public void run() {
        Optional<Terminal> terminal = Optional.empty();
        try {
            terminal = LogisticsBase.getInstance().takeTruck(this);
            if (terminal.isEmpty()) {
                log.error("Truck didn't get empty terminal");
            } else {
                if (isEmpty) {
                    loadGoods();
                } else {
                    unloadGoods();
                }
            }
        } catch (LogisticsBaseException e) {
            log.error("Error in thread {}:", this.getName(), e);
        } catch (InterruptedException e) {
            this.interrupt();
            log.error("Error in thread {}:", this.getName(), e);
        }
        terminal.ifPresent(value -> LogisticsBase.getInstance().releaseTerminal(this, value));
    }

    public void loadGoods() throws InterruptedException {
        TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 1);
        this.typeOfGoods = TypeOfGoods.NORMAL;
        isEmpty = false;
        log.info("Truck #{} loaded goods", truckId);
    }

    public void unloadGoods() throws InterruptedException {
        TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 1);
        typeOfGoods = null;
        isEmpty = true;
        log.info("Truck #{} unloaded goods", truckId);
    }

    public TypeOfGoods getTypeOfGoods() {
        return typeOfGoods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Truck truck = (Truck) o;

        if (truckId != truck.truckId) return false;
        if (isEmpty != truck.isEmpty) return false;
        return typeOfGoods == truck.typeOfGoods;
    }

    @Override
    public int hashCode() {
        int result = truckId;
        result = 31 * result + (typeOfGoods != null ? typeOfGoods.hashCode() : 0);
        result = 31 * result + (isEmpty ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Truck.class.getSimpleName() + "[", "]")
                .add("truckId=" + truckId)
                .add("typeOfGoods=" + typeOfGoods)
                .add("isEmpty=" + isEmpty)
                .toString();
    }
}
