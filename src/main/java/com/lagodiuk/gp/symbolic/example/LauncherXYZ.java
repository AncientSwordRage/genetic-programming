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

import com.lagodiuk.gp.symbolic.TabulatedFunctionFitness;
import com.lagodiuk.gp.symbolic.api.Target;
import com.lagodiuk.gp.symbolic.api.SymbolicRegressionIterationListener;
import com.lagodiuk.gp.symbolic.core.SymbolicRegressionEngine;
import com.lagodiuk.gp.symbolic.core.SymbolicRegressionFunctions;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LauncherXYZ {

	/**
	 * Modified example from book "Programming collective intelligence"
	 */
	public static void main(String[] args) {
		TabulatedFunctionFitness fitnessFunction =
				new TabulatedFunctionFitness(
						new Target().when("x", 26).when("y", 35).when("z", 1).targetIs(830),
						new Target().when("x", 8).when("y", 24).when("z", -11).targetIs(130),
						new Target().when("x", 20).when("y", 1).when("z", 10).targetIs(477),
						new Target().when("x", 33).when("y", 11).when("z", 2).targetIs(1217),
						new Target().when("x", 37).when("y", 16).when("z", 7).targetIs(1524));
		SymbolicRegressionEngine engine =
				new SymbolicRegressionEngine(
						fitnessFunction,
						list("x", "y", "z"),
						list(SymbolicRegressionFunctions.ADD, SymbolicRegressionFunctions.SUB, SymbolicRegressionFunctions.MUL, SymbolicRegressionFunctions.VARIABLE, SymbolicRegressionFunctions.CONSTANT));

		addListener(engine);

		engine.evolve(200);
		System.out.println(engine.getBestSyntaxTree().print());
	}

	private static void addListener(SymbolicRegressionEngine engine) {
		engine.addIterationListener(new SymbolicRegressionIterationListener() {
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
				if (fit < 5) {
					engine.terminate();
				}
			}
		});
	}

	private static <T> List<T> list(T... items) {
		List<T> list = new LinkedList<>();
		list.addAll(Arrays.asList(items));
		return list;
	}

}
