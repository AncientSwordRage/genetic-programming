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
package com.lagodiuk.gp.symbolic;

import com.lagodiuk.ga.Chromosome;
import java.util.ArrayList;
import java.util.List;

class GpCoefficientsChromosome implements Chromosome<GpCoefficientsChromosome>, Cloneable {

	private final GpChromosome gpc;
	private final List<Double> coefficients;

	private final double pMutation;
	private final double pCrossover;

	public GpCoefficientsChromosome(GpChromosome gpc, List<Double> coefficients, double pMutation, double pCrossover) {
		this.gpc          = gpc;
		this.coefficients = coefficients;
		this.pMutation    = pMutation;
		this.pCrossover   = pCrossover;
	}

	@Override
	public List<GpCoefficientsChromosome> crossover(GpCoefficientsChromosome anotherChromosome) {
		List<GpCoefficientsChromosome> ret = new ArrayList<>(2);

		GpCoefficientsChromosome thisClone    = this.clone();
		GpCoefficientsChromosome anotherClone = anotherChromosome.clone();

		for (int i = 0; i < thisClone.coefficients.size(); i++) {
			if (this.gpc.random.nextDouble() > this.pCrossover) {
				thisClone.coefficients.set(i, anotherChromosome.coefficients.get(i));
				anotherClone.coefficients.set(i, this.coefficients.get(i));
			}
		}
		ret.add(thisClone);
		ret.add(anotherClone);

		return ret;
	}

	@Override
	public GpCoefficientsChromosome mutate() {
		GpCoefficientsChromosome ret = this.clone();
		for (int i = 0; i < ret.coefficients.size(); i++) {
			if (this.gpc.random.nextDouble() > this.pMutation) {
				double coeff = ret.coefficients.get(i);
				coeff += this.gpc.context.getRandomMutationValue();
				ret.coefficients.set(i, coeff);
			}
		}
		return ret;
	}

	@Override
	@SuppressWarnings({ "CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported" })
	protected GpCoefficientsChromosome clone() {
		final List<Double> clonedCoefficients = new ArrayList<>(this.coefficients);
		return new GpCoefficientsChromosome(gpc, clonedCoefficients, this.pMutation, this.pCrossover);
	}

	public List<Double> getCoefficients() {
		return this.coefficients;
	}
}
