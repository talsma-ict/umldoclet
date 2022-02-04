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

public enum PName {
	Shadowing, //
	FontName, //
	FontColor, //
	DARK_FontColor, //
	FontSize, //
	FontStyle, //
	BackGroundColor, //
	DARK_BackGroundColor, //
	RoundCorner, //
	LineThickness, //
	DiagonalCorner, //
	HyperLinkColor, //
	DARK_HyperLinkColor, //
	LineColor, //
	DARK_LineColor, //
	LineStyle, //
	Padding, //
	Margin, //
	MaximumWidth, //
	MinimumWidth, //
	ExportedName, //
	Image, //
	HorizontalAlignment, //
	ShowStereotype, //
	ImagePosition;

	public static PName getFromName(String name, StyleScheme scheme) {
		for (PName prop : values()) {
			if (prop.name().equalsIgnoreCase(name)) {
				if (scheme == StyleScheme.DARK)
					return dark(prop);
				return prop;
			}
		}
		return null;
	}

	private static PName dark(PName name) {
		switch (name) {
		case FontColor:
			return DARK_FontColor;
		case BackGroundColor:
			return DARK_BackGroundColor;
		case HyperLinkColor:
			return DARK_HyperLinkColor;
		case LineColor:
			return DARK_LineColor;
		default:
			return name;
		}
	}

}
