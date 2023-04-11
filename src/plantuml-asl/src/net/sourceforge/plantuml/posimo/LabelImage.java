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
package net.sourceforge.plantuml.posimo;

import java.util.Objects;

import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.style.ISkinParam;

public class LabelImage {

	// private final Entity entity;
	final private ISkinParam param;
	final private Rose rose;
	final private TextBlock name;

	public LabelImage(Link link, Rose rose, ISkinParam param) {
		Objects.requireNonNull(link);
		// this.entity = entity;
		this.param = param;
		this.rose = rose;
//		this.name = link.getLabel().create(
//				FontConfiguration.create(param.getFont(FontParam.CLASS, null, false), HtmlColorUtils.BLACK,
//						param.getHyperlinkColor(), param.useUnderlineForHyperlink()), HorizontalAlignment.CENTER,
//				new SpriteContainerEmpty());
		throw new UnsupportedOperationException();
	}

	public XDimension2D getDimension(StringBounder stringBounder) {
		final XDimension2D dim = name.calculateDimension(stringBounder);
		return dim;
		// return Dimension2DDouble.delta(dim, 2 * margin);
	}

	public void drawU(UGraphic ug, double x, double y) {
		// final Dimension2D dim = getDimension(ug.getStringBounder());
		// ug.getParam().setBackcolor(rose.getHtmlColor(param,
		// ColorParam.classBackground).getColor());
		// ug.getParam().setColor(rose.getHtmlColor(param,
		// ColorParam.classBorder).getColor());
		// ug.draw(x, y, new URectangle(dim.getWidth(), dim.getHeight()));
		name.drawU(ug.apply(new UTranslate(x, y)));
	}
}
