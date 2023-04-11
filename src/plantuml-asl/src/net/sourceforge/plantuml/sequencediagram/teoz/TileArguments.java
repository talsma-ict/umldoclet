/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.Reference;
import net.sourceforge.plantuml.skin.SkinParamBackcolored;
import net.sourceforge.plantuml.skin.SkinParamBackcoloredReference;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.ISkinParam;

public class TileArguments implements Bordered {
	private final StringBounder stringBounder;
	private final Real xorigin;
	private final Real yorigin;
	private final LivingSpaces livingSpaces;
	private final Rose skin;
	private final ISkinParam skinParam;

	public TileArguments(StringBounder stringBounder, LivingSpaces livingSpaces, Rose skin, ISkinParam skinParam,
			Real xorigin, Real yorigin) {
		this.stringBounder = stringBounder;
		this.xorigin = xorigin;
		this.yorigin = yorigin;
		this.livingSpaces = livingSpaces;
		this.skin = skin;
		this.skinParam = skinParam;
	}

	public TileArguments withBackColorGeneral(HColor backColorElement, HColor backColorGeneral) {
		return new TileArguments(stringBounder, livingSpaces, skin,
				new SkinParamBackcolored(skinParam, backColorElement, backColorGeneral), xorigin, yorigin);
	}

	public TileArguments withBackColor(Reference reference) {
		final ISkinParam newSkinParam = new SkinParamBackcoloredReference(skinParam, reference.getBackColorElement(),
				reference.getBackColorGeneral());
		return new TileArguments(stringBounder, livingSpaces, skin, newSkinParam, xorigin, yorigin);
	}

	public final StringBounder getStringBounder() {
		return stringBounder;
	}

	public final Real getXOrigin() {
		return xorigin;
	}

	public final Real getYOrigin() {
		return yorigin;
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
		for (LivingSpace v : livingSpaces.values())
			result = v;

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

}
