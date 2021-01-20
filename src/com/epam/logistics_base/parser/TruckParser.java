package com.epam.logistics_base.parser;

import com.epam.logistics_base.entity.Truck;
import com.epam.logistics_base.entity.TypeOfGoods;

import java.util.ArrayList;
import java.util.List;

public class TruckParser {

    private static final String DELIMITER = "\\s";

    public List<Truck> parseTruckData(List<String> inputtedData) {
        List<Truck> trucks = new ArrayList<>();
        for(String truckData : inputtedData) {
            String[] splitData = truckData.trim().split(DELIMITER);

            int truckId = Integer.parseInt(splitData[0]);
            Truck nextTruck = new Truck(truckId);

            if (splitData.length == 2) {
                TypeOfGoods typeOfGoods = TypeOfGoods.valueOf(splitData[1]);
                nextTruck.setTypeOfGoods(typeOfGoods);
            }
            trucks.add(nextTruck);
        }

        return trucks;
    }

}
