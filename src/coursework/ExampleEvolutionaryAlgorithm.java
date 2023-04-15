package coursework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */

enum CrossoverProcess {
	BestChromosome,
	OnePoint,
	TwoPoint,
	RandomCrossover
}

enum MutationProcess {
	Change,
	Swap
}

enum ReplacementProcess {
	Random,
	Tournament,
	Worst
}
enum SelectionProcess {
	Tournament,
	Random
}

public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	
	private boolean shouldOutputStats = true;
	public void setConsoleOutput(boolean isEnabled) { shouldOutputStats = isEnabled; };

	private boolean shouldSaveOutput = true;
	public void setShouldSaveOutput(boolean isEnabled) { shouldSaveOutput = isEnabled; };

	private boolean shouldDisplayInitBest = true;
	public void setShouldDisplayInitBest(boolean isEnabled) { shouldDisplayInitBest = isEnabled; };

	private CrossoverProcess crossoverProcess = CrossoverProcess.BestChromosome;
	public CrossoverProcess getCrossoverProcess() { return this.crossoverProcess; }
	public void setCrossoverProcess(CrossoverProcess crossoverProcess) {
		this.crossoverProcess = crossoverProcess;
	}
	
	private MutationProcess mutationProcess = MutationProcess.Change;
	public MutationProcess getMutationProcess() { return this.mutationProcess; }
	public void setMutationProcess(MutationProcess mutationProcess) {
		this.mutationProcess = mutationProcess;
	}

	private ReplacementProcess replacementProcess = ReplacementProcess.Worst;
	public ReplacementProcess getReplacementProcess() { return this.replacementProcess; }
	public void setReplacementProcess(ReplacementProcess replacementProcess) {
		this.replacementProcess = replacementProcess;
	}

	private SelectionProcess selectionProcess = SelectionProcess.Random;
	public SelectionProcess getSelectionProcess() { return this.selectionProcess; }
	public void setSelectionProcess(SelectionProcess selectionProcess) {
		this.selectionProcess = selectionProcess;
	}

	private double activationFunctionVal = 20.0;
	public double getActivationFunctionVal() { return activationFunctionVal; }
	public void setActivationFunctionVal(double activationFunctionVal) { this.activationFunctionVal = activationFunctionVal; }
	
	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		if (shouldDisplayInitBest)
		{
			System.out.println("Best From Initialisation " + best);
		}

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population.
			Individual parent1 = select(); 
			Individual parent2 = select();

			// Generate a child by crossover
			ArrayList<Individual> children = reproduce(parent1, parent2);			
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class.
			if (shouldOutputStats)
			{
				outputStats();
			}
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		if (shouldSaveOutput)
		{
			saveNeuralNetwork();
		}
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 * 
	 * NEEDS REPLACED with proper selection this just returns a copy of a random
	 * member of the population
	 */
	private Individual select() {
		Random rng = new Random();
		Individual parent = new Individual();
		switch (selectionProcess)
		{
			case Tournament:
				ArrayList<Individual> parents = new ArrayList<Individual>();

				for (int i = 0; i < 16; ++i)
				{
					parents.add(population.get(rng.nextInt(population.size())));
				}
				parent = parents.get(0);
				for (int i = 0; i < parents.size(); ++i)
				{
					if (parents.get(i).fitness < parent.fitness)
					{
						parent = parents.get(i);
					}
				}
				break;

			case Random:
				parent = population.get(rng.nextInt(population.size()));
				break;
		}

		return parent.copy();
	}

	/**
	 * Crossover / Reproduction
	 * 
	 * NEEDS REPLACED with proper method this code just returns exact copies of the
	 * parents. 
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		int geneLen = Parameters.getNumGenes();
		Individual newChild = new Individual();
		Random rng = new Random();
		switch (crossoverProcess)
		{
			case BestChromosome:
				for (int i = 0; i < geneLen; i++) {
					if (parent1.chromosome[i] < parent2.chromosome[i]) {
						newChild.chromosome[i] = parent1.chromosome[i];
					} else {
						newChild.chromosome[i] = parent2.chromosome[i];
					}
				}
				break;

			case OnePoint:
				int cutPoint = rng.nextInt(geneLen - 1) + 1;
				for (int x = 0; x < geneLen; ++x) {
					if (x < cutPoint) {
						newChild.chromosome[x] = parent1.chromosome[x];
					}
					else {
						newChild.chromosome[x] = parent2.chromosome[x];
					}
				}
				break;

			case TwoPoint:
				int[] cutPoints = { (rng.nextInt(geneLen - 1) + 1), (rng.nextInt(geneLen - 1) + 1) };
				Arrays.sort(cutPoints);

				for (int x = 0; x < geneLen; ++x) {
					if (x > cutPoints[0] && x < cutPoints[1]) {
						newChild.chromosome[x] = parent2.chromosome[x];
					}
					else {
						newChild.chromosome[x] = parent1.chromosome[x];
					}
				}
				break;
				
			case RandomCrossover:
				for (int x = 0; x < geneLen; ++x) {
					if (rng.nextBoolean()) {
						newChild.chromosome[x] = parent1.chromosome[x];
					}
					else {
						newChild.chromosome[x] = parent2.chromosome[x];
					}
				}
		}
		children.add(newChild);
		return children;
	} 
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			switch (mutationProcess) {
				case Change:
					for (int i = 0; i < individual.chromosome.length; i++) {
						if (Parameters.random.nextDouble() < Parameters.mutateRate) {
							if (Parameters.random.nextBoolean()) {
								individual.chromosome[i] += (Parameters.mutateChange);
							} else {
								individual.chromosome[i] -= (Parameters.mutateChange);
							}
						}
					}
					break;
				case Swap:
					Random rng = new Random();
					int pos1 = rng.nextInt(Parameters.getNumGenes());
					int pos2 = rng.nextInt(Parameters.getNumGenes());
					if (pos1 != pos2) {
						double temp = individual.chromosome[pos1];
						individual.chromosome[pos1] = individual.chromosome[pos2];
						individual.chromosome[pos2] = temp;
					}
					break;
			}
		}
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		switch (replacementProcess)
		{
			case Random:
				for (Individual individual: individuals) {
					Random rng = new Random();
					int idx = rng.nextInt(population.size());
					population.set(idx, individual);
				}
				break;

			case Tournament:
				for (Individual individual: individuals) {
					Random rng = new Random();
					for (int i = 0; i < 16; ++i)
					{
						int idx = rng.nextInt(population.size());
						if (individual.fitness > population.get(idx).fitness) {
							population.set(idx, individual);
							break;
						}
					}
				}
				break;

			case Worst:
				for (Individual individual : individuals) {
					int idx = getWorstIndex();
					if (individual.fitness < population.get(idx).fitness) {
						population.set(idx, individual);
					}
				}
				break;
		}
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -activationFunctionVal) {
			return -1.0;
		} else if (x > activationFunctionVal) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
