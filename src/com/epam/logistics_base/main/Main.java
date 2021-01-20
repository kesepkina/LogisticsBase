package com.epam.logistics_base.main;

import com.epam.logistics_base.entity.Truck;
import com.epam.logistics_base.parser.TruckParser;
import com.epam.logistics_base.reader.TruckFileReader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TruckFileReader fileReader = new TruckFileReader();
        List<String> trucksData = fileReader.readFromTxtFile("data/default.txt");
        TruckParser parser = new TruckParser();
        List<Truck> trucks = parser.parseTruckData(trucksData);
        for(Truck truck : trucks) {
            truck.start();
        }
    }
}
