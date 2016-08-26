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
package com.lagodiuk.gp.symbolic.example;

import com.lagodiuk.gp.symbolic.api.SymbolicRegressionIterationListener;
import com.lagodiuk.gp.symbolic.core.SymbolicRegressionEngine;
import com.lagodiuk.gp.symbolic.core.SymbolicRegressionFunctions;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Launcher {

	public static void main(String[] args) {
		SymbolicRegressionEngine sr = new SymbolicRegressionEngine(new TestExpressionFitness2(), list("x", "y"), list(SymbolicRegressionFunctions.values()));

		sr.addIterationListener(new SymbolicRegressionIterationListener() {
			private double prevFitValue = -1;

			@Override
			public void onNewGeneration(SymbolicRegressionEngine engine) {
				final SymbolicRegressionEngine sre  = (SymbolicRegressionEngine)engine;
				final Expression               best = sre.getBestSyntaxTree();
				final double                   fit  = sre.getFitness(best);
				
				if (Double.compare(fit, this.prevFitValue) != 0) {
					System.out.println("Func = " + best.print());
				}
				System.out.println(String.format("%s \t %s", engine.getIteration(), fit));
				this.prevFitValue = fit;
				if (fit < 10) {
					engine.terminate();
				}
			}
		});

		sr.evolve(200);
		System.out.println(sr.getBestSyntaxTree().print());
	}

	private static <T> List<T> list(T... items) {
		List<T> list = new LinkedList<>();
		list.addAll(Arrays.asList(items));
		return list;
	}
}
