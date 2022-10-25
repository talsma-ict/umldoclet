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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.cucadiagram.DisplayPositioned;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.DecorateEntityImage;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;

public class AnnotatedWorker {

	private final Annotated annotated;
	private final ISkinParam skinParam;
	private final StringBounder stringBounder;
	private final AnnotatedBuilder builder;

	public AnnotatedWorker(Annotated annotated, ISkinParam skinParam, StringBounder stringBounder,
			AnnotatedBuilder builder) {
		this.annotated = annotated;
		this.skinParam = skinParam;
		this.stringBounder = stringBounder;
		this.builder = builder;
	}

	public TextBlockBackcolored addAdd(TextBlock result) {
		result = builder.decoreWithFrame(result);
		result = addLegend(result);
		result = addTitle(result);
		result = addCaption(result);
		result = builder.addHeaderAndFooter(result);
		return (TextBlockBackcolored) result;
	}

	public TextBlock addLegend(TextBlock original) {
		final DisplayPositioned legend = annotated.getLegend();
		if (legend.isNull())
			return original;

		return DecorateEntityImage.add(original, builder.getLegend(), legend.getHorizontalAlignment(),
				legend.getVerticalAlignment());
	}

	public TextBlock addTitle(TextBlock original) {
		final DisplayPositioned title = (DisplayPositioned) annotated.getTitle();
		if (title.isNull())
			return original;

		return DecorateEntityImage.addTop(original, builder.getTitle(), HorizontalAlignment.CENTER);
	}

	private TextBlock addCaption(TextBlock original) {
		final DisplayPositioned caption = annotated.getCaption();
		if (caption.isNull())
			return original;

		return DecorateEntityImage.addBottom(original, builder.getCaption(), HorizontalAlignment.CENTER);
	}

}
