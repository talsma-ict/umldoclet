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

import java.util.Deque;
import java.util.LinkedList;

public abstract class ExecutionContexts {

	private final Deque<ExecutionContextIf> allIfs = new LinkedList<ExecutionContextIf>();
	private final Deque<ExecutionContextWhile> allWhiles = new LinkedList<ExecutionContextWhile>();
	private final Deque<ExecutionContextForeach> allForeachs = new LinkedList<ExecutionContextForeach>();

	public void addIf(ExecutionContextIf value) {
		allIfs.addLast(value);
	}

	public void addWhile(ExecutionContextWhile value) {
		allWhiles.addLast(value);
	}

	public void addForeach(ExecutionContextForeach value) {
		allForeachs.addLast(value);
	}

	public ExecutionContextIf peekIf() {
		return allIfs.peekLast();
	}

	public ExecutionContextWhile peekWhile() {
		return allWhiles.peekLast();
	}

	public ExecutionContextForeach peekForeach() {
		return allForeachs.peekLast();
	}

	public ExecutionContextIf pollIf() {
		return allIfs.pollLast();
	}

	public ExecutionContextWhile pollWhile() {
		return allWhiles.pollLast();
	}

	public ExecutionContextForeach pollForeach() {
		return allForeachs.pollLast();
	}

	public boolean areAllIfOk(TContext context, TMemory memory) throws EaterException {
		for (ExecutionContextIf conditionalContext : allIfs) {
			if (conditionalContext.conditionIsOkHere() == false) {
				return false;
			}
		}
		return true;
	}

}
