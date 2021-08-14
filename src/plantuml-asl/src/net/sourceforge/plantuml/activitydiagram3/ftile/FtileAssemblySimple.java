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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileAssemblySimple extends AbstractTextBlock implements Ftile {

	private final Ftile tile1;
	private final Ftile tile2;

	@Override
	public String toString() {
		return "FtileAssemblySimple " + tile1 + " && " + tile2;
	}

	public FtileAssemblySimple(Ftile tile1, Ftile tile2) {
		this.tile1 = tile1;
		this.tile2 = tile2;
	}

	public Swimlane getSwimlaneIn() {
		return tile1.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return tile2.getSwimlaneOut();
	}

	private final Map<Ftile, UTranslate> cachedTranslation = new HashMap<Ftile, UTranslate>();

	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		UTranslate result = cachedTranslation.get(child);
		if (result == null) {
			result = getTranslateForSlow(child, stringBounder);
			cachedTranslation.put(child, result);
		}
		return result;
	}

	private UTranslate getTranslateForSlow(Ftile child, StringBounder stringBounder) {
		if (child == tile1) {
			return getTranslated1(stringBounder);
		}
		if (child == tile2) {
			return getTranslated2(stringBounder);
		}
		UTranslate tmp = tile1.getTranslateFor(child, stringBounder);
		if (tmp != null) {
			return tmp.compose(getTranslated1(stringBounder));
		}
		tmp = tile2.getTranslateFor(child, stringBounder);
		if (tmp != null) {
			return tmp.compose(getTranslated2(stringBounder));
		}
		throw new UnsupportedOperationException();
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug.apply(getTranslateFor(tile1, stringBounder)).draw(tile1);
		ug.apply(getTranslateFor(tile2, stringBounder)).draw(tile2);
	}

	public LinkRendering getInLinkRendering() {
		return tile1.getInLinkRendering();
	}

	public LinkRendering getOutLinkRendering() {
		return LinkRendering.none();
	}

	private FtileGeometry calculateDimension;

	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		if (calculateDimension == null) {
			calculateDimension = tile1.calculateDimension(stringBounder)
					.appendBottom(tile2.calculateDimension(stringBounder));
		}
		return calculateDimension;
	}

	private UTranslate getTranslated1(StringBounder stringBounder) {
		final double left = calculateDimension(stringBounder).getLeft();
		return UTranslate.dx(left - tile1.calculateDimension(stringBounder).getLeft());
	}

	private UTranslate getTranslated2(StringBounder stringBounder) {
		final Dimension2D dim1 = tile1.calculateDimension(stringBounder);
		final double left = calculateDimension(stringBounder).getLeft();
		return new UTranslate(left - tile2.calculateDimension(stringBounder).getLeft(), dim1.getHeight());
	}

	public Collection<Connection> getInnerConnections() {
		return Collections.emptyList();
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		result.addAll(tile1.getSwimlanes());
		result.addAll(tile2.getSwimlanes());
		return Collections.unmodifiableSet(result);
	}

	public ISkinParam skinParam() {
		return tile1.skinParam();
	}

	public UStroke getThickness() {
		return tile1.getThickness();
	}

	public List<WeldingPoint> getWeldingPoints() {
		final List<WeldingPoint> result = new ArrayList<>(tile1.getWeldingPoints());
		result.addAll(tile2.getWeldingPoints());
		return Collections.unmodifiableList(result);
	}

	public Collection<Ftile> getMyChildren() {
		return Arrays.asList(tile1, tile2);
	}

	public HorizontalAlignment arrowHorizontalAlignment() {
		return tile1.arrowHorizontalAlignment();
	}

}
