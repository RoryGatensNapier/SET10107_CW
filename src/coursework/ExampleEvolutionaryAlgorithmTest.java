package coursework;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
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
    CrossoverProcess crossoverProcess;
    MutationProcess mutationProcess;
    ReplacementProcess replacementProcess;
    SelectionProcess selectionProcess;
    SetupConfig(int Pop, int HL, double MC, double MR, double Fit, CrossoverProcess xp, MutationProcess mp, ReplacementProcess rp, SelectionProcess sp) {
        Population = Pop;
        HiddenLayers = HL;
        MutationChange = MC;
        MutationRate = MR;
        Fitness = Fit;
        crossoverProcess = xp;
        mutationProcess = mp;
        replacementProcess = rp;
        selectionProcess = sp;
    }
}

enum TestingMode {
    Crossover,
    Mutation,
    Replacement,
    Selection,
    ParameterTuning,
    SetupPermutationTesting
}

class ExampleEvolutionaryAlgorithmTest {

    int defaultPop = Parameters.popSize;
    int defaultHiddenLayers = Parameters.getNumHidden();
    double defaultMC = Parameters.mutateChange;
    double defaultMR = Parameters.mutateRate;
    
    int testRuns = 50;

    @org.junit.jupiter.api.Test
    void run() throws Exception {
        //  Configure processes used in EA
        boolean shouldSort = true;
        boolean multiConfigTesting = false;

        ExampleEvolutionaryAlgorithm evo = new ExampleEvolutionaryAlgorithm();
        evo.setConsoleOutput(false);
        evo.setShouldSaveOutput(false);
        evo.setShouldDisplayInitBest(false);

        //  Ensure that maxEvaluation is set to the strict limit of 20k
        assertEquals(20000, Parameters.maxEvaluations);

        //  Set up configuration list
        ArrayList<SetupConfig> configs = new ArrayList<SetupConfig>();

        if (multiConfigTesting)
        {
            for (TestingMode m : TestingMode.values()) {
                evo.setActivationFunctionVal(1.5);
                switch (m) {
                    case Crossover:
                        //CrossoverTesting(evo, configs);
                        break;

                    case Mutation:
                        //MutationTesting(evo, configs);
                        break;

                    case Replacement:
                        //ReplacementTesting(evo, configs);
                        break;

                    case Selection:
                        //SelectionTesting(evo, configs);
                        break;

                    case SetupPermutationTesting:
                        //PermutationTesting(evo, configs);
                        break;

                    case ParameterTuning:
                        evo.setCrossoverProcess(CrossoverProcess.OnePoint);
                        evo.setMutationProcess(MutationProcess.Change);
                        evo.setReplacementProcess(ReplacementProcess.Tournament);
                        evo.setSelectionProcess(SelectionProcess.Random);

                        ParameterTuning(evo, configs);
                        break;
                }

                if (shouldSort) {//  Custom sorting method to compare all the configurations by fitness
                    Collections.sort(configs, (o1, o2) -> {
                        return Double.compare(o1.Fitness, o2.Fitness);
                    });
                }

                if (!configs.isEmpty()) {//  Print best fitness and configuration
                    System.out.println("Done! Best fitness found = " + configs.get(0).Fitness);
                    System.out.println("Config - Pop = " + configs.get(0).Population + ", Hidden Layers = " + configs.get(0).HiddenLayers + ", Mutation Change = " + configs.get(0).MutationChange + ", Mutation Rate = " + configs.get(0).MutationRate);
                    File directory = new File("ConfigLogs/" + m.toString());
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    String fileToWrite = directory + "/configResults_"
                            + "TestingMode-" + m.toString() + "_"
                            + evo.getCrossoverProcess().toString() + "_"
                            + evo.getMutationProcess().toString() + "_"
                            + evo.getReplacementProcess().toString() + "_"
                            + evo.getSelectionProcess().toString() + "_"
                            + "NumberOfTestRuns-" + testRuns + "_"
                            + "activationFunctionVal-" + evo.getActivationFunctionVal() + "_"
                            + System.currentTimeMillis() + ".json";
                    //gson.toJson(configs, new FileWriter(fileToWrite));

                    try (JsonWriter writer = new JsonWriter(new FileWriter(fileToWrite))) {
                        writer.beginArray();
                        for (SetupConfig s : configs) {
                            writer.beginObject();
                            writer.name("CrossoverProcess").value(s.crossoverProcess.toString());
                            writer.name("MutationProcess").value(s.mutationProcess.toString());
                            writer.name("ReplacementProcess").value(s.replacementProcess.toString());
                            writer.name("SelectionProcess").value(s.selectionProcess.toString());
                            writer.name("Population").value(s.Population);
                            writer.name("HiddenLayers").value(s.HiddenLayers);
                            writer.name("MutationChange").value(s.MutationChange);
                            writer.name("MutationRate").value(s.MutationRate);
                            writer.name("Fitness").value(s.Fitness);
                            writer.endObject();
                        }
                        writer.endArray();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                configs.clear();
            }
        }
        else {
            OptimalSettingsTesting(evo, configs);

            if (shouldSort) {//  Custom sorting method to compare all the configurations by fitness
                Collections.sort(configs, (o1, o2) -> {
                    return Double.compare(o1.Fitness, o2.Fitness);
                });
            }

            if (!configs.isEmpty()) {//  Print best fitness and configuration
                System.out.println("Done! Best fitness found = " + configs.get(0).Fitness);
                System.out.println("Config - Pop = " + configs.get(0).Population + ", Hidden Layers = " + configs.get(0).HiddenLayers + ", Mutation Change = " + configs.get(0).MutationChange + ", Mutation Rate = " + configs.get(0).MutationRate);
                File directory = new File("ConfigLogs/ConfigBurning");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                String fileToWrite = directory + "/configResults_"
                        + "TestingMode-FoundOptimumConfigurationTesting" + "_"
                        + evo.getCrossoverProcess().toString() + "_"
                        + evo.getMutationProcess().toString() + "_"
                        + evo.getReplacementProcess().toString() + "_"
                        + evo.getSelectionProcess().toString() + "_"
                        + "NumberOfTestRuns-" + testRuns + "_"
                        + "activationFunctionVal-" + evo.getActivationFunctionVal() + "_"
                        + System.currentTimeMillis() + ".json";
                //gson.toJson(configs, new FileWriter(fileToWrite));

                try (JsonWriter writer = new JsonWriter(new FileWriter(fileToWrite))) {
                    writer.beginArray();
                    for (SetupConfig s : configs) {
                        writer.beginObject();
                        writer.name("CrossoverProcess").value(s.crossoverProcess.toString());
                        writer.name("MutationProcess").value(s.mutationProcess.toString());
                        writer.name("ReplacementProcess").value(s.replacementProcess.toString());
                        writer.name("SelectionProcess").value(s.selectionProcess.toString());
                        writer.name("Population").value(s.Population);
                        writer.name("HiddenLayers").value(s.HiddenLayers);
                        writer.name("MutationChange").value(s.MutationChange);
                        writer.name("MutationRate").value(s.MutationRate);
                        writer.name("Fitness").value(s.Fitness);
                        writer.endObject();
                    }
                    writer.endArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            configs.clear();
        }
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

    void ParameterTuning(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs) {
        //  Set up modifiers for parameter configurations
        int _popMod = 0;
        double _mcMod = 0;
        double _mrMod = 0;

        while (_popMod < 181) {
            //System.out.println("Current setup - Pop = " + (20 + _popMod) + ", MutationChange = " + (_mcMod) + ", MutationRate = " + (_mrMod));
            ParamSetup(20 + _popMod, 5, _mcMod, _mrMod); // Default set up!
            double fitness = runningOrder(evo);
            //double fitness = rng.nextDouble(0, 1);
            //System.out.println("Fitness found = " + fitness);
            configs.add(new SetupConfig(20 + _popMod, 5, _mcMod, _mrMod, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));

            if (_mcMod < 0.99) {
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
    }

    void CrossoverTesting(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs) {
        evo.setMutationProcess(MutationProcess.Swap);
        evo.setReplacementProcess(ReplacementProcess.Tournament);
        evo.setSelectionProcess(SelectionProcess.Tournament);
        ParamSetup(defaultPop, defaultHiddenLayers, defaultMC, defaultMR);

        evo.setCrossoverProcess(CrossoverProcess.BestChromosome);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }

        evo.setCrossoverProcess(CrossoverProcess.RandomCrossover);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }

        evo.setCrossoverProcess(CrossoverProcess.OnePoint);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }

        evo.setCrossoverProcess(CrossoverProcess.TwoPoint);
        for (int i = 0; i < testRuns; ++i) {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }
    }

    void MutationTesting(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs) {
        evo.setCrossoverProcess(CrossoverProcess.BestChromosome);
        evo.setReplacementProcess(ReplacementProcess.Tournament);
        evo.setSelectionProcess(SelectionProcess.Tournament);
        ParamSetup(defaultPop, defaultHiddenLayers, defaultMC, defaultMR);

        evo.setMutationProcess(MutationProcess.Swap);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }

        evo.setMutationProcess(MutationProcess.Change);
        for (int i = 0; i < testRuns; ++i) {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }
    }

    void ReplacementTesting(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs) {
        evo.setCrossoverProcess(CrossoverProcess.BestChromosome);
        evo.setMutationProcess(MutationProcess.Swap);
        evo.setSelectionProcess(SelectionProcess.Tournament);
        ParamSetup(defaultPop, defaultHiddenLayers, defaultMC, defaultMR);

        evo.setReplacementProcess(ReplacementProcess.Random);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }


        evo.setReplacementProcess(ReplacementProcess.Tournament);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }

        evo.setReplacementProcess(ReplacementProcess.Worst);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }
    }

