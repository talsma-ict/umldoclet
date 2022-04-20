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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.plantuml.ugraphic.UTranslate;

public class WormMutation {

	private final List<UTranslate> translations = new ArrayList<>();

	private WormMutation() {

	}

	public static WormMutation create(Worm worm, double delta) {
		final String signature = worm.getDirectionsCode();
		final String definition = getDefinition(signature);
		if (definition == null) {
			return createFromLongSignature(signature, delta);
		}
		return new WormMutation(definition, delta);
	}

	private static WormMutation createFromLongSignature(final String signature, final double delta) {
		final WormMutation result = new WormMutation();
		for (int i = 0; i < signature.length() - 1; i++) {
			WormMutation tmp = new WormMutation(getDefinition(signature.substring(i, i + 2)), delta);
			if (i == 0) {
				result.translations.add(tmp.translations.get(0));
			} else {
				UTranslate last = result.getLast();
				if (last.isAlmostSame(tmp.translations.get(0)) == false) {
					tmp = tmp.reverse();
				}
			}
			result.translations.add(tmp.translations.get(1));
			if (i == signature.length() - 2) {
				result.translations.add(tmp.translations.get(2));
			}
		}
		return result;
	}

	private WormMutation reverse() {
		final WormMutation result = new WormMutation();
		for (UTranslate tr : translations) {
			result.translations.add(tr.reverse());
		}
		return result;
	}

	public UTranslate getLast() {
		return translations.get(translations.size() - 1);
	}

	public UTranslate getFirst() {
		return translations.get(0);
	}

	public int size() {
		return translations.size();
	}

	private static String getDefinition(final String signature) {
		if (signature.equals("D") || signature.equals("U")) {
			return "33";
		} else if (signature.equals("L") || signature.equals("R")) {
			return "55";
		} else if (signature.equals("RD")) {
			return "123";
		} else if (signature.equals("RU")) {
			return "543";
		} else if (signature.equals("LD")) {
			return "187";
		} else if (signature.equals("DL")) {
			return "345";
		} else if (signature.equals("DR")) {
			return "765";
		} else if (signature.equals("UL")) {
			return "321";
		} else if (signature.equals("UR")) {
			return "781";
			// } else if (signature.equals("DLD")) {
			// return "3443";
		}
		return null;
	}

	private WormMutation(String definition, double delta) {
		for (int i = 0; i < Objects.requireNonNull(definition).length(); i++) {
			this.translations.add(translation(Integer.parseInt(definition.substring(i, i + 1)), delta));
		}

	}

	private static UTranslate translation(int type, double delta) {
		switch (type) {
		case 1:
			return UTranslate.dy(-delta);
		case 2:
			return new UTranslate(delta, -delta);
		case 3:
			return UTranslate.dx(delta);
		case 4:
			return new UTranslate(delta, delta);
		case 5:
			return UTranslate.dy(delta);
		case 6:
			return new UTranslate(-delta, delta);
		case 7:
			return UTranslate.dx(-delta);
		case 8:
			return new UTranslate(-delta, -delta);
		}
		throw new IllegalArgumentException();
	}

	static private class MinMax {

		private double min = Double.MAX_VALUE;
		private double max = Double.MIN_VALUE;

		private void append(double v) {
			if (v > max) {
				max = v;
			}
			if (v < min) {
				min = v;
			}
		}

		private double getExtreme() {
			if (Math.abs(max) > Math.abs(min)) {
				return max;
			}
			return min;
		}

	}

	public UTranslate getTextTranslate(int size) {
		final MinMax result = new MinMax();
		for (UTranslate tr : translations) {
			result.append(tr.getDx());
		}
		return UTranslate.dx(result.getExtreme() * (size - 1));
	}

	public boolean isDxNegative() {
		return translations.get(0).getDx() < 0;
	}

	public Worm mute(Worm original) {
		final Worm result = new Worm(original.getStyle());
		for (int i = 0; i < original.size(); i++) {
			result.addPoint(translations.get(i).getTranslated(original.get(i)));
		}
		return result;
	}

}
