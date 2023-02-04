/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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

import java.awt.Font;
import java.util.Objects;

import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class ValueImpl implements Value {

	private final DarkString value;

	public static ValueImpl dark(String value, AutomaticCounter counter) {
		return new ValueImpl(new DarkString(null, Objects.requireNonNull(value), counter.getNextInt()));
	}

	public static ValueImpl regular(String value, AutomaticCounter counter) {
		return new ValueImpl(new DarkString(Objects.requireNonNull(value), null, counter.getNextInt()));
	}

	public static ValueImpl regular(String value, int priority) {
		return new ValueImpl(new DarkString(Objects.requireNonNull(value), null, priority));
	}

	public Value mergeWith(Value other) {
		if (other == null)
			return this;
		if (other instanceof ValueImpl)
			return new ValueImpl(value.mergeWith(((ValueImpl) other).value));
		if (other instanceof ValueColor) {
			if (other.getPriority() > getPriority())
				return other;
			return this;
		}
		throw new UnsupportedOperationException();
	}

	private ValueImpl(DarkString value) {
		this.value = value;
	}

	public Value addPriority(int delta) {
		return new ValueImpl(value.addPriority(delta));
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public String asString() {
		return value.getValue1();
	}

	public HColor asColor(HColorSet set) {
		final String value1 = value.getValue1();
		if ("none".equalsIgnoreCase(value1))
			return HColors.transparent();

		if ("transparent".equalsIgnoreCase(value1))
			return HColors.transparent();

		if (value1 == null)
			throw new IllegalArgumentException(value.toString());

		final HColor result = set.getColorOrWhite(value1);
		if (value.getValue2() != null) {
			final HColor dark = set.getColorOrWhite(value.getValue2());
			return result.withDark(dark);
		}
		return result;
	}

	public boolean asBoolean() {
		return "true".equalsIgnoreCase(value.getValue1());
	}

	public int asInt(boolean minusOneIfError) {
		String s = value.getValue1();
		s = s.replaceAll("[^0-9]", "");
		if (s.length() == 0)
			return minusOneIfError ? -1 : 0;
		return Integer.parseInt(s);
	}

	public double asDouble() {
		String s = value.getValue1();
		s = s.replaceAll("[^.0-9]", "");
		return Double.parseDouble(s);
	}

	public int asFontStyle() {
		if (value.getValue1().equalsIgnoreCase("bold"))
			return Font.BOLD;

		if (value.getValue1().equalsIgnoreCase("italic"))
			return Font.ITALIC;

		return Font.PLAIN;
	}

	public HorizontalAlignment asHorizontalAlignment() {
		return HorizontalAlignment.fromString(asString());
	}

	public int getPriority() {
		return value.getPriority();
	}

}
