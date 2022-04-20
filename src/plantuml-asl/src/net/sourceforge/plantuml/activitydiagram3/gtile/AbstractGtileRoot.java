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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public abstract class AbstractGtileRoot extends AbstractTextBlock implements Gtile {

	protected final StringBounder stringBounder;
	private final ISkinParam skinParam;

	public AbstractGtileRoot(StringBounder stringBounder, ISkinParam skinParam) {
		this.stringBounder = stringBounder;
		this.skinParam = skinParam;
	}

	@Override
	final public StringBounder getStringBounder() {
		return stringBounder;
	}

	final public ISkinParam skinParam() {
		if (skinParam == null) {
			throw new IllegalStateException();
		}
		return skinParam;
	}

	final public HColorSet getIHtmlColorSet() {
		return skinParam.getIHtmlColorSet();
	}

	@Override
	final public GPoint getGPoint(String name) {
		if (name.equals(GPoint.NORTH_HOOK) || name.equals(GPoint.SOUTH_HOOK) || name.equals(GPoint.WEST_HOOK)
				|| name.equals(GPoint.EAST_HOOK) || name.equals(GPoint.NORTH_BORDER) || name.equals(GPoint.SOUTH_BORDER)
				|| name.equals(GPoint.WEST_BORDER) || name.equals(GPoint.EAST_BORDER))
			return new GPoint(this, name);
		throw new UnsupportedOperationException();
	}

	@Override
	public final UTranslate getCoord(String name) {
		if (name.equals(GPoint.NORTH_BORDER)) {
			final UTranslate tmp = getCoordImpl(GPoint.NORTH_HOOK);
			return new UTranslate(tmp.getDx(), 0);
		}
		if (name.equals(GPoint.SOUTH_BORDER)) {
			final UTranslate tmp = getCoordImpl(GPoint.SOUTH_HOOK);
			return new UTranslate(tmp.getDx(), calculateDimension(stringBounder).getHeight());
		}
		if (name.equals(GPoint.WEST_BORDER)) {
			final UTranslate tmp = getCoordImpl(GPoint.WEST_HOOK);
			return new UTranslate(0, tmp.getDy());
		}
		if (name.equals(GPoint.EAST_BORDER)) {
			final UTranslate tmp = getCoordImpl(GPoint.EAST_HOOK);
			return new UTranslate(calculateDimension(stringBounder).getWidth(), tmp.getDy());
		}
		return getCoordImpl(name);
	}

	abstract protected UTranslate getCoordImpl(String name);

	@Override
	final public void drawU(UGraphic ug) {
		drawUInternal(ug);
		for (GConnection connection : getInnerConnections()) {
			ug.draw(connection);
		}

	}

	abstract protected void drawUInternal(UGraphic ug);

}
