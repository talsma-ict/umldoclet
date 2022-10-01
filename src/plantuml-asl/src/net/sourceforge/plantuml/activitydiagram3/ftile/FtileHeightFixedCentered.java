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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileHeightFixedCentered extends AbstractFtile {

	private final Ftile tile;
	private final double fixedHeight;

	public FtileHeightFixedCentered(Ftile tile, double fixedHeight) {
		super(tile.skinParam());
		this.tile = tile;
		this.fixedHeight = fixedHeight;
	}
	
	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.singleton(tile);
		// return tile.getMyChildren();
	}


	@Override
	public LinkRendering getInLinkRendering() {
		return tile.getInLinkRendering();
	}

	@Override
	public LinkRendering getOutLinkRendering() {
		return tile.getOutLinkRendering();
	}

	public Set<Swimlane> getSwimlanes() {
		return tile.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return tile.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return tile.getSwimlaneOut();
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		return tile.calculateDimension(stringBounder).translate(getTranslate(stringBounder)).fixedHeight(fixedHeight);
	}

	private UTranslate getTranslate(StringBounder stringBounder) {
		final XDimension2D dim = tile.calculateDimension(stringBounder);
		if (dim.getHeight() > fixedHeight) {
			throw new IllegalStateException();
		}
		return UTranslate.dy((fixedHeight - dim.getHeight()) / 2);
	}

	public void drawU(UGraphic ug) {
		ug.apply(getTranslate(ug.getStringBounder())).draw(tile);
	}

}
