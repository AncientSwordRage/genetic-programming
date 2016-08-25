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
import com.lagodiuk.gp.symbolic.ExpressionFitness;
import com.lagodiuk.gp.symbolic.api.Function;
import com.lagodiuk.gp.symbolic.api.SymbolicRegressionIterationListener;
import com.lagodiuk.gp.symbolic.interpreter.Context;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import com.lagodiuk.gp.symbolic.interpreter.SyntaxTreeUtils;
import java.util.Collection;
import java.util.List;

public class SymbolicRegressionEngine implements Algorithm<GpChromosome, Double>
{
	private final static int INITIAL_PARENT_SURVIVE_COUNT = 1;
	private final static int DEFAULT_POPULATION_SIZE      = 10;
	private final static int MAX_INITIAL_TREE_DEPTH       = 1;

	private final SymbolicRegressionFitness              fitnessFunc;
	private final GeneticAlgorithm<GpChromosome, Double> environment;
	private final Context                                context;
	private final ExpressionFitness                      expressionFitness;

	public SymbolicRegressionEngine(ExpressionFitness fitness, Collection<String> variables, List<? extends Function> baseFunctions)
	{
		this.expressionFitness = fitness;
		this.fitnessFunc = new SymbolicRegressionFitness(this.expressionFitness);
		this.context     = new Context(baseFunctions, variables);
		final Population<GpChromosome, Double> population = createPopulation(DEFAULT_POPULATION_SIZE);
		this.environment = new GeneticAlgorithm<>(population, fitnessFunc);
		this.environment.getSettings().setParentSurviveCount(INITIAL_PARENT_SURVIVE_COUNT);
		this.environment.getSettings().setAsync(false);
	}
	private Population<GpChromosome, Double> createPopulation(int populationSize)
	{
		final Population<GpChromosome, Double> result = new GeneticPopulation<>();
		for(int i = 0; i < populationSize; i += 1)
		{
			final Expression syntaxTree = SyntaxTreeUtils.createTree(MAX_INITIAL_TREE_DEPTH, context);
			result.add(new GpChromosome(this.context, this.fitnessFunc, syntaxTree));
		}
		return result;
	}
	@Override
	public void addIterationListener(IterationListener<GpChromosome, Double> listener)
	{
		this.environment.addIterationListener(listener);
	}
	@Override
	public void removeIterationListener(IterationListener<GpChromosome, Double> listener)
	{
		this.environment.removeIterationListener(listener);
	}
	public void addIterationListener(final SymbolicRegressionIterationListener listener)
	{
		this.environment.addIterationListener(env -> listener.update(SymbolicRegressionEngine.this));
	}
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
	@Override
	public Settings getSettings()
	{
		return this.environment.getSettings();
	}
	@Override
	public void evolve(int count)
	{
		this.environment.evolve(count);
	}
	@Override
	public void evolve()
	{
		this.environment.evolve();
	}
	@Override
	public void terminate()
	{
		this.environment.terminate();
	}
	@Override
	public int getIteration()
	{
		return this.environment.getIteration();
	}
	@Override
	public void resetIterations()
	{
		this.environment.resetIterations();
	}
	@Override
	public Population<GpChromosome, Double> getPopulation()
	{
		return this.environment.getPopulation();
	}
	@Override
	public GpChromosome getBest()
	{
		return this.environment.getBest();
	}
	@Override
	public GpChromosome getWorst()
	{
		return this.environment.getWorst();
	}
	@Override
	public Double getFitness(GpChromosome chromosome)
	{
		return this.environment.getFitness(chromosome);
	}
	@Override
	public void clearCache()
	{
		this.environment.clearCache();
	}
}
