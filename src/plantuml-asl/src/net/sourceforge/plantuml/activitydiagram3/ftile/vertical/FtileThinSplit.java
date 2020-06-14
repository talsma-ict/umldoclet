/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileThinSplit extends AbstractFtile {

	private double width;
	private double first;
	private double last;
	private final double height = 1.5;
	private final HtmlColor colorBar;
	private final Swimlane swimlane;

	public FtileThinSplit(ISkinParam skinParam, HtmlColor colorBar, Swimlane swimlane) {
		super(skinParam);
		this.colorBar = colorBar;
		this.swimlane = swimlane;
	}

	public void setGeom(double first, double last, double width) {
		this.width = width;
		this.first = first;
		this.last = last;
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		return new FtileGeometry(width, height, width / 2, 0, height);
	}

	public void drawU(UGraphic ug) {
		final UShape rect = new ULine(last - first, 0);
		ug = ug.apply(new UTranslate(first, 0));
		ug.apply(new UChangeColor(colorBar)).apply(new UStroke(1.5)).draw(rect);
	}

	public Set<Swimlane> getSwimlanes() {
		return Collections.singleton(swimlane);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

}
