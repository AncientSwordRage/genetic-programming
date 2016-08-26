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

import com.lagodiuk.gp.symbolic.api.Function;
import com.lagodiuk.gp.symbolic.interpreter.Context;
import com.lagodiuk.gp.symbolic.interpreter.Expression;
import java.util.LinkedList;
import java.util.List;

public enum SymbolicRegressionFunctions implements Function
{
	CONSTANT(true,  0, 1, null)
	{
		@Override
		public boolean isNumber()
		{
			return true;
		}
		@Override
		public List<Double> getCoefficients(Expression expression)
		{
			return expression.getCoefficientsOfNode().subList(0, coefficientsCount());
		}
		@Override
		public void setCoefficients(Expression expression, List<Double> coefficients, int startIndex)
		{
			expression.removeCoefficients();
			for(int i = 0; i < coefficientsCount(); i++)
				expression.addCoefficient(coefficients.get(startIndex + i));
		}
		@Override
		public double eval(Expression expression, Context context)
		{
			return expression.getCoefficientsOfNode().get(0);
		}
		@Override
		public String print(Expression expression)
		{
			double retVal = expression.getCoefficientsOfNode().get(0);
			String retStr = String.format("%s", retVal); // prev. was %s!
			// String retStr = String.format("%.3g", retVal); // prev. was %s!
			if(retVal < 0)
				retStr = "(" + retStr + ")";
			return retStr;
		}
	},
	VARIABLE(false, 0, 0, null)
	{
		@Override
		public boolean isVariable()
		{
			return true;
		}
		@Override
		public double eval(Expression expression, Context context)
		{
			return context.lookupVariable(expression.getVariable());
		}
		@Override
		public String print(Expression expression)
		{
			return expression.getVariable();
		}
	},
	ADD     (true,  2, 0, "(%s + %s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double left = childs.get(0).eval(context);
			double right = childs.get(1).eval(context);
			return (left + right);
		}
	},
	SUB     (false, 2, 0, "(%s - %s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double left = childs.get(0).eval(context);
			double right = childs.get(1).eval(context);
			return (left - right);
		}
	},
	MUL     (true,  2, 0, "(%s * %s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double left = childs.get(0).eval(context);
			double right = childs.get(1).eval(context);
			return (left * right);
		}
	},
	DIV     (false, 2, 0, "(%s / %s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double left = childs.get(0).eval(context);
			double right = childs.get(1).eval(context);
			return (left / right);
		}
	},
	SQRT    (true,  1, 0, "sqrt(abs(%s))")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double arg = childs.get(0).eval(context);
			return Math.sqrt(Math.abs(arg));
		}
	},
	POW     (false, 2, 0, "(%s ^ %s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double arg1 = childs.get(0).eval(context);
			double arg2 = childs.get(1).eval(context);
			return Math.pow(arg1, arg2);
		}
	},
	LN      (true,  1, 0, null)
	{
		private final double threshold = 1e-5;
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double arg = childs.get(0).eval(context);
			return Math.log(Math.abs(arg) + this.threshold);
		}
		@Override
		public String print(Expression expression)
		{
			List<Expression> childs = expression.getChilds();
			String arg = childs.get(0).print();
			return String.format("ln(abs(%s) + %s)", arg, this.threshold);
		}
	},
	SIN     (true,  1, 0, "sin(%s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double arg = childs.get(0).eval(context);
			return Math.sin(arg);
		}
	},
	COS     (true,  1, 0, "cos(%s)")
	{
		@Override
		public double eval(Expression expression, Context context)
		{
			List<Expression> childs = expression.getChilds();
			double arg = childs.get(0).eval(context);
			return Math.cos(arg);
		}
	},
	;
	private final boolean commutative;
	private final int     coefficientsCount;
	private final int     argumentsCount;
	private final String  format;
	private SymbolicRegressionFunctions(boolean commutative, int argumentsCount, int coefficientsCount, String format)
	{
		this.commutative       = commutative;
		this.argumentsCount    = argumentsCount;
		this.coefficientsCount = coefficientsCount;
		this.format            = format;
	}
	private SymbolicRegressionFunctions()
	{
		this.commutative       = false;
		this.argumentsCount    = 2;
		this.coefficientsCount = 0;
		this.format            = "(%s)";
	}
	@Override
	public boolean isNumber()
	{
		return false;
	}
	@Override
	public boolean isVariable()
	{
		return false;
	}
	@Override
	public boolean isCommutative()
	{
		return this.commutative;
	}
	@Override
	public int coefficientsCount()
	{
		return this.coefficientsCount;
	}
	@Override
	public int argumentsCount()
	{
		return this.argumentsCount;
	}
	@Override
	public List<Double> getCoefficients(Expression expression)
	{
		return new LinkedList<>();
	}
	@Override
	public void setCoefficients(Expression expression, List<Double> coefficients, int startIndex)
	{
		expression.removeCoefficients();
	}
	@Override
	public String print(Expression expression)
	{
		final List<Expression> childs = expression.getChilds();
		final int              count  = childs.size();
		final String           args[] = new String[count];
		for(int index = 0; index < count; index += 1)
			args[index] = childs.get(index).print();
		return String.format(this.format, (Object[])args);
	}
	/*
	double       eval(Expression expression, Context context);
	int          argumentsCount();
	boolean      isVariable();
	boolean      isNumber();
	boolean      isCommutative();
	String       print(Expression expression);
	List<Double> getCoefficients(Expression expression);
	void         setCoefficients(Expression expression, List<Double> coefficients, int startIndex);
	*/
}