    void SelectionTesting(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs) {
        evo.setCrossoverProcess(CrossoverProcess.BestChromosome);
        evo.setMutationProcess(MutationProcess.Swap);
        evo.setReplacementProcess(ReplacementProcess.Random);
        ParamSetup(defaultPop, defaultHiddenLayers, defaultMC, defaultMR);

        evo.setSelectionProcess(SelectionProcess.Random);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }

        evo.setSelectionProcess(SelectionProcess.Tournament);
        for (int i = 0; i < testRuns; ++i)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, evo.getCrossoverProcess(), evo.getMutationProcess(), evo.getReplacementProcess(), evo.getSelectionProcess()));
        }
    }

    void PermutationTesting(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs)
    {
        for (SelectionProcess sp : SelectionProcess.values()) {
            for (CrossoverProcess xp : CrossoverProcess.values()) {
                for (MutationProcess mp : MutationProcess.values()) {
                    for (ReplacementProcess rp : ReplacementProcess.values()) {
                        evo.setSelectionProcess(sp);
                        evo.setCrossoverProcess(xp);
                        evo.setMutationProcess(mp);
                        evo.setReplacementProcess(rp);
                        evo.setActivationFunctionVal(20);
                        ParamSetup(defaultPop, defaultHiddenLayers, defaultMC, defaultMR);
                        for (int i = 0; i < testRuns; ++i) {
                            double fitness = runningOrder(evo);
                            configs.add(new SetupConfig(defaultPop, defaultHiddenLayers, defaultMC, defaultMR, fitness, xp, mp, rp, sp));
                        }
                    }
                }
            }
        }
    }

    void OptimalSettingsTesting(ExampleEvolutionaryAlgorithm evo, ArrayList<SetupConfig> configs)
    {
        evo.setCrossoverProcess(CrossoverProcess.OnePoint);
        evo.setMutationProcess(MutationProcess.Change);
        evo.setReplacementProcess(ReplacementProcess.Random);
        evo.setSelectionProcess(SelectionProcess.Tournament);
        evo.setActivationFunctionVal(20);
        ParamSetup(Parameters.popSize, 5, Parameters.mutateChange, Parameters.mutateRate);
        for (int i = 0; i < testRuns; i++)
        {
            double fitness = runningOrder(evo);
            configs.add(new SetupConfig(Parameters.popSize,
                    5,
                    Parameters.mutateChange,
                    Parameters.mutateRate,
                    fitness,
                    evo.getCrossoverProcess(),
                    evo.getMutationProcess(),
                    evo.getReplacementProcess(),
                    evo.getSelectionProcess())
            );
        }

    }
}