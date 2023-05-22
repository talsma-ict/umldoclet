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
package net.sourceforge.plantuml.dot;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.EntityPortion;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.asciiart.BasicCharArea;
import net.sourceforge.plantuml.cucadiagram.ICucaDiagram;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.drawing.txt.UGraphicTxt;
import net.sourceforge.plantuml.klimt.geom.XCubicCurve2D;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.DotPath;
import net.sourceforge.plantuml.posimo.Block;
import net.sourceforge.plantuml.posimo.Cluster;
import net.sourceforge.plantuml.posimo.GraphvizSolverB;
import net.sourceforge.plantuml.posimo.Path;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SecurityUtils;
import net.sourceforge.plantuml.text.BackSlash;

public final class CucaDiagramTxtMaker {
	// ::remove file when __CORE__

	// private final CucaDiagram diagram;
	private final FileFormat fileFormat;
	private final UGraphicTxt globalUg = new UGraphicTxt();
	private final PortionShower portionShower;

	private static double getXPixelPerChar() {
		return 5;
	}

	private static double getYPixelPerChar() {
		return 10;
	}

	private boolean showMember(Entity entity) {
		final boolean showMethods = portionShower.showPortion(EntityPortion.METHOD, entity);
		final boolean showFields = portionShower.showPortion(EntityPortion.FIELD, entity);
		return showMethods || showFields;
	}

	public CucaDiagramTxtMaker(ICucaDiagram diagram, FileFormat fileFormat) throws IOException {
		this.fileFormat = fileFormat;
		this.portionShower = diagram;

		final Cluster root = new Cluster(null, 0, 0);
		int uid = 0;

		final Map<Entity, Block> blocks = new HashMap<Entity, Block>();

		for (Entity ent : diagram.getEntityFactory().leafs()) {
			// printClass(ent);
			// ug.translate(0, getHeight(ent) + 1);
			final double width = getWidth(ent) * getXPixelPerChar();
			final double height = getHeight(ent) * getYPixelPerChar();
			final Block b = new Block(uid++, width, height, null);
			root.addBloc(b);
			blocks.put(ent, b);
		}

		final GraphvizSolverB solver = new GraphvizSolverB();

		final Collection<Path> paths = new ArrayList<>();
		for (Link link : diagram.getLinks()) {
			final Block b1 = blocks.get(link.getEntity1());
			final Block b2 = blocks.get(link.getEntity2());
			paths.add(new Path(b1, b2, null, link.getLength(), link.isInvis()));
		}
		solver.solve(root, paths);
		for (Path p : paths) {
			if (p.isInvis()) {
				continue;
			}
			drawDotPath(p.getDotPath(), globalUg.getCharArea(), getXPixelPerChar(), getYPixelPerChar());
		}
		for (Entity ent : diagram.getEntityFactory().leafs()) {
			final Block b = blocks.get(ent);
			final XPoint2D p = b.getPosition();
			printClass(ent, (UGraphicTxt) globalUg
					.apply(new UTranslate(p.getX() / getXPixelPerChar(), p.getY() / getYPixelPerChar())));
		}

	}

	private void drawDotPath(DotPath dotPath, BasicCharArea area, double pixelXPerChar, double pixelYPerChar) {
		for (XCubicCurve2D bez : dotPath.getBeziers())
			if (bez.x1 == bez.x2)
				area.drawVLine('|', (int) (bez.x1 / pixelXPerChar), (int) (bez.y1 / pixelYPerChar),
						(int) (bez.y2 / pixelYPerChar));
			else if (bez.y1 == bez.y2)
				area.drawHLine('-', (int) (bez.y1 / pixelYPerChar), (int) (bez.x1 / pixelXPerChar),
						(int) (bez.x2 / pixelXPerChar));

	}

	private void printClass(final Entity ent, UGraphicTxt ug) {
		final int w = getWidth(ent);
		final int h = getHeight(ent);
		ug.getCharArea().drawBoxSimple(0, 0, w, h);
		ug.getCharArea().drawStringsLRSimple(ent.getDisplay().asList(), 1, 1);
		if (showMember(ent)) {
			int y = 2;
			ug.getCharArea().drawHLine('-', y, 1, w - 1);
			y++;
			for (CharSequence att : ent.getBodier().getRawBody()) {
				final List<String> disp = BackSlash.getWithNewlines(att.toString());
				ug.getCharArea().drawStringsLRSimple(disp, 1, y);
				y += StringUtils.getHeight(disp);
			}
//			for (Member att : ent.getBodier().getFieldsToDisplay()) {
//				final List<String> disp = BackSlash.getWithNewlines(att.getDisplay(true));
//				ug.getCharArea().drawStringsLR(disp, 1, y);
//				y += StringUtils.getHeight(disp);
//			}
//			ug.getCharArea().drawHLine('-', y, 1, w - 1);
//			y++;
//			for (Member att : ent.getBodier().getMethodsToDisplay()) {
//				final List<String> disp = BackSlash.getWithNewlines(att.getDisplay(true));
//				ug.getCharArea().drawStringsLR(disp, 1, y);
//				y += StringUtils.getHeight(disp);
//			}
		}
	}

	public List<SFile> createFiles(SFile suggestedFile) throws IOException {
		if (fileFormat == FileFormat.UTXT) {
			globalUg.getCharArea().print(suggestedFile.createPrintStream(UTF_8));
		} else {
			globalUg.getCharArea().print(suggestedFile.createPrintStream());
		}
		return Collections.singletonList(suggestedFile);
	}

	private int getHeight(Entity entity) {
		int result = StringUtils.getHeight(entity.getDisplay());
		if (showMember(entity)) {
			for (CharSequence att : entity.getBodier().getRawBody()) {
				result += StringUtils.getHeight(Display.getWithNewlines(att.toString()));
			}
//			for (Member att : entity.getBodier().getMethodsToDisplay()) {
//				result += StringUtils.getHeight(Display.getWithNewlines(att.getDisplay(true)));
//			}
//			result++;
//			for (Member att : entity.getBodier().getFieldsToDisplay()) {
//				result += StringUtils.getHeight(Display.getWithNewlines(att.getDisplay(true)));
//			}
//			result++;
		}
		return result + 3;
	}

	private int getWidth(Entity entity) {
		int result = StringUtils.getWcWidth(entity.getDisplay());
		if (showMember(entity)) {
			for (CharSequence att : entity.getBodier().getRawBody()) {
				final int w = StringUtils.getWcWidth(Display.getWithNewlines(att.toString()));
				if (w > result) {
					result = w;
				}
			}
//			for (Member att : entity.getBodier().getMethodsToDisplay()) {
//			final int w = StringUtils.getWcWidth(Display.getWithNewlines(att.getDisplay(true)));
//			if (w > result) {
//				result = w;
//			}
//		}
//			for (Member att : entity.getBodier().getFieldsToDisplay()) {
//				final int w = StringUtils.getWcWidth(Display.getWithNewlines(att.getDisplay(true)));
//				if (w > result) {
//					result = w;
//				}
//			}
		}
		return result + 2;
	}

	public void createFiles(OutputStream os, int index) {
		globalUg.getCharArea().print(SecurityUtils.createPrintStream(os));
	}

}
