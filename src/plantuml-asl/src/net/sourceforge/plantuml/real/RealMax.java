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
package net.sourceforge.plantuml.real;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class RealMax extends AbstractReal implements Real {

	private final List<Real> all = new ArrayList<>();
	private final Throwable creationPoint;

	RealMax(Collection<Real> reals) {
		super(line(reals));
		this.all.addAll(reals);
		this.creationPoint = new Throwable();
		this.creationPoint.fillInStackTrace();
	}

	static RealLine line(Collection<Real> reals) {
		return ((AbstractReal) reals.iterator().next()).getLine();
	}

	public String getName() {
		return "max " + all;
	}

	@Override
	double getCurrentValueInternal() {
		double result = all.get(0).getCurrentValue();
		for (int i = 1; i < all.size(); i++) {
			Throwable t = new Throwable();
			t.fillInStackTrace();
			final int stackLength = t.getStackTrace().length;
			if (stackLength > 1000) {
				System.err.println("The faulty RealMax " + getName());
				System.err.println("has been created here:");
				printCreationStackTrace();
				throw new IllegalStateException("Infinite recursion?");
			}
			final double v = all.get(i).getCurrentValue();
			if (v > result) {
				result = v;
			}
		}
		return result;
	}

	public Real addFixed(double delta) {
		return new RealDelta(this, delta);
	}

	public Real addAtLeast(double delta) {
		throw new UnsupportedOperationException();
	}

	public void ensureBiggerThan(Real other) {
		throw new UnsupportedOperationException();
	}

	public void printCreationStackTrace() {
		creationPoint.printStackTrace();
	}

}
