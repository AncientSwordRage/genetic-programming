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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Expression implements Cloneable {

	private List<Expression> childs = new ArrayList<>();

	private List<Double> coefficients = new ArrayList<>();

	private String variable;

	private Function function;

	public Expression(Function function) {
		this.function = function;
	}

	public double eval(Context context) {
		return this.function.eval(this, context);
	}

	public String print() {
		return this.function.print(this);
	}

	public List<Expression> getChilds() {
		return this.childs;
	}

	public Expression setChilds(List<Expression> childs) {
		this.childs = childs;
		return this;
	}

	public void addChild(Expression child) {
		this.childs.add(child);
	}

	public void removeChilds() {
		this.childs.clear();
	}

	public List<Double> getCoefficientsOfNode() {
		return this.coefficients;
	}

	public Expression setCoefficientsOfNode(List<Double> coefficients) {
		this.coefficients = coefficients;
		return this;
	}

	public void addCoefficient(double coefficient) {
		this.coefficients.add(coefficient);
	}

	public void removeCoefficients() {
		if (this.coefficients.size() > 0) {
			this.coefficients.clear();
		}
	}

	public String getVariable() {
		return this.variable;
	}

	public Expression setVariable(String variable) {
		this.variable = variable;
		return this;
	}

	public Function getFunction() {
		return this.function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	@Override
	@SuppressWarnings({ "CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported" })
	public Expression clone() {
		Expression cloned = new Expression(this.function);
		if (this.variable != null) {
			cloned.variable = this.variable;
		}
		for (Expression c : this.childs) {
			cloned.childs.add(c.clone());
		}
		for (Double d : this.coefficients) {
			cloned.coefficients.add(d);
		}
		return cloned;
	}

	public List<Double> getCoefficientsOfTree() {
		LinkedList<Double> result = new LinkedList<>();
		this.getCoefficientsOfTree(result);
		Collections.reverse(result);
		return result;
	}

	private void getCoefficientsOfTree(Deque<Double> coefficients) {
		List<Double> coeffs = this.function.getCoefficients(this);
		for (Double d : coeffs) {
			coefficients.push(d);
		}
		for (int i = 0; i < this.childs.size(); i++) {
			this.childs.get(i).getCoefficientsOfTree(coefficients);
		}
	}

	public void setCoefficientsOfTree(List<Double> coefficients) {
		this.setCoefficientsOfTree(coefficients, 0);
	}

	private int setCoefficientsOfTree(List<Double> coefficients, int index) {
		this.function.setCoefficients(this, coefficients, index);
		index += this.function.coefficientsCount();
		if (this.childs.size() > 0) {
			for (int i = 0; i < this.childs.size(); i++) {
				index = this.childs.get(i).setCoefficientsOfTree(coefficients, index);
			}
		}
		return index;
	}

	public List<Expression> getAllNodesAsList() {
		List<Expression> nodes = new LinkedList<>();
		this.getAllNodesBreadthFirstSearch(nodes);
		return nodes;
	}

	/**
	 * non-recursive Breadth-first iteration over all node of syntax tree
	 */
	private void getAllNodesBreadthFirstSearch(List<Expression> nodesList)
	{
		int index = 0;
		nodesList.add(this);
		for(;;)
			if(index < nodesList.size())
			{
				Expression node = nodesList.get(index++);
				for(Expression child : node.childs)
					nodesList.add(child);
			} else
				break;
	}
}
