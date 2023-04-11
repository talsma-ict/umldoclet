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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RealLine {

	private final List<PositiveForce> forces = new ArrayList<>();

	private double min;
	private double max;
	private Set<AbstractReal> all = new HashSet<>();

	void register(double v) {
		// System.err.println("RealLine::register " + v);
		// min = Math.min(min, v);
		// max = Math.max(max, v);
	}

	void register2(AbstractReal abstractReal) {
		all.add(abstractReal);
	}

	public double getAbsoluteMin() {
		return min;
	}

	public double getAbsoluteMax() {
		return max;
	}

	public void addForce(PositiveForce force) {
		this.forces.add(force);
	}

	static private int CPT;

	public void compile() {
		int cpt = 0;
		final Map<PositiveForce, Integer> counter = new HashMap<PositiveForce, Integer>();
		do {
			boolean done = true;
			for (PositiveForce f : forces) {
				// System.err.println("force=" + f);
				final boolean change = f.apply();
				if (change) {
					incCounter(counter, f);
					// System.err.println("changed! " + f);
					done = false;
				}
			}
			if (done) {
				// System.err.println("cpt=" + cpt + " size=" + forces.size());
				CPT += cpt;
				// System.err.println("CPT=" + CPT);
				min = 0;
				max = 0;
				for (AbstractReal real : all) {
					final double v = real.getCurrentValue();
					// System.err.println("RealLine::compile v=" + v);
					if (v > max)
						max = v;

					if (v < min)
						min = v;

				}
				// System.err.println("RealLine::compile min=" + min + " max=" + max);
				return;
			}
			cpt++;
			if (cpt > 99999) {
				printCounter(counter);
				throw new IllegalStateException("Inifinite Loop?");
			}
		} while (true);

	}

	private void printCounter(Map<PositiveForce, Integer> counter) {
		for (PositiveForce f : forces)
			System.err.println("force=" + f);

		for (Map.Entry<PositiveForce, Integer> ent : counter.entrySet())
			System.err.println("count=" + ent.getValue() + " for " + ent.getKey());

	}

	private static void incCounter(Map<PositiveForce, Integer> counter, PositiveForce f) {
		final Integer v = counter.get(f);
		counter.put(f, v == null ? 1 : v + 1);
	}

	Real asMaxAbsolute() {
		return new MaxAbsolute();
	}

	Real asMinAbsolute() {
		return new MinAbsolute();
	}

	class MaxAbsolute extends AbstractAbsolute {
		public double getCurrentValue() {
			return max;
		}
	}

	class MinAbsolute extends AbstractAbsolute {
		public double getCurrentValue() {
			return min;
		}
	}

	abstract class AbstractAbsolute implements Real {

		public void printCreationStackTrace() {
		}

		public String getName() {
			return getClass().getName();
		}

		public Real addFixed(double delta) {
			throw new UnsupportedOperationException();
		}

		public Real addAtLeast(double delta) {
			throw new UnsupportedOperationException();
		}

		public void ensureBiggerThan(Real other) {
			throw new UnsupportedOperationException();
		}

		public Real getMaxAbsolute() {
			return asMaxAbsolute();
		}

		public Real getMinAbsolute() {
			return asMinAbsolute();
		}

	}

}
