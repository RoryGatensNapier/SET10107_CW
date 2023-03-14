package coursework;

import model.Fitness;
import model.LunarParameters;
import model.NeuralNetwork;

import static org.junit.jupiter.api.Assertions.*;

enum BitwiseHack { TRUE, FALSE };

class ExampleEvolutionaryAlgorithmTest {

    @org.junit.jupiter.api.Test
    void run() throws Exception {
        ExampleEvolutionaryAlgorithm evo = new ExampleEvolutionaryAlgorithm();
        evo.setConsoleOutput(false);
        evo.setShouldSaveOutput(false);

        Parameters.maxEvaluations = 20000; // Used to terminate the EA after this many generations (does not change)
        assertEquals(20000, Parameters.maxEvaluations);

        int _pop = 20;
        int _hdlyr = 5;
        double _mc = 0.1;
        double _mr = 0.01;

        while (_pop <= 200)
        {
            ParamSetup(_pop, _hdlyr, _mc, _mr); // Default set up!
            double fitness = runningOrder(evo);
            System.out.println("\n");
            _pop += 1;
        }
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
        System.out.println("Fitness on " + Parameters.getDataSet() + " " + nn.best);

        /**
         * We now need to test the trained network on the unseen test Set
         */
        Parameters.setDataSet(LunarParameters.DataSet.Test);
        double fitness = Fitness.evaluate(nn);
        System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness);
        return fitness;
    }
}