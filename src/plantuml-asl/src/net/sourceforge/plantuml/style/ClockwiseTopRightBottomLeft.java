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
package net.sourceforge.plantuml.style;

public class ClockwiseTopRightBottomLeft {

	private final double top;
	private final double right;
	private final double bottom;
	private final double left;

	public static ClockwiseTopRightBottomLeft same(double value) {
		return new ClockwiseTopRightBottomLeft(value, value, value, value);
	}

	public static ClockwiseTopRightBottomLeft none() {
		return new ClockwiseTopRightBottomLeft(0, 0, 0, 0);
	}

	public static ClockwiseTopRightBottomLeft read(String value) {
		if (value.matches("[0-9 ]+")) {
			final String split[] = value.split(" +");
			if (split.length == 1) {
				final double first = Integer.parseInt(split[0]);
				return new ClockwiseTopRightBottomLeft(first, first, first, first);
			}
			if (split.length == 2) {
				final double first = Integer.parseInt(split[0]);
				final double second = Integer.parseInt(split[1]);
				return new ClockwiseTopRightBottomLeft(first, second, first, second);
			}
			if (split.length == 3) {
				final double first = Integer.parseInt(split[0]);
				final double second = Integer.parseInt(split[1]);
				final double third = Integer.parseInt(split[2]);
				return new ClockwiseTopRightBottomLeft(first, second, third, second);
			}
			if (split.length == 4) {
				final double first = Integer.parseInt(split[0]);
				final double second = Integer.parseInt(split[1]);
				final double third = Integer.parseInt(split[2]);
				final double forth = Integer.parseInt(split[3]);
				return new ClockwiseTopRightBottomLeft(first, second, third, forth);
			}
		}
		return none();
	}

	public static ClockwiseTopRightBottomLeft margin1margin2(double margin1, double margin2) {
		return topRightBottomLeft(margin1, margin2, margin1, margin2);
	}

	public static ClockwiseTopRightBottomLeft topRightBottomLeft(double top, double right, double bottom, double left) {
		return new ClockwiseTopRightBottomLeft(top, right, bottom, left);
	}

	private ClockwiseTopRightBottomLeft(double top, double right, double bottom, double left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	@Override
	public String toString() {
		return "" + top + ":" + right + ":" + bottom + ":" + left;
	}

	public final double getTop() {
		return top;
	}

	public final double getRight() {
		return right;
	}

	public final double getBottom() {
		return bottom;
	}

	public final double getLeft() {
		return left;
	}

	public static ClockwiseTopRightBottomLeft marginForDocument(StyleBuilder styleBuilder) {
		final Style style = StyleSignature.of(SName.root, SName.document).getMergedStyle(styleBuilder);
		return style.getMargin();
	}

}
