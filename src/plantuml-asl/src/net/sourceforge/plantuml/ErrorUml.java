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
package net.sourceforge.plantuml;

public class ErrorUml {

	private final String error;
	private final ErrorUmlType type;
	private final LineLocation lineLocation;
	private final int score;

	public ErrorUml(ErrorUmlType type, String error, int score, LineLocation lineLocation) {
		if (error == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.score = score;
		this.error = error;
		this.type = type;
		this.lineLocation = lineLocation;
	}

	public int score() {
		return score;
	}

	@Override
	public boolean equals(Object obj) {
		final ErrorUml this2 = (ErrorUml) obj;
		return this.type == this2.type && this.getPosition() == this2.getPosition() && this.error.equals(this2.error);
	}

	@Override
	public int hashCode() {
		return error.hashCode() + type.hashCode() + getPosition();
	}

	@Override
	public String toString() {
		return type.toString() + " " + getPosition() + " " + error;
	}

	public final String getError() {
		return error;
	}

	public final ErrorUmlType getType() {
		return type;
	}

	public final int getPosition() {
		return lineLocation.getPosition();
	}

	public final LineLocation getLineLocation() {
		return lineLocation;
	}

}
