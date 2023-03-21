package coursework;

import model.Fitness;
import model.LunarParameters;
import model.NeuralNetwork;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SetupConfig {
    int Population = 0;
    int HiddenLayers = 0;
    double MutationChange = 0;
    double MutationRate = 0;

    double Fitness = 0;

    SetupConfig(int Pop, int HL, double MC, double MR, double Fit) {
        Population = Pop;
        HiddenLayers = HL;
        MutationChange = MC;
        MutationRate = MR;
        Fitness = Fit;
    }
}

class ExampleEvolutionaryAlgorithmTest {

    @org.junit.jupiter.api.Test
    void run() throws Exception {
        File csvFile = new File("results.csv");

        ExampleEvolutionaryAlgorithm evo = new ExampleEvolutionaryAlgorithm();
        evo.setConsoleOutput(false);
        evo.setShouldSaveOutput(false);
        evo.setShouldDisplayInitBest(false);

        Parameters.maxEvaluations = 20000; // Used to terminate the EA after this many generations (does not change)
        assertEquals(20000, Parameters.maxEvaluations);

        int _popMod = 0;
        int _hdlyrMod = -4;
        double _mcMod = -0.1;
        double _mrMod = -0.01;
        int bitmask = 1;

        ArrayList<SetupConfig> configs = new ArrayList<SetupConfig>();

        while (bitmask < 16)
        {
            int pop = 20 + (_popMod * (bitmask & 8));
            int hiddenLayers = 5 + (_hdlyrMod * (bitmask & 4));
            double mutationChange = 0.1 + (_mcMod * (bitmask & 2));
            double mutationRate = 0.01 + (_mrMod * (bitmask & 1));
            ParamSetup(pop, hiddenLayers, mutationChange, mutationRate); // Default set up!
            double fitness = runningOrder(evo);

            if ((_popMod >= 180) || (_hdlyrMod >= 0) || (_mcMod >= 0.9) || (_mrMod >= 0.99)) {
                bitmask += 1;
                System.out.println("State = " + bitmask);
            }

            if ((bitmask & 8) == 1) {
                _popMod += 1;
            }
            else {
                _popMod = 0;
            }
            if ((bitmask & 4) == 1) {
                _hdlyrMod += 1;
            }
            else {
                _hdlyrMod = -4;
            }
            if ((bitmask & 2) == 1) {
                _mcMod += 0.01;
            }
            else {
                _mcMod = 0;
            }
            if ((bitmask & 1) == 1) {
                _mrMod += 0.01;
            }
            else {
                _mrMod = 0;
            }

            configs.add(new SetupConfig(pop, hiddenLayers, mutationChange, mutationRate, fitness));
        }

        Collections.sort(configs, new Comparator<SetupConfig>() {
            @Override
            public int compare(SetupConfig o1, SetupConfig o2) {
                return o1.Fitness < o2.Fitness ? 1 : 0;
            }
        });
        System.out.println("Best fitness found = " + configs.get(0).Fitness);
    }

    void ParamSetup(int PopulationSize, int HiddenLayers, double MutationChange, double MutationRate) {
        Parameters.popSize = PopulationSize;
        Parameters.setHidden(HiddenLayers);
        Parameters.mutateChange = MutationChange;
        Parameters.mutateRate = MutationRate;
    }

    double runningOrder(NeuralNetwork nn) {
        //Set the data set for training
        Parameters.setDataSet(LunarParameters.DataSet.Training);

        /*train the neural net (Go and make a coffee)*/
        nn.run();

        /** Print out the best weights found
         * (these will have been saved to disk in the project default directory)
         */
        //System.out.println("Best individual from " + Parameters.getDataSet() + " " + nn.best);/

        /**
         * We now need to test the trained network on the unseen test Set
         */
        Parameters.setDataSet(LunarParameters.DataSet.Test);
        double fitness = Fitness.evaluate(nn);
        //System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness);
        return fitness;
    }

    public void toCSV(String[] strings, String csvFile) throws Exception {
        FileWriter fileWriter = new FileWriter(csvFile);

        //write header line here if you need.

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            line.append("\"");
            line.append(strings[i].replaceAll("\"","\"\""));
            line.append("\"");
            if (i != strings.length - 1) {
                line.append(',');
            }
        }
        line.append("\n");
        fileWriter.write(line.toString());
        fileWriter.close();
    }
}