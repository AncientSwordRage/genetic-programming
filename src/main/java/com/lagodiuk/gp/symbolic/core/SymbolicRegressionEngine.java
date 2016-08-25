/*******************************************************************************
 * Copyright 2012 Yuriy Lagodiuk
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.lagodiuk.gp.symbolic.core;

import com.lagodiuk.ga.api.Fitness;
import com.lagodiuk.ga.implementation.GeneticAlgorithm;
import com.lagodiuk.ga.implementation.GeneticPopulation;
import com.lagodiuk.gp.symbolic.ExpressionFitness;
import com.lagodiuk.gp.symbolic.SymbolicRegressionIterationListener;
import com.lagodiuk.gp.symbolic.api.Function;
import com.lagodiuk.gp.symbolic.interpreter.Context;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import com.lagodiuk.gp.symbolic.interpreter.SyntaxTreeUtils;
import java.util.Collection;
import java.util.List;

public class SymbolicRegressionEngine {

	private final static int INITIAL_PARENT_CHROMOSOMES_SURVIVE_COUNT = 1;
	private final static int DEFAULT_POPULATION_SIZE                  = 5;
	private final static int MAX_INITIAL_TREE_DEPTH                   = 1;

	private final GeneticAlgorithm<GpChromosome, Double> environment;
	private final SymbolicRegressionFitness              fitnessFunction;
	private final Context                                context;
	private final ExpressionFitness                      expressionFitness;

	public SymbolicRegressionEngine(ExpressionFitness expressionFitness, Collection<String> variables, List<? extends Function> baseFunctions) {
		this.context           = new Context(baseFunctions, variables);
		this.expressionFitness = expressionFitness;
		this.fitnessFunction   = new SymbolicRegressionFitness(this.expressionFitness);
		final GeneticPopulation<GpChromosome, Double> population = createPopulation(this.context, fitnessFunction, DEFAULT_POPULATION_SIZE);
		this.environment = new GeneticAlgorithm<>(population, fitnessFunction);
		this.environment.getSettings().setParentSurviveCount(INITIAL_PARENT_CHROMOSOMES_SURVIVE_COUNT);
		this.environment.getSettings().setAsync(false);
	}

	private GeneticPopulation<GpChromosome, Double> createPopulation(Context context, Fitness<GpChromosome, Double> fitnessFunction, int populationSize) {
		GeneticPopulation<GpChromosome, Double> population = new GeneticPopulation<>();
		for (int i = 0; i < populationSize; i++) {
			GpChromosome chromosome = new GpChromosome(
				context,
				fitnessFunction,
				SyntaxTreeUtils.createTree(MAX_INITIAL_TREE_DEPTH, context));
			population.add(chromosome);
		}
		return population;
	}

	public void addIterationListener(final SymbolicRegressionIterationListener listener) {
		this.environment.addIterationListener(env -> listener.update(SymbolicRegressionEngine.this) );
	}

	public void evolve(int itrationsCount) {
		this.environment.evolve(itrationsCount);
	}

	public Context getContext() {
		return this.context;
	}

	public Expression getBestSyntaxTree() {
		return this.environment.getBest().getSyntaxTree();
	}

	public double fitness(Expression expression) {
		return this.expressionFitness.fitness(expression, this.context);
	}

	public void terminate() {
		this.environment.terminate();
	}

	public int getIteration() {
		return this.environment.getIteration();
	}

	public void setParentsSurviveCount(int n) {
		this.environment.getSettings().setParentSurviveCount(n);
	}
}
