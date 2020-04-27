/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.tim;

import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.tim.expression.TValue;

public interface TMemory {

	public TValue getVariable(String varname);

	public void putVariable(String varname, TValue value, TVariableScope scope) throws EaterException;

	public void removeVariable(String varname);

	public boolean isEmpty();

	public Set<String> variablesNames();

	public Trie variablesNames3();

	public TMemory forkFromGlobal(Map<String, TValue> input);

	public ExecutionContextIf peekIf();

	public boolean areAllIfOk(TContext context, TMemory memory) throws EaterException;

	public void addIf(ExecutionContextIf context);

	public void addWhile(ExecutionContextWhile value);

	public void addForeach(ExecutionContextForeach value);

	public ExecutionContextIf pollIf();

	public ExecutionContextWhile pollWhile();

	public ExecutionContextWhile peekWhile();

	public ExecutionContextForeach pollForeach();

	public ExecutionContextForeach peekForeach();

	public void dumpDebug(String message);

}
