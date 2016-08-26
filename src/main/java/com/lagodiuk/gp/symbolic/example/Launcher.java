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
package com.lagodiuk.gp.symbolic.example;

import com.lagodiuk.gp.symbolic.core.SymbolicRegressionEngine;
import com.lagodiuk.gp.symbolic.core.SymbolicRegressionFunctions;
import com.lagodiuk.gp.symbolic.interpreter.Context;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import com.lagodiuk.gp.symbolic.interpreter.ExpressionFitness;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Launcher
{
	public static class TestExpressionFitnessX implements ExpressionFitness
	{
		@Override
		public double fitness(Expression expression, Context context)
		{
			double delt = 0;
			for(int i = -20; i < 20; i++)
			{
				context.setVariable("x", i);
				double target = (i * i * i * 5) + i + 10;
				// double target = ((((3 * i * i * i) - (i * i * 7)) + (i * 10)) -
				// 35) * i;
				// double target = i * i;
				// double target = ( i + 100 ) * ( i - 100 );
				// double target = i + 6;
				// double target = (Math.cos((i * Math.PI) / 10) * 10) + i;
				// (2)^(SIN(A2*5)*3)+A2
				// double target = Math.pow(2, Math.sin(i * 5) * 3) + i;
				double x = target - expression.eval(context);
				delt += x * x;
			}
			return delt;
		}
	}
	public static class TestExpressionFitnessXY implements ExpressionFitness
	{
		@Override
		public double fitness(Expression expression, Context context)
		{
			double delt = 0;
			for(int x = -10; x < 10; x += 2)
			{
				context.setVariable("x", x);
				for(int y = -10; y < 10; y += 2)
				{
					context.setVariable("y", y);
					// double target = (x * 5) + (y * (y - 4));
					// double target = x + y;
					// double target = (x * 5) + (y * (y - 4)) + (x * y);
					// double target = x * x;
					double target = (x * x) + (y * y);
					double val = target - expression.eval(context);
					delt += val * val;
				}
			}
			return delt;
		}
	}
	private static <T> List<T> list(T... items)
	{
		return new LinkedList<>(Arrays.asList(items));
	}
	public static double prevFitValue = -1.0;
	public static void main(String[] args)
	{
		final SymbolicRegressionEngine sre = new SymbolicRegressionEngine(
			new TestExpressionFitnessXY(),
			list("x", "y"),
			list(SymbolicRegressionFunctions.values()));

		sre.addIterationListener((SymbolicRegressionEngine engine) ->
		{
			final Expression best = sre.getBestSyntaxTree();
			final double     fit  = sre.getFitness(best);

			if(Double.compare(fit, prevFitValue) != 0)
				System.out.println("Func = " + best.print());
			System.out.println(String.format("%s \t %s", engine.getIteration(), fit));
			prevFitValue = fit;
			if(fit < 10)
				engine.terminate();
		});

		sre.evolve(200);

		System.out.println(sre.getBestSyntaxTree().print());
	}
}
