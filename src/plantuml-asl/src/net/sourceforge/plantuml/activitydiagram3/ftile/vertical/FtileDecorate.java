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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.WeldingPoint;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public abstract class FtileDecorate extends AbstractTextBlock implements Ftile {

	final private Ftile ftile;

	public FtileDecorate(final Ftile ftile) {
		this.ftile = ftile;
	}

	@Override
	public String toString() {
		return "" + getClass() + " " + ftile;
	}

	@Override
	public LinkRendering getOutLinkRendering() {
		return ftile.getOutLinkRendering();
	}

	@Override
	public LinkRendering getInLinkRendering() {
		return ftile.getInLinkRendering();
	}

	@Override
	public void drawU(UGraphic ug) {
		ftile.drawU(ug);
	}

	@Override
	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		return ftile.calculateDimension(stringBounder);
	}

	@Override
	public Collection<Connection> getInnerConnections() {
		return ftile.getInnerConnections();
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		return ftile.getSwimlanes();
	}

	@Override
	public Swimlane getSwimlaneIn() {
		return ftile.getSwimlaneIn();
	}

	@Override
	public Swimlane getSwimlaneOut() {
		return ftile.getSwimlaneOut();
	}

	@Override
	public ISkinParam skinParam() {
		return ftile.skinParam();
	}

	@Override
	public UStroke getThickness(Style style) {
		return ftile.getThickness(style);
	}

	protected final Ftile getFtileDelegated() {
		return ftile;
	}

	@Override
	public List<WeldingPoint> getWeldingPoints() {
		return ftile.getWeldingPoints();
	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (child == ftile)
			return new UTranslate();

		return ftile.getTranslateFor(child, stringBounder);
	}

	@Override
	public Collection<Ftile> getMyChildren() {
		if (this == ftile)
			throw new IllegalStateException();

		return Collections.singleton(ftile);
	}

	@Override
	public HorizontalAlignment arrowHorizontalAlignment() {
		return ftile.arrowHorizontalAlignment();
	}

}
