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

import java.util.Objects;

public class Constraint {

	private final Pushable p1;
	private final Pushable p2;
	private double value;

	public Constraint(Pushable p1, Pushable p2) {
		this.p1 = Objects.requireNonNull(p1);
		this.p2 = Objects.requireNonNull(p2);
	}

	public final Pushable getParticipant1() {
		return p1;
	}

	public final Pushable getParticipant2() {
		return p2;
	}

	public final double getValue() {
		return value;
	}

	public final void ensureValue(double newValue) {
		if (newValue > value) {
			this.value = newValue;
		}
	}

	public void push(double delta) {
		value += delta;
	}

	@Override
	public String toString() {
		return "Constraint " + p1 + " " + p2 + " " + value;
	}

}
