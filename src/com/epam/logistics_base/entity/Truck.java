package com.epam.logistics_base.entity;

import com.epam.logistics_base.exception.LogisticsBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Truck extends Thread {

    private static final Logger log = LogManager.getLogger();

    private final int truckId;
    private TypeOfGoods typeOfGoods = null;
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
        } catch (LogisticsBaseException | InterruptedException e) {
            log.error("Error in thread {}", Thread.currentThread().getName(), e);
        }
        terminal.ifPresent(value -> LogisticsBase.getInstance().releaseTerminal(this, value));
    }

    public void loadGoods() throws InterruptedException {
        TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 1);
        this.typeOfGoods = TypeOfGoods.OTHER;
        isEmpty = false;
        log.info("Truck #{} loaded goods.", truckId);
    }

    public void unloadGoods() throws InterruptedException {
        TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 1);
        typeOfGoods = null;
        isEmpty = true;
        log.info("Truck #{} unloaded goods.", truckId);
    }

    public TypeOfGoods getTypeOfGoods() {
        return typeOfGoods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Truck that = (Truck) o;

        return typeOfGoods == that.typeOfGoods;
    }

    @Override
    public int hashCode() {
        return typeOfGoods != null ? typeOfGoods.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Truck.class.getSimpleName() + "[", "]")
                .add("typeOfGoods=" + typeOfGoods)
                .toString();
    }
}
