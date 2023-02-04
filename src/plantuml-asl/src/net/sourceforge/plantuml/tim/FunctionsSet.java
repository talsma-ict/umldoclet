/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.utils.StringLocated;

public class FunctionsSet {

	private final Map<TFunctionSignature, TFunction> functions = new HashMap<TFunctionSignature, TFunction>();
	private final Set<TFunctionSignature> functionsFinal = new HashSet<>();
	private final Trie functions3 = new TrieImpl();
	private TFunctionImpl pendingFunction;

	public TFunction getFunctionSmart(TFunctionSignature searched) {
		final TFunction func = this.functions.get(searched);
		if (func != null) {
			return func;
		}
		for (TFunction candidate : this.functions.values()) {
			if (candidate.getSignature().sameFunctionNameAs(searched) == false) {
				continue;
			}
			if (candidate.canCover(searched.getNbArg(), searched.getNamedArguments())) {
				return candidate;
			}
		}
		return null;
	}

	public int size() {
		return functions().size();
	}

	public Map<TFunctionSignature, TFunction> functions() {
		return Collections.unmodifiableMap(functions);
	}

	public String getLonguestMatchStartingIn(String s) {
		return functions3.getLonguestMatchStartingIn(s);
	}

	public TFunctionImpl pendingFunction() {
		return pendingFunction;
	}

	public void addFunction(TFunction func) {
		if (func.getFunctionType() == TFunctionType.LEGACY_DEFINELONG) {
			((TFunctionImpl) func).finalizeEnddefinelong();
		}
		this.functions.put(func.getSignature(), func);
		this.functions3.add(func.getSignature().getFunctionName() + "(");
	}

	public void executeEndfunction() {
		this.addFunction(this.pendingFunction);
		this.pendingFunction = null;
	}

	public void executeLegacyDefine(TContext context, TMemory memory, StringLocated s)
			throws EaterException, EaterExceptionLocated {
		if (this.pendingFunction != null) {
			throw EaterException.located("already0048");
		}
		final EaterLegacyDefine legacyDefine = new EaterLegacyDefine(s);
		legacyDefine.analyze(context, memory);
		final TFunction function = legacyDefine.getFunction();
		this.functions.put(function.getSignature(), function);
		this.functions3.add(function.getSignature().getFunctionName() + "(");
	}

	public void executeLegacyDefineLong(TContext context, TMemory memory, StringLocated s)
			throws EaterException, EaterExceptionLocated {
		if (this.pendingFunction != null) {
			throw EaterException.located("already0068");
		}
		final EaterLegacyDefineLong legacyDefineLong = new EaterLegacyDefineLong(s);
		legacyDefineLong.analyze(context, memory);
		this.pendingFunction = legacyDefineLong.getFunction();
	}

	public void executeDeclareReturnFunction(TContext context, TMemory memory, StringLocated s)
			throws EaterException, EaterExceptionLocated {
		if (this.pendingFunction != null) {
			throw EaterException.located("already0068");
		}
		final EaterDeclareReturnFunction declareFunction = new EaterDeclareReturnFunction(s);
		declareFunction.analyze(context, memory);
		final boolean finalFlag = declareFunction.getFinalFlag();
		final TFunctionSignature declaredSignature = declareFunction.getFunction().getSignature();
		final TFunction previous = this.functions.get(declaredSignature);
		if (previous != null && (finalFlag || this.functionsFinal.contains(declaredSignature))) {
			throw EaterException.located("This function is already defined");
		}
		if (finalFlag) {
			this.functionsFinal.add(declaredSignature);
		}
		if (declareFunction.getFunction().hasBody()) {
			this.addFunction(declareFunction.getFunction());
		} else {
			this.pendingFunction = declareFunction.getFunction();
		}
	}

	public void executeDeclareProcedure(TContext context, TMemory memory, StringLocated s)
			throws EaterException, EaterExceptionLocated {
		if (this.pendingFunction != null) {
			throw EaterException.located("already0068");
		}
		final EaterDeclareProcedure declareFunction = new EaterDeclareProcedure(s);
		declareFunction.analyze(context, memory);
		final boolean finalFlag = declareFunction.getFinalFlag();
		final TFunctionSignature declaredSignature = declareFunction.getFunction().getSignature();
		final TFunction previous = this.functions.get(declaredSignature);
		if (previous != null && (finalFlag || this.functionsFinal.contains(declaredSignature))) {
			throw EaterException.located("This function is already defined");
		}
		if (finalFlag) {
			this.functionsFinal.add(declaredSignature);
		}
		if (declareFunction.getFunction().hasBody()) {
			this.addFunction(declareFunction.getFunction());
		} else {
			this.pendingFunction = declareFunction.getFunction();
		}
	}

}
