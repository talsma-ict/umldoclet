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
package net.sourceforge.plantuml.ditaa;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.BackSlash;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.svek.GraphvizCrash;

public class PSystemDitaa extends AbstractPSystem {

	// private ProcessingOptions processingOptions;
	private Object processingOptions;
	private final boolean dropShadows;
	private final String data;
	private final float scale;
	private final boolean performSeparationOfCommonEdges;

	public PSystemDitaa(UmlSource source, String data, boolean performSeparationOfCommonEdges, boolean dropShadows, float scale) {
		super(source);
		this.data = data;
		this.dropShadows = dropShadows;
		this.performSeparationOfCommonEdges = performSeparationOfCommonEdges;
		try {
			this.processingOptions = Class.forName("org.stathissideris.ascii2image.core.ProcessingOptions")
					.newInstance();
			// this.processingOptions.setPerformSeparationOfCommonEdges(performSeparationOfCommonEdges);
			this.processingOptions.getClass().getMethod("setPerformSeparationOfCommonEdges", boolean.class)
					.invoke(this.processingOptions, performSeparationOfCommonEdges);
		} catch (Exception e) {
			e.printStackTrace();
			this.processingOptions = null;
		}
		this.scale = scale;
	}

	PSystemDitaa add(String line) {
		return new PSystemDitaa(getSource(), data + line + BackSlash.NEWLINE, performSeparationOfCommonEdges, dropShadows, scale);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Ditaa)");
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat)
			throws IOException {
		if (fileFormat.getFileFormat() == FileFormat.ATXT) {
			os.write(getSource().getPlainString().getBytes());
			return ImageDataSimple.ok();
		}

		// ditaa can only export png so file format is mostly ignored
		try {
			// ditaa0_9.jar
			// final ConversionOptions options = new ConversionOptions();
			final Object options = Class.forName("org.stathissideris.ascii2image.core.ConversionOptions").newInstance();

			// final RenderingOptions renderingOptions = options.renderingOptions;
			final Field f_renderingOptions = options.getClass().getField("renderingOptions");
			final Object renderingOptions = f_renderingOptions.get(options);

			// renderingOptions.setScale(scale);
			final Method setScale = renderingOptions.getClass().getMethod("setScale", float.class);
			setScale.invoke(renderingOptions, scale);

			// options.setDropShadows(dropShadows);
			final Method setDropShadows = options.getClass().getMethod("setDropShadows", boolean.class);
			setDropShadows.invoke(options, dropShadows);

			// final TextGrid grid = new TextGrid();
			final Object grid = Class.forName("org.stathissideris.ascii2image.text.TextGrid").newInstance();

			// grid.initialiseWithText(data, null);
			final Method initialiseWithText = grid.getClass().getMethod("initialiseWithText", String.class,
					Class.forName("org.stathissideris.ascii2image.core.ProcessingOptions"));
			initialiseWithText.invoke(grid, data, null);

			// final Diagram diagram = new Diagram(grid, options, processingOptions);
			final Class<?> clDiagram = Class.forName("org.stathissideris.ascii2image.graphics.Diagram");
			clDiagram.getConstructor(grid.getClass(), options.getClass(), processingOptions.getClass())
					.newInstance(grid, options, processingOptions);
			final Object diagram = clDiagram
					.getConstructor(grid.getClass(), options.getClass(), processingOptions.getClass())
					.newInstance(grid, options, processingOptions);

			// final BitmapRenderer bitmapRenderer = new BitmapRenderer();
			final Object bitmapRenderer = Class.forName("org.stathissideris.ascii2image.graphics.BitmapRenderer")
					.newInstance();

			// final BufferedImage image = (BufferedImage)
			// bitmapRenderer.renderToImage(diagram, renderingOptions);
			final Method renderToImage = bitmapRenderer.getClass().getMethod("renderToImage", diagram.getClass(),
					renderingOptions.getClass());
			final BufferedImage image = (BufferedImage) renderToImage.invoke(bitmapRenderer, diagram, renderingOptions);

			SImageIO.write(image, "png", os);
			final int width = image.getWidth();
			final int height = image.getHeight();
			return new ImageDataSimple(width, height);
		} catch (Throwable e) {
			final List<String> strings = new ArrayList<>();
			strings.add("DITAA has crashed");
			strings.add(" ");
			GraphvizCrash.youShouldSendThisDiagram(strings);
			strings.add(" ");
			UmlDiagram.exportDiagramError(os, e, new FileFormatOption(FileFormat.PNG), seed(), null, null, strings);
			return ImageDataSimple.error();
		}

	}

}
