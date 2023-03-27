package coursework;

import com.google.gson.*;
import model.Fitness;
import model.LunarParameters;
import model.NeuralNetwork;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

//  Class definition for storing the configurations generated
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
        //  Configure processes used in EA
        ExampleEvolutionaryAlgorithm evo = new ExampleEvolutionaryAlgorithm();
        evo.setConsoleOutput(false);
        evo.setShouldSaveOutput(false);
        evo.setShouldDisplayInitBest(false);

        evo.setCrossoverProcess(CrossoverProcess.BestChromosome);
        evo.setMutationProcess(MutationProcess.Change);
        evo.setReplacementProcess(ReplacementProcess.Tournament);
        evo.setSelectionProcess(SelectionProcess.Tournament);

        //  Ensure that maxEvaluation is set to the strict limit of 20k
        Parameters.maxEvaluations = 20000; // Used to terminate the EA after this many generations (does not change)
        assertEquals(20000, Parameters.maxEvaluations);

        //  Set up modifiers for parameter configurations
        int _popMod = 0;
        double _mcMod = 0;
        double _mrMod = 0;

        //  Set up configuration list
        ArrayList<SetupConfig> configs = new ArrayList<SetupConfig>();

        Gson gson = new Gson();

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
        //  Custom sorting method to compare all the configurations by fitness
        Collections.sort(configs, (o1, o2) -> {
            return Double.compare(o1.Fitness, o2.Fitness);
        });

        //  Print best fitness and configuration
        System.out.println("Done! Best fitness found = " + configs.get(0).Fitness);
        System.out.println("Config - Pop = " + configs.get(0).Population + ", Hidden Layers = " + configs.get(0).HiddenLayers + ", Mutation Change = " + configs.get(0).MutationChange + ", Mutation Rate = " + configs.get(0).MutationRate);
        File directory = new File("ConfigLogs");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileToWrite = directory + "/configResults_"
                + evo.getCrossoverProcess().toString() + "_"
                + evo.getMutationProcess().toString() + "_"
                + evo.getReplacementProcess().toString() + "_"
                + evo.getSelectionProcess().toString() + "_" + ".json";
        gson.toJson(configs, new FileWriter(fileToWrite));
    }

    //  Function to configure the EA parameters
    void ParamSetup(int PopulationSize, int HiddenLayers, double MutationChange, double MutationRate) {
        Parameters.popSize = PopulationSize;
        Parameters.setHidden(HiddenLayers);
        Parameters.mutateChange = MutationChange;
        Parameters.mutateRate = MutationRate;
    }

    //  Function that handles running the EA training/evaluation
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
}