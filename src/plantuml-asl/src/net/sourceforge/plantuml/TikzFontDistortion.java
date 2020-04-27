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

import java.util.StringTokenizer;

public class TikzFontDistortion {

	private final double magnify;
	private final double distortion;

	private TikzFontDistortion(double magnify, double distortion) {
		this.magnify = magnify;
		this.distortion = distortion;
	}

	@Override
	public String toString() {
		return "" + magnify + ";" + distortion;
	}

	public static TikzFontDistortion fromValue(String value) {
		if (value == null) {
			return getDefault();
		}
		final StringTokenizer st = new StringTokenizer(value, ";");
		if (st.hasMoreElements() == false) {
			return getDefault();
		}
		final String v1 = st.nextToken();
		if (st.hasMoreElements() == false) {
			return getDefault();
		}
		final String v2 = st.nextToken();
		if (v1.matches("[\\d.]+") && v2.matches("[-\\d.]+")) {
			return new TikzFontDistortion(Double.parseDouble(v1), Double.parseDouble(v2));
		}
		return getDefault();
	}

	public static TikzFontDistortion getDefault() {
		return new TikzFontDistortion(1.20, 4.0);
	}

	public final double getMagnify() {
		return magnify;
	}

	public final double getDistortion() {
		return distortion;
	}

}
