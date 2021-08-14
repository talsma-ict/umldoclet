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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockVertical2;

public class BodyEnhanced2 extends BodyEnhancedAbstract {

	private final Display rawBody;
	private final ISkinSimple skinParam;

	private final LineBreakStrategy lineBreakStrategy;

	BodyEnhanced2(Display rawBody, FontParam fontParam, ISkinSimple skinParam, HorizontalAlignment align,
			FontConfiguration titleConfig, LineBreakStrategy lineBreakStrategy) {
		super(align, titleConfig);
		this.rawBody = rawBody;
		this.lineBreakStrategy = lineBreakStrategy;
		this.skinParam = skinParam;

	}

	@Override
	protected double getMarginX() {
		return 0;
	}

	@Override
	protected TextBlock getArea(StringBounder stringBounder) {
		if (area != null) {
			return area;
		}
		// urls.clear();
		final List<TextBlock> blocks = new ArrayList<>();

		char separator = 0;
		TextBlock title = null;
		Display display = Display.empty();
		for (CharSequence s : rawBody) {
			if (isBlockSeparator(s.toString())) {
				blocks.add(decorate(stringBounder, getTextBlock(display), separator, title));
				separator = s.charAt(0);
				title = getTitle(s.toString(), skinParam);
				display = Display.empty();
			} else {
				if (s instanceof String) {
					s = Guillemet.GUILLEMET.manageGuillemet(s.toString());
				}
				display = display.add(s);
			}
		}
		blocks.add(decorate(stringBounder, getTextBlock(display), separator, title));

		if (blocks.size() == 1) {
			this.area = blocks.get(0);
		} else {
			this.area = new TextBlockVertical2(blocks, align);
		}

		if (skinParam.minClassWidth() > 0) {
			this.area = TextBlockUtils.withMinWidth(this.area, skinParam.minClassWidth(), align);
		}

		return area;
	}

	private TextBlock getTextBlock(Display display) {
		final TextBlock result = display.create9(titleConfig, align, skinParam, lineBreakStrategy);
		return result;
	}

}
