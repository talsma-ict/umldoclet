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
package net.sourceforge.plantuml.project.draw;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UPath;

public class PathUtils {

	public static UPath UtoRight(double width, double height, double round) {
		final double halfRound = round / 2;
		final UPath result = new UPath();
		result.moveTo(0, 0);
		result.lineTo(width - halfRound, 0);
		result.arcTo(new Point2D.Double(width, halfRound), halfRound, 0, 1);
		result.lineTo(width, height - halfRound);
		result.arcTo(new Point2D.Double(width - halfRound, height), halfRound, 0, 1);
		result.lineTo(0, height);
		return result;
	}

	public static UPath UtoLeft(double width, double height, double round) {
		final double halfRound = round / 2;
		final UPath result = new UPath();
		result.moveTo(width, height);
		result.lineTo(halfRound, height);
		result.arcTo(new Point2D.Double(0, height - halfRound), halfRound, 0, 1);
		result.lineTo(0, halfRound);
		result.arcTo(new Point2D.Double(halfRound, 0), halfRound, 0, 1);
		result.lineTo(width, 0);
		return result;
	}

}
