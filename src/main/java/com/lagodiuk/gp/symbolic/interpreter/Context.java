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
package com.lagodiuk.gp.symbolic.interpreter;

import com.lagodiuk.gp.symbolic.api.Function;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class Context
{
	private final static double MIN_VALUE          = -50;
	private final static double MAX_VALUE          =  50;
	private final static double MIN_MUTATION_VALUE = -3;
	private final static double MAX_MUTATION_VALUE =  3;

	private final Map<String, Double> variables            = new HashMap<>();
	private final List<Function>      nonTerminalFunctions = new ArrayList<>();
	private final List<Function>      terminalFunctions    = new ArrayList<>();
	private       int nextRndFunctionIndx = 0;

	public Context(List<? extends Function> functions, Collection<String> variables)
	{
		for(Function func : functions)
			if(func.argumentsCount() == 0)
				this.terminalFunctions.add(func);
			else
				this.nonTerminalFunctions.add(func);
		if(this.terminalFunctions.isEmpty())
			throw new IllegalArgumentException("At least one terminal function must be defined");

		if(variables.isEmpty())
			throw new IllegalArgumentException("At least one variable must be defined");

		for(String variable : variables)
			this.setVariable(variable, 0);
	}
	public double lookupVariable(String variable)
	{
		return this.variables.get(variable);
	}
	public void setVariable(String variable, double value)
	{
		this.variables.put(variable, value);
	}
	public Function getRandomNonTerminalFunction()
	{
		return this.roundRobinFunctionSelection();
		// return this.randomFunctionSelection();
	}
	// private Function randomFunctionSelection() {
	// int indx = this.random.nextInt(this.nonTerminalFunctions.size());
	// return this.nonTerminalFunctions.get(indx);
	// }
	private Function roundRobinFunctionSelection()
	{
		if(this.nextRndFunctionIndx >= this.nonTerminalFunctions.size())
		{
			this.nextRndFunctionIndx = 0;
			Collections.shuffle(this.nonTerminalFunctions);
		}
		// round-robin like selection
		return this.nonTerminalFunctions.get(this.nextRndFunctionIndx++);
	}
	public Function getRandomTerminalFunction()
	{
		final int      index    = ThreadLocalRandom.current().nextInt(this.terminalFunctions.size());
		final Function function = this.terminalFunctions.get(index);
		return function;
	}
	public List<Function> getTerminalFunctions()
	{
		return this.terminalFunctions;
	}
	public String getRandomVariableName()
	{
		int index = ThreadLocalRandom.current().nextInt(this.variables.keySet().size());
		int i = 0;
		for(String key : this.variables.keySet())
		{
			if(i == index)
				return key;
			i += 1;
		}
		// Unreachable code
		return this.variables.keySet().iterator().next();
	}
	public double getRandomValue()
	{
		final double random = ThreadLocalRandom.current().nextDouble();
		return MIN_VALUE + random * (MAX_VALUE - MIN_VALUE);
	}
	public double getRandomMutationValue()
	{
		final double random = ThreadLocalRandom.current().nextDouble();
		return MIN_MUTATION_VALUE + random * (MAX_MUTATION_VALUE - MIN_MUTATION_VALUE);
	}
	public boolean hasVariables()
	{
		return !this.variables.isEmpty();
	}
}
