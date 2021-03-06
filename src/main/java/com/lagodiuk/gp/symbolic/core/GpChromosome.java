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

import com.lagodiuk.ga.api.Chromosome;
import com.lagodiuk.ga.api.Fitness;
import com.lagodiuk.gp.symbolic.api.Function;
import com.lagodiuk.gp.symbolic.interpreter.Context;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import com.lagodiuk.gp.symbolic.interpreter.SyntaxTreeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GpChromosome implements Chromosome<GpChromosome> {
	Expression syntaxTree;
	final Fitness<GpChromosome, Double> fitnessFunction;
	boolean isTreeOptimized = false;

	Context context;
	final Random random = new Random();

	public GpChromosome(Context context, Fitness<GpChromosome, Double> fitnessFunction, Expression syntaxTree) {
		this.context = context;
		this.fitnessFunction = fitnessFunction;
		this.syntaxTree = syntaxTree;
	}

	@Override
	public List<GpChromosome> crossover(GpChromosome anotherChromosome) {
		List<GpChromosome> ret = new ArrayList<>(2);

		GpChromosome thisClone    = new GpChromosome(this.context, this.fitnessFunction, this.syntaxTree.clone());
		GpChromosome anotherClone = new GpChromosome(this.context, this.fitnessFunction, anotherChromosome.syntaxTree.clone());

		Expression thisRandomNode    = this.getRandomNode(thisClone.syntaxTree);
		Expression anotherRandomNode = this.getRandomNode(anotherClone.syntaxTree);

		Expression thisRandomSubTreeClone    = thisRandomNode.clone();
		Expression anotherRandomSubTreeClone = anotherRandomNode.clone();

		this.swapNode(thisRandomNode,    anotherRandomSubTreeClone);
		this.swapNode(anotherRandomNode, thisRandomSubTreeClone);

		ret.add(thisClone);
		ret.add(anotherClone);

		thisClone.isTreeOptimized    = false;
		anotherClone.isTreeOptimized = false;
		// thisClone.optimizeTree();
		// anotherClone.optimizeTree();

		return ret;
	}

	@Override
	public GpChromosome mutate() {
		GpChromosome ret = new GpChromosome(this.context, this.fitnessFunction, this.syntaxTree.clone());

		int type = this.random.nextInt(7);
		switch (type) {
			case 0:
				ret.mutateByRandomChangeOfFunction();
				break;
			case 1:
				ret.mutateByRandomChangeOfChild();
				break;
			case 2:
				ret.mutateByRandomChangeOfNodeToChild();
				break;
			case 3:
				ret.mutateByReverseOfChildsList();
				break;
			case 4:
				ret.mutateByRootGrowth();
				break;
			case 5:
				ret.syntaxTree = SyntaxTreeUtils.createTree(2, this.context);
				break;
			case 6:
				ret.mutateByReplaceEntireTreeWithAnySubTree();
				break;
		}

		ret.isTreeOptimized = false;
		// ret.optimizeTree();
		return ret;
	}

	private void mutateByReplaceEntireTreeWithAnySubTree() {
		this.syntaxTree = this.getRandomNode(this.syntaxTree);
	}

	private void mutateByRootGrowth() {
		Function function = this.context.getRandomNonTerminalFunction();
		Expression newRoot = new Expression(function);
		newRoot.addChild(this.syntaxTree);
		for (int i = 1; i < function.argumentsCount(); i++) {
			newRoot.addChild(SyntaxTreeUtils.createTree(0, this.context));
		}
		for (int i = 0; i < function.argumentsCount(); i++) {
			newRoot.addCoefficient(this.context.getRandomValue());
		}
		this.syntaxTree = newRoot;
	}

	private void mutateByRandomChangeOfFunction() {
		Expression mutatingNode = this.getRandomNode(this.syntaxTree);

		Function oldFunction = mutatingNode.getFunction();
		Function newFunction = oldFunction;

		while(newFunction == oldFunction) {
			newFunction = (this.random.nextDouble() > 0.5)
				? this.context.getRandomNonTerminalFunction()
				: this.context.getRandomTerminalFunction();
		}

		mutatingNode.setFunction(newFunction);

		if (newFunction.isVariable()) {
			mutatingNode.setVariable(this.context.getRandomVariableName());
		}

		int functionArgumentsCount = newFunction.argumentsCount();
		int mutatingNodeChildsCount = mutatingNode.getChilds().size();

		if (functionArgumentsCount > mutatingNodeChildsCount) {
			for (int i = 0; i < ((functionArgumentsCount - mutatingNodeChildsCount) + 1); i++) {
				mutatingNode.getChilds().add(SyntaxTreeUtils.createTree(1, this.context));
			}
		} else if (functionArgumentsCount < mutatingNodeChildsCount) {
			List<Expression> subList = new ArrayList<>(functionArgumentsCount);
			for (int i = 0; i < functionArgumentsCount; i++) {
				subList.add(mutatingNode.getChilds().get(i));
			}
			mutatingNode.setChilds(subList);
		}

		int functionCoefficientsCount = newFunction.coefficientsCount();
		int mutatingNodeCoefficientsCount = mutatingNode.getCoefficientsOfNode().size();
		if (functionCoefficientsCount > mutatingNodeCoefficientsCount) {
			for (int i = 0; i < ((functionCoefficientsCount - mutatingNodeCoefficientsCount) + 1); i++) {
				mutatingNode.addCoefficient(this.context.getRandomValue());
			}
		} else if (functionCoefficientsCount < mutatingNodeCoefficientsCount) {
			List<Double> subList = new ArrayList<>(functionCoefficientsCount);
			for (int i = 0; i < functionCoefficientsCount; i++) {
				subList.add(mutatingNode.getCoefficientsOfNode().get(i));
			}
			mutatingNode.setCoefficientsOfNode(subList);
		}
	}

	private void mutateByReverseOfChildsList() {
		Expression mutatingNode = this.getRandomNode(this.syntaxTree);
		Function mutatingNodeFunction = mutatingNode.getFunction();

		if ((mutatingNode.getChilds().size() > 1)
				&& (!mutatingNodeFunction.isCommutative())) {

			Collections.reverse(mutatingNode.getChilds());

		} else {
			this.mutateByRandomChangeOfFunction();
		}
	}

	private void mutateByRandomChangeOfChild() {
		Expression mutatingNode = this.getRandomNode(this.syntaxTree);

		if (!mutatingNode.getChilds().isEmpty()) {

			int indx = this.random.nextInt(mutatingNode.getChilds().size());

			mutatingNode.getChilds().set(indx, SyntaxTreeUtils.createTree(1, this.context));

		} else {
			this.mutateByRandomChangeOfFunction();
		}
	}

	private void mutateByRandomChangeOfNodeToChild() {
		Expression mutatingNode = this.getRandomNode(this.syntaxTree);

		if (!mutatingNode.getChilds().isEmpty()) {

			int indx = this.random.nextInt(mutatingNode.getChilds().size());

			Expression child = mutatingNode.getChilds().get(indx);

			this.swapNode(mutatingNode, child.clone());

		} else {
			this.mutateByRandomChangeOfFunction();
		}
	}

	private Expression getRandomNode(Expression tree) {
		List<Expression> allNodesOfTree = tree.getAllNodesAsList();
		int allNodesOfTreeCount = allNodesOfTree.size();
		int indx = this.random.nextInt(allNodesOfTreeCount);
		return allNodesOfTree.get(indx);
	}

	private void swapNode(Expression oldNode, Expression newNode) {
		oldNode.setChilds(newNode.getChilds());
		oldNode.setFunction(newNode.getFunction());
		oldNode.setCoefficientsOfNode(newNode.getCoefficientsOfNode());
		oldNode.setVariable(newNode.getVariable());
	}

	public void optimizeTree()
	{
		this.optimizeTree(GpDefaults.OPTIMIZING_TREE_ITERATIONS);
	}
	private synchronized void optimizeTree(int iterations)
	{

		isTreeOptimized = true;
		SyntaxTreeUtils.cutTree     (this.syntaxTree, this.context, 6);
		SyntaxTreeUtils.simplifyTree(this.syntaxTree, this.context);
		optimizeCoefficients(iterations);
	}
	private void optimizeCoefficients(int iterations)
	{
		final List<Double> coefficientsOfTree = this.syntaxTree.getCoefficientsOfTree();

		if(!coefficientsOfTree.isEmpty())
		{
			final CoefficientsPopulation population   = new CoefficientsPopulation();
			final CoefficientsChromosome coefficients = new CoefficientsChromosome(this, coefficientsOfTree,
				GpDefaults.OPTIMIZING_TREE_PMUTATION, GpDefaults.OPTIMIZING_TREE_PCROSSOVER);
			population.add(coefficients);

			for (int i = 0; i < GpDefaults.OPTIMIZING_TREE_MUTATED; i++)
				population.add(coefficients.mutate());

			final CoefficientsFitness fitness = new CoefficientsFitness(this);
			final CoefficientsEngine  engine  = new CoefficientsEngine(population, fitness);
			engine.getSettings().setAsync(GpDefaults.OPTIMIZING_TREE_ASYNC);

			engine.evolve(iterations);

			final List<Double> optimizedCoefficients = engine.getBest().getCoefficients();
			this.syntaxTree.setCoefficientsOfTree(optimizedCoefficients);
		}
	}
	public Context getContext()
	{
		return this.context;
	}
	public void setContext(Context context)
	{
		this.context = context;
	}
	public Expression getSyntaxTree()
	{
		return this.syntaxTree;
	}
	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.syntaxTree);
		hash = 89 * hash + (this.isTreeOptimized ? 1 : 0);
		// hash = 89 * hash + Objects.hashCode(this.fitnessFunction);
		// hash = 89 * hash + Objects.hashCode(this.context);
		return hash;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null || getClass() != obj.getClass())
			return false;
		final GpChromosome other = (GpChromosome)obj;
		if(this.isTreeOptimized != other.isTreeOptimized)
			return false;
		/*
		if(!Objects.equals(this.fitnessFunction, other.fitnessFunction))
			return false;
		if(!Objects.equals(this.context, other.context))
			return false;
		*/
		return Objects.equals(this.syntaxTree, other.syntaxTree);
	}
}
