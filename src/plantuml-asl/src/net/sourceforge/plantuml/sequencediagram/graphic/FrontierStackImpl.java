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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FrontierStackImpl implements FrontierStack {

	class Stack {
		final private FrontierComplex current;
		final private FrontierComplex envelop;

		Stack(FrontierComplex current) {
			this(current, null);
		}

		private Stack(FrontierComplex current, FrontierComplex envelop) {
			this.current = current;
			this.envelop = envelop;
		}

		Stack addEnvelop(FrontierComplex env) {
			if (this.envelop == null) {
				return new Stack(this.current, env);
			}
			return new Stack(this.current, this.envelop.mergeMax(env));
		}
	}

	final private List<Stack> all;

	public FrontierStackImpl(double freeY, int rangeEnd) {
		final Stack s = new Stack(new FrontierComplex(freeY, rangeEnd));
		all = Collections.singletonList(s);
	}

	private FrontierStackImpl(List<Stack> all) {
		this.all = Collections.unmodifiableList(all);
	}

	private FrontierComplex getLast() {
		return all.get(all.size() - 1).current;
	}

	public double getFreeY(ParticipantRange range) {
		return getLast().getFreeY(range);
	}

	public FrontierStackImpl add(double delta, ParticipantRange range) {
		final List<Stack> result = new ArrayList<>(all);
		final Stack s = new Stack(getLast().add(delta, range));
		result.set(result.size() - 1, s);
		return new FrontierStackImpl(result);
	}

	public FrontierStack openBar() {
		final List<Stack> result = new ArrayList<>(all);
		final Stack s = new Stack(getLast().copy());
		result.add(s);
		return new FrontierStackImpl(result);
	}

	public FrontierStack restore() {
		final List<Stack> result = new ArrayList<>(all);
		final Stack openedBar = result.get(result.size() - 2);
		final Stack lastStack = result.get(result.size() - 1);
		result.set(result.size() - 2, openedBar.addEnvelop(lastStack.current));
		result.remove(result.size() - 1);
		final Stack s = new Stack(openedBar.current.copy());
		result.add(s);
		return new FrontierStackImpl(result);
	}

	public FrontierStack closeBar() {
		final List<Stack> result = new ArrayList<>(all);
		final Stack openedBar = result.get(result.size() - 2);
		final Stack lastStack = result.get(result.size() - 1);
		final Stack merge = openedBar.addEnvelop(lastStack.current);
		result.set(result.size() - 2, new Stack(merge.envelop));
		result.remove(result.size() - 1);
		return new FrontierStackImpl(result);
	}

	public FrontierStackImpl copy() {
		// return new FrontierStackImpl(all);
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "nb=" + all.size() + " " + getLast().toString();
	}

}
