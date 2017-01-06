package com.soedomoto.mtsp.dyntw;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by soedomoto on 13/12/16.
 */
public class InterLocationWeight {
    private CensusBlock from;
    private CensusBlock to;
    private Double distance;
    private Double time;

    public InterLocationWeight() {}

    public CensusBlock getFrom() {
        return from;
    }

    public InterLocationWeight setFrom(CensusBlock from) {
        this.from = from;
        return this;
    }

    public CensusBlock getTo() {
        return to;
    }

    public InterLocationWeight setTo(CensusBlock to) {
        this.to = to;
        return this;
    }

    public Double getTime() {
        return time;
    }

    public InterLocationWeight setTime(Double time) {
        this.time = time;
        return this;
    }

    public Double getDistance() {
        return distance;
    }

    public InterLocationWeight setDistance(Double distance) {
        this.distance = distance;
        return this;
    }

    public String getId() {
        return this.from.getId() + "-" + this.to.getId();
    }

    public static List<InterLocationWeight> readCsvData(Map<String, CensusBlock> censusBlocks, String csvPath) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(csvPath));

        // Skip header
        reader.readNext();

        // Read row
        List<InterLocationWeight> distanceMatrix = new ArrayList<InterLocationWeight>();
        String[] row;
        while ((row = reader.readNext()) != null) {
            InterLocationWeight cost;

            try {
                cost = new InterLocationWeight().setFrom(censusBlocks.get(row[0]))
                        .setTo(censusBlocks.get(row[1])).setDistance(Double.parseDouble(row[2]))
                        .setTime(Double.parseDouble(row[3]));
            } catch (Exception e) {
                cost = new InterLocationWeight().setFrom(censusBlocks.get(row[0]))
                        .setTo(censusBlocks.get(row[1])).setDistance(0.0)
                        .setTime(0.0);
            }

            distanceMatrix.add(cost);
        }

        return distanceMatrix;
    }
}
