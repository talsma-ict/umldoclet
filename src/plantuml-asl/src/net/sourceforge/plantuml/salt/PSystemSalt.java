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
package net.sourceforge.plantuml.salt;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.ScaleSimple;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.WithSprite;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandFactorySprite;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.salt.element.Element;
import net.sourceforge.plantuml.salt.factory.AbstractElementFactoryComplex;
import net.sourceforge.plantuml.salt.factory.ElementFactory;
import net.sourceforge.plantuml.salt.factory.ElementFactoryBorder;
import net.sourceforge.plantuml.salt.factory.ElementFactoryButton;
import net.sourceforge.plantuml.salt.factory.ElementFactoryCheckboxOff;
import net.sourceforge.plantuml.salt.factory.ElementFactoryCheckboxOn;
import net.sourceforge.plantuml.salt.factory.ElementFactoryDroplist;
import net.sourceforge.plantuml.salt.factory.ElementFactoryImage;
import net.sourceforge.plantuml.salt.factory.ElementFactoryLine;
import net.sourceforge.plantuml.salt.factory.ElementFactoryMenu;
import net.sourceforge.plantuml.salt.factory.ElementFactoryPyramid;
import net.sourceforge.plantuml.salt.factory.ElementFactoryRadioOff;
import net.sourceforge.plantuml.salt.factory.ElementFactoryRadioOn;
import net.sourceforge.plantuml.salt.factory.ElementFactoryRetrieveFromDictonnary;
import net.sourceforge.plantuml.salt.factory.ElementFactoryScroll;
import net.sourceforge.plantuml.salt.factory.ElementFactoryTab;
import net.sourceforge.plantuml.salt.factory.ElementFactoryText;
import net.sourceforge.plantuml.salt.factory.ElementFactoryTextField;
import net.sourceforge.plantuml.salt.factory.ElementFactoryTree;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PSystemSalt extends AbstractPSystem implements WithSprite {

	private final List<String> data;
	private final Dictionary dictionary = new Dictionary();

	@Deprecated
	public PSystemSalt(List<String> data) {
		this.data = data;
	}

	public PSystemSalt() {
		this(new ArrayList<String>());
	}

	public void add(String s) {
		data.add(s);
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		try {
			final Element salt = createElement(manageSprite());

			final Dimension2D size = salt.getPreferredDimension(fileFormat.getDefaultStringBounder(), 0, 0);

			double scale = 1;
			if (getScale() != null) {
				scale = getScale().getScale(size.getWidth(), size.getHeight());
			}

			final int margin1;
			final int margin2;
			if (SkinParam.USE_STYLES()) {
				margin1 = SkinParam.zeroMargin(5);
				margin2 = SkinParam.zeroMargin(5);
			} else {
				margin1 = 5;
				margin2 = 5;
			}

			final ImageBuilder builder = ImageBuilder.buildB(new ColorMapperIdentity(), false,
					ClockwiseTopRightBottomLeft.margin1margin2(margin1, margin2), null, getMetadata(), null, scale,
					HColorUtils.WHITE);
			builder.setUDrawable(new UDrawable() {

				public void drawU(UGraphic ug) {
					ug = ug.apply(HColorUtils.BLACK);
					salt.drawU(ug, 0, new Dimension2DDouble(size.getWidth(), size.getHeight()));
					salt.drawU(ug, 1, new Dimension2DDouble(size.getWidth(), size.getHeight()));
				}
			});
			return builder.writeImageTOBEMOVED(fileFormat, seed, os);
		} catch (Exception e) {
			e.printStackTrace();
			UmlDiagram.exportDiagramError(os, e, fileFormat, seed, getMetadata(), "none", new ArrayList<String>());
			return ImageDataSimple.error();
		}
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Salt)");
	}

	public void addSprite(String name, Sprite sprite) {
		dictionary.addSprite(name, sprite);
	}

	private List<String> manageSprite() {

		final Command<WithSprite> cmd = new CommandFactorySprite().createMultiLine(false);

		final List<String> result = new ArrayList<String>();
		for (Iterator<String> it = data.iterator(); it.hasNext();) {
			String s = it.next();
			if (s.equals("hide stereotype")) {
				// System.err.println("skipping " + s);
			} else if (s.startsWith("skinparam ")) {
				// System.err.println("skipping " + s);
			} else if (s.startsWith("scale ")) {
				final double scale = Double.parseDouble(s.substring("scale ".length()));
				this.setScale(new ScaleSimple(scale));
				// System.err.println("skipping " + s);
			} else if (s.startsWith("sprite $")) {
				BlocLines bloc = BlocLines.singleString(s);
				do {
					s = it.next();
					bloc = bloc.addString(s);
				} while (s.equals("}") == false);
				final CommandExecutionResult cmdResult = cmd.execute(this, bloc);
			} else {
				result.add(s);
			}
		}
		return result;
	}

	private Element createElement(List<String> data) {

		final DataSourceImpl source = new DataSourceImpl(data);

		final Collection<AbstractElementFactoryComplex> cpx = new ArrayList<AbstractElementFactoryComplex>();

		// cpx.add(new ElementFactorySimpleFrame(source, dictionnary));
		cpx.add(new ElementFactoryPyramid(source, dictionary));
		cpx.add(new ElementFactoryScroll(source, dictionary));
		cpx.add(new ElementFactoryBorder(source, dictionary));

		for (AbstractElementFactoryComplex f : cpx) {
			addSimpleFactory(f, source, dictionary);
		}
		for (AbstractElementFactoryComplex f1 : cpx) {
			for (AbstractElementFactoryComplex f2 : cpx) {
				f1.addFactory(f2);
			}
		}

		for (ElementFactory f : cpx) {
			if (f.ready()) {
				Log.info("Using " + f);
				return f.create().getElement();
			}
		}

		Log.println("data=" + data);
		throw new IllegalArgumentException();

	}

	private static void addSimpleFactory(final AbstractElementFactoryComplex cpxFactory, final DataSource source,
			Dictionary dictionary) {
		cpxFactory.addFactory(new ElementFactoryMenu(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryTree(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryTab(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryLine(source));
		cpxFactory.addFactory(new ElementFactoryTextField(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryButton(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryDroplist(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryRadioOn(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryRadioOff(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryCheckboxOn(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryCheckboxOff(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryImage(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryRetrieveFromDictonnary(source, dictionary));
		cpxFactory.addFactory(new ElementFactoryText(source, dictionary));
	}

}
