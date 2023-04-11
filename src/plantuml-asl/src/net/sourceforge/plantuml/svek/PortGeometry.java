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
package net.sourceforge.plantuml.svek;

public final class PortGeometry implements Comparable<PortGeometry> {

	private final String id;
	private final double position;
	private final double height;
	private final int score;

	public PortGeometry(String id, double position, double height, int score) {
		this.id = id;
		this.position = position;
		this.height = height;
		this.score = score;
	}

	public PortGeometry translateY(double deltaY) {
		return new PortGeometry(id, position + deltaY, height, score);
	}

	@Override
	public String toString() {
		return "pos=" + position + " height=" + height + " (" + score + ")";
	}

	public double getHeight() {
		return height;
	}

	public double getPosition() {
		return position;
	}

	public double getLastY() {
		return position + height;
	}

	public int getScore() {
		return score;
	}

	public String getId() {
		return id;
	}

	@Override
	public int compareTo(PortGeometry other) {
		return Double.compare(this.position, other.position);
	}

}
