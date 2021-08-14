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
package net.sourceforge.plantuml.sprite;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.WithSprite;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandFactorySprite;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.preproc.Stdlib;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class StdlibDiagram extends UmlDiagram {

	private static final int WIDTH = 1800;
	private String name;

	public StdlibDiagram(UmlSource source, ISkinSimple skinParam) {
		super(source, UmlDiagramType.HELP, skinParam);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Sprites)");
	}

	@Override
	public ImageBuilder createImageBuilder(FileFormatOption fileFormatOption) throws IOException {
		return super.createImageBuilder(fileFormatOption)
				.annotations(false);
	}

	@Override
	protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		return createImageBuilder(fileFormatOption)
				.drawable(getTable())
				.write(os);
	}

	private TextBlock getTable() {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				try {
					drawInternal(ug);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(WIDTH, 4096);
			}
		};
	}

	public void setStdlibName(String name) {
		this.name = name;
	}

	private void drawInternal(UGraphic ug) throws IOException {
		double x = 0;
		double y = 0;
		double rawHeight = 0;
		final Stdlib folder = Stdlib.retrieve(name);

		final CommandFactorySprite factorySpriteCommand = new CommandFactorySprite();

		Command<WithSprite> cmd = factorySpriteCommand.createMultiLine(false);

		final List<String> all = folder.extractAllSprites();
		int nb = 0;
		for (String s : all) {
			// System.err.println("s="+s);
			final BlocLines bloc = BlocLines.fromArray(s.split("\n"));
			try {
				cmd.execute(this, bloc);
			} catch (NoSuchColorException e) {
				e.printStackTrace();
			}
//			System.err.println("nb=" + nb);
			nb++;
		}

		for (String n : getSkinParam().getAllSpriteNames()) {
			final Sprite sprite = getSkinParam().getSprite(n);
			TextBlock blockName = Display.create(n).create(FontConfiguration.blackBlueTrue(UFont.sansSerif(14)),
					HorizontalAlignment.LEFT, getSkinParam());
			TextBlock tb = sprite.asTextBlock(HColorUtils.BLACK, 1.0);
			tb = TextBlockUtils.mergeTB(tb, blockName, HorizontalAlignment.CENTER);
			tb.drawU(ug.apply(new UTranslate(x, y)));
			final Dimension2D dim = tb.calculateDimension(ug.getStringBounder());
			rawHeight = Math.max(rawHeight, dim.getHeight());
			x += dim.getWidth();
			x += 30;
			if (x > WIDTH) {
				x = 0;
				y += rawHeight + 50;
				rawHeight = 0;
				if (y > 1024) {
//					break;
				}
			}
		}
	}
}
