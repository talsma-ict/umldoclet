/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.tim.expression.TValue;

public class TMemoryGlobal extends ConditionalContexts implements TMemory {

	private final Map<String, TVariable> globalVariables = new HashMap<String, TVariable>();
	private final Trie variables = new Trie();

	public TVariable getVariable(String varname) {
		return this.globalVariables.get(varname);
	}

	public void dumpDebug(String message) {
		Log.error("[MemGlobal] Start of memory_dump " + message);
		dumpMemoryInternal();
		Log.error("[MemGlobal] End of memory_dump");
	}

	void dumpMemoryInternal() {
		Log.error("[MemGlobal] Number of variable(s) : " + globalVariables.size());
		for (Entry<String, TVariable> ent : new TreeMap<String, TVariable>(globalVariables).entrySet()) {
			final String name = ent.getKey();
			final TValue value = ent.getValue().getValue();
			Log.error("[MemGlobal] " + name + " = " + value);
		}
	}

	public void putVariable(String varname, TVariable value, TVariableScope scope) throws EaterException {
		Log.info("[MemGlobal] Setting " + varname);
		if (scope == TVariableScope.LOCAL) {
			throw new EaterException("Cannot use local variable here");
		}
		this.globalVariables.put(varname, value);
		this.variables.add(varname);
	}

	public void removeVariable(String varname) {
		this.globalVariables.remove(varname);
		this.variables.remove(varname);
	}

	public boolean isEmpty() {
		return globalVariables.isEmpty();
	}

	public Set<String> variablesNames() {
		return Collections.unmodifiableSet(globalVariables.keySet());
	}

	public Trie variablesNames3() {
		return variables;
	}

	public TMemory forkFromGlobal(Map<String, TVariable> input) {
		return new TMemoryLocal(this, input);
	}

}
