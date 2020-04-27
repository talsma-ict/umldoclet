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
package net.sourceforge.plantuml.ugraphic;

public class UStroke implements UChange {

	private final double dashVisible;
	private final double dashSpace;
	private final double thickness;

	@Override
	public String toString() {
		return "" + dashVisible + "-" + dashSpace + "-" + thickness;
	}

	public UStroke(double dashVisible, double dashSpace, double thickness) {
		this.dashVisible = dashVisible;
		this.dashSpace = dashSpace;
		this.thickness = thickness;
	}

	public UStroke(double thickness) {
		this(0, 0, thickness);
	}

	public UStroke() {
		this(1.0);
	}

	public UStroke onlyThickness() {
		return new UStroke(thickness);
	}

	private UStroke applyThickness(UStroke thickness) {
		if (thickness == null) {
			return this;
		}
		return new UStroke(dashVisible, dashSpace, thickness.thickness);
	}

	public double getDashVisible() {
		return dashVisible;
	}

	public double getDashSpace() {
		return dashSpace;
	}

	public double getThickness() {
		return thickness;
	}

	public String getDasharraySvg() {
		if (dashVisible == 0) {
			return null;
		}
		return "" + dashVisible + "," + dashSpace;
	}

	public String getDashTikz() {
		if (dashVisible == 0) {
			return null;
		}
		return "on " + dashVisible + "pt off " + dashSpace + "pt";
	}

	// public String getDasharrayEps() {
	// if (dashVisible == 0) {
	// return null;
	// }
	// return "" + dashVisible + " " + dashSpace;
	// }

}
