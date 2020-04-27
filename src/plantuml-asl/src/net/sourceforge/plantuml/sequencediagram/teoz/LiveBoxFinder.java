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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UBackground;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UParamNull;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class LiveBoxFinder implements UGraphic {

	public boolean matchesProperty(String propertyName) {
		return false;
	}

	public double dpiFactor() {
		return 1;
	}

	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new LiveBoxFinder(stringBounder, translate.compose((UTranslate) change));
		} else if (change instanceof UStroke) {
			return new LiveBoxFinder(this);
		} else if (change instanceof UBackground) {
			return new LiveBoxFinder(this);
		} else if (change instanceof HColor) {
			return new LiveBoxFinder(this);
		}
		throw new UnsupportedOperationException();
	}

	private final StringBounder stringBounder;
	private final UTranslate translate;

	public LiveBoxFinder(StringBounder stringBounder) {
		this(stringBounder, new UTranslate());
	}

	private LiveBoxFinder(StringBounder stringBounder, UTranslate translate) {
		this.stringBounder = stringBounder;
		this.translate = translate;
	}

	private LiveBoxFinder(LiveBoxFinder other) {
		this(other.stringBounder, other.translate);
	}

	public StringBounder getStringBounder() {
		return stringBounder;
	}

	public UParam getParam() {
		return new UParamNull();
	}

	public void draw(UShape shape) {
		final double x = translate.getDx();
		final double y = translate.getDy();
		if (shape instanceof GroupingTile) {
			((GroupingTile) shape).drawU(this);
		} else if (shape instanceof TileWithUpdateStairs) {
			((TileWithUpdateStairs) shape).updateStairs(stringBounder, y);
			// } else if (shape instanceof EmptyTile) {
			// // Nothing ?
			// } else if (shape instanceof TileParallel) {
			// // Nothing ?
			// } else if (shape instanceof NotesTile) {
			// // Nothing ?
			// } else if (shape instanceof Tile) {
			// Log.info("OtherTile " + shape);
		} else {
			// Nothing ?
			// throw new UnsupportedOperationException(shape.getClass().getName());
		}
	}

	public ColorMapper getColorMapper() {
		return new ColorMapperIdentity();
	}

	public void startUrl(Url url) {
	}

	public void closeAction() {
	}

	public void flushUg() {
	}

}
