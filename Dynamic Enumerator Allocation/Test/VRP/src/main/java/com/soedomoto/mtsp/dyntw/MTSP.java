package com.soedomoto.mtsp.dyntw;

import java.io.IOException;
import java.util.List;

/**
 * Created by soedomoto on 13/12/16.
 */
public class MTSP {

    public static void main(String[] args) throws IOException {
        Broker broker = new Broker();

        List<Enumerator> enumerators = Enumerator.readCsvData("/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/" +
                "Research/Dynamic Enumerator Allocation/Test/Data/enumerators.csv");
        for(Enumerator enumerator : enumerators) broker.addEnumerator(enumerator.getId(), enumerator);

        List<CensusBlock> censusBlocks = CensusBlock.readCsvData("/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/" +
                "Research/Dynamic Enumerator Allocation/Test/Data/enumeration_problem.csv");
        for(CensusBlock bs : censusBlocks) broker.addCensusBlock(bs.getId(), bs);

        List<InterLocationWeight> weightMatrix = InterLocationWeight.readCsvData(broker.getCensusBlocks(),
                "/media/soedomoto/DATA/ITB2015/EL5090 - Research Method/Research/Dynamic Enumerator Allocation/Test/" +
                        "Data/cost_matrix_nagari_2014.csv");
        for(InterLocationWeight weight : weightMatrix) broker.addInterLocationWeight(weight);

        broker.setOutputFile("output/mtsp_dynamic_time_windows.csv");

        Thread thread = new Thread(broker);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
