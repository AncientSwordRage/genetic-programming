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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CoefficientsChromosome implements Chromosome<CoefficientsChromosome>, Cloneable
{
	private final GpChromosome gpc;
	private final List<Double> coefficients;
	private final double pMutation;
	private final double pCrossover;

	public CoefficientsChromosome(GpChromosome gpc, List<Double> coefficients, double pMutation, double pCrossover)
	{
		this.gpc = gpc;
		this.coefficients = coefficients;
		this.pMutation = pMutation;
		this.pCrossover = pCrossover;
	}
	@Override
	public List<CoefficientsChromosome> crossover(CoefficientsChromosome anotherChromosome)
	{
		List<CoefficientsChromosome> ret = new ArrayList<>(2);

		CoefficientsChromosome thisClone    = this.clone();
		CoefficientsChromosome anotherClone = anotherChromosome.clone();

		for(int i = 0; i < thisClone.coefficients.size(); i++)
			if(this.gpc.random.nextDouble() > this.pCrossover)
			{
				thisClone.coefficients.set(i, anotherChromosome.coefficients.get(i));
				anotherClone.coefficients.set(i, this.coefficients.get(i));
			}
		ret.add(thisClone);
		ret.add(anotherClone);
		return ret;
	}
	@Override
	public CoefficientsChromosome mutate()
	{
		CoefficientsChromosome result = this.clone();
		for(int i = 0; i < result.coefficients.size(); i++)
			if(this.gpc.random.nextDouble() > this.pMutation)
			{
				double coeff = result.coefficients.get(i);
				coeff += this.gpc.context.getRandomMutationValue();
				result.coefficients.set(i, coeff);
			}
		return result;
	}
	@Override
	@SuppressWarnings({ "CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported" })
	protected CoefficientsChromosome clone()
	{
		final List<Double> clonedCoefficients = new ArrayList<>(this.coefficients);
		return new CoefficientsChromosome(gpc, clonedCoefficients, this.pMutation, this.pCrossover);
	}
	public List<Double> getCoefficients()
	{
		return this.coefficients;
	}
	@Override
	public int hashCode()
	{
		int hash = 129 + Objects.hashCode(this.coefficients);
		// hash = 43 * hash + Objects.hashCode(this.gpc);
		// hash = 43 * hash + (int)(Double.doubleToLongBits(this.pMutation)  ^ (Double.doubleToLongBits(this.pMutation)  >>> 32));
		// hash = 43 * hash + (int)(Double.doubleToLongBits(this.pCrossover) ^ (Double.doubleToLongBits(this.pCrossover) >>> 32));
		return hash;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null || getClass() != obj.getClass())
			return false;
		final CoefficientsChromosome other = (CoefficientsChromosome)obj;
		/*
		if(Double.doubleToLongBits(this.pMutation)  != Double.doubleToLongBits(other.pMutation))
			return false;
		if(Double.doubleToLongBits(this.pCrossover) != Double.doubleToLongBits(other.pCrossover))
			return false;
		if(!Objects.equals(this.gpc, other.gpc))
			return false;
		*/
		return Objects.equals(this.coefficients, other.coefficients);
	}
}
