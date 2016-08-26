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

final class GpDefaults
{
	final static int     OPTIMIZING_TREE_ITERATIONS = 50;
	final static int     OPTIMIZING_TREE_MUTATED    = 5;
	final static boolean OPTIMIZING_TREE_ASYNC      = true;
	final static double  OPTIMIZING_TREE_PMUTATION  = 0.6;
	final static double  OPTIMIZING_TREE_PCROSSOVER = 0.8;
}