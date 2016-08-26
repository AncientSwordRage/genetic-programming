/**
 * *****************************************************************************
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
 *****************************************************************************
 */
package com.lagodiuk.gp.symbolic.core;

import com.lagodiuk.ga.api.Algorithm;
import com.lagodiuk.ga.api.IterationListener;
import com.lagodiuk.ga.api.Population;
import com.lagodiuk.ga.api.Settings;
import com.lagodiuk.ga.implementation.GeneticAlgorithm;
import com.lagodiuk.ga.implementation.GeneticPopulation;
import com.lagodiuk.gp.symbolic.api.Function;
import com.lagodiuk.gp.symbolic.api.SymbolicRegressionDefaults;
import com.lagodiuk.gp.symbolic.api.SymbolicRegressionIterationListener;
import com.lagodiuk.gp.symbolic.interpreter.Context;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import com.lagodiuk.gp.symbolic.interpreter.ExpressionFitness;
import com.lagodiuk.gp.symbolic.interpreter.SyntaxTreeUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SymbolicRegressionEngine implements IterationListener<GpChromosome, Double>
{
	private final GpFitness                              fitnessFunc;
	private final GeneticAlgorithm<GpChromosome, Double> environment;
	private final Context                                context;
	private final ExpressionFitness                      expressionFitness;

	public SymbolicRegressionEngine(ExpressionFitness fitness, Collection<String> variables, List<? extends Function> baseFunctions)
	{
		this.expressionFitness = fitness;
		this.fitnessFunc = new GpFitness(this.expressionFitness);
		this.context     = new Context(baseFunctions, variables);
		final Population<GpChromosome, Double> population = createPopulation(SymbolicRegressionDefaults.DEFAULT_POPULATION_SIZE);
		this.environment = new GeneticAlgorithm<>(population, fitnessFunc);
		this.environment.addIterationListener((IterationListener<GpChromosome, Double>)this);
		this.environment.getSettings().setParentSurviveCount(SymbolicRegressionDefaults.INITIAL_PARENT_SURVIVE_COUNT);
		this.environment.getSettings().setAsync(false);
	}
	private Population<GpChromosome, Double> createPopulation(int populationSize)
	{
		final Population<GpChromosome, Double> result = new GeneticPopulation<>();
		for(int i = 0; i < populationSize; i += 1)
		{
			final Expression syntaxTree = SyntaxTreeUtils.createTree(SymbolicRegressionDefaults.INITIAL_TREE_DEPTH, context);
			result.add(new GpChromosome(this.context, this.fitnessFunc, syntaxTree));
		}
		return result;
	}
	private final List<SymbolicRegressionIterationListener> listeners = new LinkedList<>();
	@Override
	public void onNewGeneration(Algorithm<GpChromosome, Double> environment)
	{
		for(SymbolicRegressionIterationListener sril : listeners)
			sril.onNewGeneration(this);
	}
	public void addIterationListener(SymbolicRegressionIterationListener listener)
	{
		this.listeners.add(listener);
	}
	public void removeIterationListener(SymbolicRegressionIterationListener listener)
	{
		this.listeners.remove(listener);
	}
	/*
	public void addIterationListener(SymbolicRegressionIterationListener listener)
	{
		this.environment.addIterationListener(listener);
	}
	public void removeIterationListener(SymbolicRegressionIterationListener listener)
	{
		this.environment.removeIterationListener(listener);
	}
	*/
	public Context getContext()
	{
		return this.context;
	}
	public Expression getBestSyntaxTree()
	{
		return this.environment.getBest().getSyntaxTree();
	}
	public double getFitness(Expression expression)
	{
		return this.expressionFitness.fitness(expression, this.context);
	}
	public Settings getSettings()
	{
		return this.environment.getSettings();
	}
	public void evolve(int count)
	{
		this.environment.evolve(count);
	}
	public void evolve()
	{
		this.environment.evolve();
	}
	public void terminate()
	{
		this.environment.terminate();
	}
	public int getIteration()
	{
		return this.environment.getIteration();
	}
	public void resetIterations()
	{
		this.environment.resetIterations();
	}
	public Population<GpChromosome, Double> getPopulation()
	{
		return this.environment.getPopulation();
	}
	public GpChromosome getBest()
	{
		return this.environment.getBest();
	}
	public GpChromosome getWorst()
	{
		return this.environment.getWorst();
	}
	public Double getFitness(GpChromosome chromosome)
	{
		return this.environment.getFitness(chromosome);
	}
	public void clearCache()
	{
		this.environment.clearCache();
	}
}
