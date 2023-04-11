/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.real;

class RealImpl extends RealMoveable implements RealOrigin {

	private double currentValue;

	public RealImpl(String name, RealLine line, double currentValue) {
		super(line, name);
		this.currentValue = currentValue;
	}

	void move(double delta) {
		this.currentValue += delta;
	}

	@Override
	double getCurrentValueInternal() {
		return currentValue;
	}

	public Real addAtLeast(double delta) {
		final RealImpl result = new RealImpl(getName() + ".addAtLeast" + delta, getLine(), this.currentValue + delta);
		getLine().addForce(new PositiveForce(this, result, delta));
		return result;
	}

	public void ensureBiggerThan(Real other) {
		getLine().addForce(new PositiveForce(other, this, 0));
	}

	public void compileNow() {
		getLine().compile();
	}
}
