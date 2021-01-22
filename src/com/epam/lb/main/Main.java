package com.epam.lb.main;

import com.epam.lb.entity.Truck;
import com.epam.lb.parser.TruckParser;
import com.epam.lb.reader.TruckFileReader;

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
