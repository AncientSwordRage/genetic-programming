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
package com.lagodiuk.gp.symbolic.interpreter;

import com.lagodiuk.gp.symbolic.api.Function;
import com.lagodiuk.gp.symbolic.core.SymbolicRegressionFunctions;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TestUtils {

	public static Context createContext(Function... functions) {
		return new Context(list(functions), list("x"));
	}

	public static Expression variableExpr(String x) {
		return new Expression(SymbolicRegressionFunctions.VARIABLE).setVariable(x);
	}

	public static Expression constantExpr(double value) {
		return new Expression(SymbolicRegressionFunctions.CONSTANT).setCoefficientsOfNode(list(value));
	}

	public static Expression subExpr(Expression left, Expression right) {
		return new Expression(SymbolicRegressionFunctions.SUB).setChilds(list(left, right));
	}

	public static Expression addExpr(Expression left, Expression right) {
		return new Expression(SymbolicRegressionFunctions.ADD).setChilds(list(left, right));
	}

	public static Expression powExpr(Expression left, Expression right) {
		return new Expression(SymbolicRegressionFunctions.POW).setChilds(list(left, right));
	}

	public static <T> List<T> list(T... elements) {
		List<T> list = new LinkedList<>();
		list.addAll(Arrays.asList(elements));
		return list;
	}

	public static <T> Set<T> setFromArray(T... elements) {
		Set<T> list = new HashSet<>();
		list.addAll(Arrays.asList(elements));
		return list;
	}

}
