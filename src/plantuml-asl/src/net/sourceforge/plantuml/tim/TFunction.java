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

import java.util.List;

import net.sourceforge.plantuml.tim.expression.TValue;

public interface TFunction {

	public TFunctionSignature getSignature();

	public boolean canCover(int nbArg);

	public TFunctionType getFunctionType();

	public void executeVoid(TContext context, TMemory memory, String s) throws EaterException;

	public TValue executeReturn(TContext context, TMemory memory, List<TValue> args) throws EaterException;

	public void executeVoidInternal(TContext context, TMemory memory, List<TValue> args) throws EaterException;

	public boolean isUnquoted();

}
