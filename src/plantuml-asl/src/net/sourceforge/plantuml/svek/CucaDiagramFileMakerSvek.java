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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.BaseFile;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.NamedOutputStream;
import net.sourceforge.plantuml.Scale;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.api.ImageDataAbstract;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramSimplifierActivity;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramSimplifierState;
import net.sourceforge.plantuml.cucadiagram.dot.DotData;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;

public final class CucaDiagramFileMakerSvek implements CucaDiagramFileMaker {

	private final CucaDiagram diagram;

	public CucaDiagramFileMakerSvek(CucaDiagram diagram) throws IOException {
		this.diagram = diagram;
	}

	public ImageData createFile(OutputStream os, List<String> dotStrings, FileFormatOption fileFormatOption)
			throws IOException {
		try {
			return createFileInternal(os, dotStrings, fileFormatOption);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	private GeneralImageBuilder createDotDataImageBuilder(DotMode dotMode, StringBounder stringBounder) {
		final DotData dotData = new DotData(diagram.getEntityFactory().getRootGroup(), getOrderedLinks(),
				diagram.getLeafsvalues(), diagram.getUmlDiagramType(), diagram.getSkinParam(), diagram, diagram,
				diagram.getColorMapper(), diagram.getEntityFactory(), diagram.isHideEmptyDescriptionForState(), dotMode,
				diagram.getNamespaceSeparator(), diagram.getPragma());
		final boolean intricated = diagram.mergeIntricated();
		return new GeneralImageBuilder(intricated, dotData, diagram.getEntityFactory(), diagram.getSource(),
				diagram.getPragma(), stringBounder);

	}


	private ImageData createFileInternal(OutputStream os, List<String> dotStrings, FileFormatOption fileFormatOption)
			throws IOException, InterruptedException {
		if (diagram.getUmlDiagramType() == UmlDiagramType.ACTIVITY) {
			new CucaDiagramSimplifierActivity(diagram, dotStrings, fileFormatOption.getDefaultStringBounder());
		} else if (diagram.getUmlDiagramType() == UmlDiagramType.STATE) {
			new CucaDiagramSimplifierState(diagram, dotStrings, fileFormatOption.getDefaultStringBounder());
		}

		// System.err.println("FOO11 type=" + os.getClass());
		GeneralImageBuilder svek2 = createDotDataImageBuilder(DotMode.NORMAL,
				fileFormatOption.getDefaultStringBounder());
		BaseFile basefile = null;
		if (fileFormatOption.isDebugSvek() && os instanceof NamedOutputStream) {
			basefile = ((NamedOutputStream) os).getBasefile();
		}
		// System.err.println("FOO11 basefile=" + basefile);

		TextBlockBackcolored result = svek2.buildImage(basefile, diagram.getDotStringSkek());
		if (result instanceof GraphvizCrash) {
			svek2 = createDotDataImageBuilder(DotMode.NO_LEFT_RIGHT_AND_XLABEL,
					fileFormatOption.getDefaultStringBounder());
			result = svek2.buildImage(basefile, diagram.getDotStringSkek());
		}
		final boolean isGraphvizCrash = result instanceof GraphvizCrash;
		result = new AnnotatedWorker(diagram, diagram.getSkinParam(), fileFormatOption.getDefaultStringBounder())
				.addAdd(result);

		final String widthwarning = diagram.getSkinParam().getValue("widthwarning");
		String warningOrError = null;
		if (widthwarning != null && widthwarning.matches("\\d+")) {
			warningOrError = svek2.getWarningOrError(Integer.parseInt(widthwarning));
		}
		final Dimension2D dim = result.calculateDimension(fileFormatOption.getDefaultStringBounder());
		final double scale = getScale(fileFormatOption, dim);

		final ImageBuilder imageBuilder = new ImageBuilder(diagram.getSkinParam(), scale,
				fileFormatOption.isWithMetadata() ? diagram.getMetadata() : null, warningOrError, 0, 10,
				diagram.getAnimation(), result.getBackcolor());
		imageBuilder.setUDrawable(result);
		final ImageData imageData = imageBuilder.writeImageTOBEMOVED(fileFormatOption, diagram.seed(), os);
		if (isGraphvizCrash) {
			((ImageDataAbstract) imageData).setStatus(503);
		}
		return imageData;
	}

	private List<Link> getOrderedLinks() {
		final List<Link> result = new ArrayList<Link>();
		for (Link l : diagram.getLinks()) {
			addLinkNew(result, l);
		}
		return result;
	}

	private void addLinkNew(List<Link> result, Link link) {
		for (int i = 0; i < result.size(); i++) {
			final Link other = result.get(i);
			if (other.sameConnections(link)) {
				while (i < result.size() && result.get(i).sameConnections(link)) {
					i++;
				}
				if (i == result.size()) {
					result.add(link);
				} else {
					result.add(i, link);
				}
				return;
			}
		}
		result.add(link);
	}

	private double getScale(FileFormatOption fileFormatOption, final Dimension2D dim) {
		final double scale;
		final Scale diagScale = diagram.getScale();
		if (diagScale == null) {
			scale = diagram.getScaleCoef(fileFormatOption);
		} else {
			scale = diagScale.getScale(dim.getWidth(), dim.getHeight());
		}
		return scale;
	}

}
