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
        //File csvFile = new File("results.csv");

        ExampleEvolutionaryAlgorithm evo = new ExampleEvolutionaryAlgorithm();
        evo.setConsoleOutput(false);
        evo.setShouldSaveOutput(false);
        evo.setShouldDisplayInitBest(false);

        Parameters.maxEvaluations = 20000; // Used to terminate the EA after this many generations (does not change)
        assertEquals(20000, Parameters.maxEvaluations);

        int _popMod = 0;
        double _mcMod = 0;
        double _mrMod = 0;

        ArrayList<SetupConfig> configs = new ArrayList<SetupConfig>();

        while (_popMod < 181)
        {
            //System.out.println("Current setup - Pop = " + (20 + _popMod) + ", MutationChange = " + (_mcMod) + ", MutationRate = " + (_mrMod));
            ParamSetup(20 + _popMod, 5, _mcMod, _mrMod); // Default set up!
            double fitness = runningOrder(evo);
            //System.out.println("Fitness found = " + fitness);
            configs.add(new SetupConfig(20 + _popMod, 5, _mcMod, _mrMod, fitness));

            if (_mcMod < 0.99)
            {
                if (_mrMod < 0.9) {
                    _mrMod += 0.1;
                } else {
                    _mcMod += 0.05;
                    _mrMod = 0.0;
                }
            } else {
                _popMod += 1;
                _mcMod = 0.0;
                _mrMod = 0.0;
            }
        }

        Collections.sort(configs, (o1, o2) -> {
            return Double.compare(o1.Fitness, o2.Fitness);
        });
        System.out.println("Done! Best fitness found = " + configs.get(0).Fitness);
        System.out.println("Config - Pop = " + configs.get(0).Population + ", Hidden Layers = " + configs.get(0).HiddenLayers + ", Mutation Change = " + configs.get(0).MutationChange + ", Mutation Rate = " + configs.get(0).MutationRate);
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