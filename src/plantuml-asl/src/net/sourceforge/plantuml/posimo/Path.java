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
package net.sourceforge.plantuml.posimo;

public class Path {

	private final Label label;
	private final Block start;
	private final Block end;
	private final int length;
	private DotPath dotPath;
	private final boolean invis;

	public Path(Block start, Block end, Label label) {
		this(start, end, label, 2, false);
	}

	public Path(Block start, Block end, Label label, int length, boolean invis) {
		if (start == null || end == null) {
			throw new IllegalArgumentException();
		}
		if (length < 1) {
			throw new IllegalArgumentException("length=" + length);
		}
		this.invis = invis;
		this.start = start;
		this.end = end;
		this.label = label;
		this.length = length;
	}

	public final Label getLabel() {
		return label;
	}

	public final Block getStart() {
		return start;
	}

	public final Block getEnd() {
		return end;
	}

	public void setLabelPositionCenter(double labelX, double labelY) {
		label.setCenterX(labelX);
		label.setCenterY(labelY);
	}

	public void setLabelPosition(double x, double y) {
		label.setX(x);
		label.setY(y);
	}

	public void setDotPath(DotPath dotPath) {
		this.dotPath = dotPath;

	}

	public final DotPath getDotPath() {
		return dotPath;
	}

	public int getLength() {
		return length;
	}

	public final boolean isInvis() {
		return invis;
	}

}
