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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GPoint {

	public static final String NORTH_HOOK = "NORTH_HOOK";
	public static final String SOUTH_HOOK = "SOUTH_HOOK";
	public static final String WEST_HOOK = "WEST_HOOK";
	public static final String EAST_HOOK = "EAST_HOOK";

	public static final String NORTH_BORDER = "NORTH_BORDER";
	public static final String SOUTH_BORDER = "SOUTH_BORDER";
	public static final String WEST_BORDER = "WEST_BORDER";
	public static final String EAST_BORDER = "EAST_BORDER";

	private final Gtile gtile;
	private final String name;
	private final LinkRendering linkRendering;

	public GPoint(Gtile gtile, String name, LinkRendering linkRendering) {
		this.gtile = gtile;
		this.name = name;
		this.linkRendering = linkRendering;
	}

	public GPoint(Gtile gtile, String name) {
		this(gtile, name, LinkRendering.none());
	}

	public Gtile getGtile() {
		return gtile;
	}

	public String getName() {
		return name;
	}

	public UTranslate getCoord() {
		return gtile.getCoord(name);
	}

	public Point2D getPoint2D() {
		return getCoord().getPosition();
	}

	public LinkRendering getLinkRendering() {
		return linkRendering;
	}

}
