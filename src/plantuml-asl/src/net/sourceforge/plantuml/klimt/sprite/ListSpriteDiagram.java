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
package net.sourceforge.plantuml.klimt.sprite;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.atmp.ImageBuilder;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.skin.UmlDiagramType;

public class ListSpriteDiagram extends UmlDiagram {
	// ::remove file when __CORE__

	public ListSpriteDiagram(UmlSource source, Map<String, String> skinParam) {
		super(source, UmlDiagramType.HELP, skinParam);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Sprites)");
	}

	@Override
	public ImageBuilder createImageBuilder(FileFormatOption fileFormatOption) throws IOException {
		return super.createImageBuilder(fileFormatOption).annotations(false);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	@Override
	protected TextBlock getTextBlock() {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				double x = 0;
				double y = 0;
				double rawHeight = 0;
				for (String n : getSkinParam().getAllSpriteNames()) {
					final Sprite sprite = getSkinParam().getSprite(n);
					TextBlock blockName = Display.create(n).create(FontConfiguration.blackBlueTrue(UFont.sansSerif(14)),
							HorizontalAlignment.LEFT, getSkinParam());
					TextBlock tb = sprite.asTextBlock(HColors.BLACK, 1.0);
					tb = TextBlockUtils.mergeTB(tb, blockName, HorizontalAlignment.CENTER);
					tb.drawU(ug.apply(new UTranslate(x, y)));
					final XDimension2D dim = tb.calculateDimension(ug.getStringBounder());
					rawHeight = Math.max(rawHeight, dim.getHeight());
					x += dim.getWidth();
					x += 30;
					if (x > 1024) {
						x = 0;
						y += rawHeight + 50;
						rawHeight = 0;
					}
				}
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return new XDimension2D(1024, 1024);
			}
		};
	}
}
