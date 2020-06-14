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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamBackcolored;
import net.sourceforge.plantuml.SkinParamBackcoloredReference;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.Reference;
import net.sourceforge.plantuml.skin.rose.Rose;

public class TileArguments implements Bordered {
	private final StringBounder stringBounder;
	private final Real origin;
	private final LivingSpaces livingSpaces;
	private final Rose skin;
	private final ISkinParam skinParam;

	public TileArguments(StringBounder stringBounder, LivingSpaces livingSpaces, Rose skin, ISkinParam skinParam,
			Real origin) {
		this.stringBounder = stringBounder;
		this.origin = origin;
		this.livingSpaces = livingSpaces;
		this.skin = skin;
		this.skinParam = skinParam;
	}

	public TileArguments withBackColorGeneral(HtmlColor backColorElement, HtmlColor backColorGeneral) {
		return new TileArguments(stringBounder, livingSpaces, skin, new SkinParamBackcolored(skinParam,
				backColorElement, backColorGeneral), origin);
	}

	public TileArguments withBackColor(Reference reference) {
		final ISkinParam newSkinParam = new SkinParamBackcoloredReference(skinParam, reference.getBackColorElement(),
				reference.getBackColorGeneral());
		return new TileArguments(stringBounder, livingSpaces, skin, newSkinParam, origin);
	}

	public final StringBounder getStringBounder() {
		return stringBounder;
	}

	// public final Real getMaxAbsolute() {
	// return origin.getMaxAbsolute();
	// }

	public final Real getOrigin() {
		return origin;
	}

	public final LivingSpaces getLivingSpaces() {
		return livingSpaces;
	}

	public final Rose getSkin() {
		return skin;
	}

	public final ISkinParam getSkinParam() {
		return skinParam;
	}

	public LivingSpace getLivingSpace(Participant p) {
		return livingSpaces.get(p);
	}

	public LivingSpace getFirstLivingSpace() {
		return livingSpaces.values().iterator().next();
	}

	public LivingSpace getLastLivingSpace() {
		LivingSpace result = null;
		for (LivingSpace v : livingSpaces.values()) {
			result = v;
		}
		return result;
	}

	private Bordered bordered;

	public void setBordered(Bordered bordered) {
		this.bordered = bordered;
	}

	public double getBorder1() {
		return bordered.getBorder1();
	}

	public double getBorder2() {
		return bordered.getBorder2();
	}

	// public double getAbsoluteMin() {
	// return line.getAbsoluteMin();
	// }
	//
	// public double getAbsoluteMax() {
	// return line.getAbsoluteMax();
	// }

	// public void ensure(Tile tile) {
	// getAlpha().ensureLowerThan(tile.getMinX(getStringBounder()));
	// getOmega().ensureBiggerThan(tile.getMaxX(getStringBounder()));
	// }

}
