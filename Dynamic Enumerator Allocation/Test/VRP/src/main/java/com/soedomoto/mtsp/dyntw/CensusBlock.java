package com.soedomoto.mtsp.dyntw;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by soedomoto on 13/12/16.
 */
public class CensusBlock {
    private String id;
    private Double lat;
    private Double lon;
    private Double serviceTime;

    private boolean visited = false;

    public CensusBlock() {}

    public String getId() {
        return id;
    }

    public CensusBlock setId(String id) {
        this.id = id;
        return this;
    }

    public Double getLat() {
        return lat;
    }

    public CensusBlock setLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLon() {
        return lon;
    }

    public CensusBlock setLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public Double getServiceTime() {
        return serviceTime;
    }

    public CensusBlock setServiceTime(Double serviceTime) {
        this.serviceTime = serviceTime;
        return this;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public static List<CensusBlock> readCsvData(String csvPath) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(csvPath));

        // Skip header
        reader.readNext();

        // Read row
        List<CensusBlock> censusBlocks = new ArrayList<CensusBlock>();
        String[] row;
        while ((row = reader.readNext()) != null) {
            CensusBlock censusBlock = new CensusBlock().setId(row[0]).setLat(Double.parseDouble(row[2]))
                    .setLon(Double.parseDouble(row[1])).setServiceTime(Double.parseDouble(row[3]));
            censusBlocks.add(censusBlock);
        }

        return censusBlocks;
    }
}
