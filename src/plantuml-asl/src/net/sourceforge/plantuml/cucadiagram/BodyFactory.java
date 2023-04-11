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
package net.sourceforge.plantuml.cucadiagram;

import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.klimt.LineBreakStrategy;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.ISkinSimple;
import net.sourceforge.plantuml.style.Style;

public class BodyFactory {

	public final static boolean BODY3 = false;

	public static Bodier createLeaf(LeafType type, Set<VisibilityModifier> hides) {
		if (type.isLikeClass() || type == LeafType.OBJECT)
			return new BodierLikeClassOrObject(type, hides);

		return new BodierSimple();
	}

	public static Bodier createGroup(Set<VisibilityModifier> hides) {
		return new BodierSimple();
	}

	public static TextBlock create1(HorizontalAlignment align, List<CharSequence> rawBody, ISkinParam skinParam,
			Stereotype stereotype, Entity entity, Style style) {
		return new BodyEnhanced1(align, rawBody, skinParam, entity, style);
	}

	public static TextBlock create2(HorizontalAlignment align, Display display, ISkinParam skinParam,
			Stereotype stereotype, Entity entity, Style style) {
		return new BodyEnhanced1(align, display, skinParam, entity, style);
	}

	public static TextBlock create3(Display rawBody, ISkinSimple skinParam, HorizontalAlignment align,
			FontConfiguration titleConfig, LineBreakStrategy lineBreakStrategy, Style style) {
		return new BodyEnhanced2(rawBody, skinParam, align, titleConfig, lineBreakStrategy, style);
	}

}
