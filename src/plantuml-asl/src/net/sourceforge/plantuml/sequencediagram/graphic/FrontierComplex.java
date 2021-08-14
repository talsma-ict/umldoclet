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

import java.util.Arrays;
import java.util.Objects;

class FrontierComplex implements Frontier {

	private final double freeY[];

	public FrontierComplex(double freeY, int rangeEnd) {
		this.freeY = new double[rangeEnd + 1];
		for (int i = 0; i <= rangeEnd; i++) {
			this.freeY[i] = freeY;
		}
	}

	private FrontierComplex(double freeY[]) {
		this.freeY = freeY;
	}

	private FrontierComplex(double freeY[], double delta, ParticipantRange range) {
		this(freeY.clone());
		final double newV = getFreeY(range) + delta;
		for (int i = range.start(); i <= range.end(); i++) {
			this.freeY[i] = newV;
		}
	}

	public double getFreeY(ParticipantRange range) {
		Objects.requireNonNull(range);
		double result = freeY[range.start()];
		for (int i = range.start(); i <= range.end(); i++) {
			if (freeY[i] > result) {
				result = freeY[i];
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return Arrays.toString(freeY);
	}

	public FrontierComplex add(double delta, ParticipantRange range) {
		return new FrontierComplex(freeY, delta, Objects.requireNonNull(range));
	}

	FrontierComplex copy() {
		return new FrontierComplex(freeY.clone());
	}
	
	FrontierComplex mergeMax(FrontierComplex other) {
		if (this.freeY.length != other.freeY.length) {
			throw new IllegalArgumentException();
		}
		final FrontierComplex result = new FrontierComplex(new double[freeY.length]);
		for (int i=0; i<freeY.length; i++) {
			result.freeY[i] = Math.max(this.freeY[i], other.freeY[i]);
		}
		return result;
	}

	// public double diff(Frontier other) {
	// return freeY - other.freeY;
	// }

}
