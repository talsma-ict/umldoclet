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
package net.sourceforge.plantuml.posimo;

import static java.nio.charset.StandardCharsets.UTF_8;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.cucadiagram.dot.Graphviz;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.cucadiagram.dot.ProcessState;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.svek.MinFinder;
import net.sourceforge.plantuml.svek.SvgResult;
import net.sourceforge.plantuml.svek.YDelta;

public class GraphvizSolverB {

	// static private void traceDotString(String dotString) throws IOException {
	// final File f = SecurityUtils.File("dottmpfile" + UniqueSequence.getValue() + ".tmp");
	// PrintWriter pw = null;
	// try {
	// pw = SecurityUtils.PrintWriter(new FileWriter(f));
	// pw.print(dotString);
	// Log.info("Creating file " + f);
	// } finally {
	// if (pw != null) {
	// pw.close();
	// }
	// }
	// }
	//
	// static private void traceSvgString(String svg) throws IOException {
	// final File f = SecurityUtils.File("svgtmpfile" + UniqueSequence.getValue() + ".svg");
	// PrintWriter pw = null;
	// try {
	// pw = SecurityUtils.PrintWriter(new FileWriter(f));
	// pw.print(svg);
	// Log.info("Creating file " + f);
	// } finally {
	// if (pw != null) {
	// pw.close();
	// }
	// }
	// }

	public Dimension2D solve(Cluster root, Collection<Path> paths) throws IOException {
		final String dotString = new DotxMaker(root, paths).createDotString("nodesep=0.2;", "ranksep=0.2;");

		// if (OptionFlags.getInstance().isKeepTmpFiles()) {
		// traceDotString(dotString);
		// }

		final MinFinder minMax = new MinFinder();

		// Log.println("dotString=" + dotString);

		// exportPng(dotString, SecurityUtils.File("png", "test1.png"));

		final Graphviz graphviz = GraphvizUtils.create(null, dotString, "svg");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ProcessState state = graphviz.createFile3(baos);
		baos.close();
		if (state.differs(ProcessState.TERMINATED_OK())) {
			throw new IllegalStateException("Timeout2 " + state);
		}
		final byte[] result = baos.toByteArray();
		final String s = new String(result, UTF_8);
		// Log.println("result=" + s);

		// if (OptionFlags.getInstance().isKeepTmpFiles()) {
		// traceSvgString(s);
		// }

		final Pattern pGraph = Pattern.compile("(?m)\\<svg\\s+width=\"(\\d+)pt\"\\s+height=\"(\\d+)pt\"");
		final Matcher mGraph = pGraph.matcher(s);
		if (mGraph.find() == false) {
			throw new IllegalStateException();
		}
		final int width = Integer.parseInt(mGraph.group(1));
		final int height = Integer.parseInt(mGraph.group(2));

		final YDelta yDelta = new YDelta(height);
		for (Block b : root.getRecursiveContents()) {
			final String start = "b" + b.getUid();
			final int p1 = s.indexOf("<title>" + start + "</title>");
			if (p1 == -1) {
				throw new IllegalStateException();
			}
			final List<Point2D.Double> pointsList = extractPointsList(s, p1, yDelta);
			b.setX(getMinX(pointsList));
			b.setY(getMinY(pointsList));
			minMax.manage(b.getPosition());
		}

		for (Cluster cl : root.getSubClusters()) {
			final String start = "cluster" + cl.getUid();
			final int p1 = s.indexOf("<title>" + start + "</title>");
			if (p1 == -1) {
				throw new IllegalStateException();
			}
			final List<Point2D.Double> pointsList = extractPointsList(s, p1, yDelta);
			cl.setX(getMinX(pointsList));
			cl.setY(getMinY(pointsList));
			final double w = getMaxX(pointsList) - getMinX(pointsList);
			final double h = getMaxY(pointsList) - getMinY(pointsList);
			cl.setHeight(h);
			cl.setWidth(w);
			minMax.manage(cl.getPosition());
		}

		for (Path p : paths) {
			final String start = "b" + p.getStart().getUid();
			final String end = "b" + p.getEnd().getUid();
			final String searched = "<title>" + start + "&#45;&gt;" + end + "</title>";
			final int p1 = s.indexOf(searched);
			if (p1 == -1) {
				throw new IllegalStateException(searched);
			}
			final int p2 = s.indexOf(" d=\"", p1);
			final int p3 = s.indexOf("\"", p2 + " d=\"".length());
			final String points = s.substring(p2 + " d=\"".length(), p3);
			final DotPath dotPath = new DotPath(new SvgResult(points, yDelta));
			p.setDotPath(dotPath);
			minMax.manage(dotPath.getMinFinder());

			// Log.println("pointsList=" + pointsList);
			if (p.getLabel() != null) {
				final List<Point2D.Double> pointsList = extractPointsList(s, p1, yDelta);
				final double x = getMinX(pointsList);
				final double y = getMinY(pointsList);
				p.setLabelPosition(x, y);
				minMax.manage(x, y);
			}
		}
		return new Dimension2DDouble(width, height);
	}

	static private List<Point2D.Double> extractPointsList(final String svg, final int starting, final YDelta yDelta) {
		return new SvgResult(svg, yDelta).substring(starting).extractList(SvgResult.POINTS_EQUALS);
	}

	static private double getMaxX(List<Point2D.Double> points) {
		double result = points.get(0).x;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).x > result) {
				result = points.get(i).x;
			}
		}
		return result;
	}

	static private double getMinX(List<Point2D.Double> points) {
		double result = points.get(0).x;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).x < result) {
				result = points.get(i).x;
			}
		}
		return result;
	}

	static private double getMaxY(List<Point2D.Double> points) {
		double result = points.get(0).y;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).y > result) {
				result = points.get(i).y;
			}
		}
		return result;
	}

	static private double getMinY(List<Point2D.Double> points) {
		double result = points.get(0).y;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).y < result) {
				result = points.get(i).y;
			}
		}
		return result;
	}

	private void exportPng(final String dotString, SFile f) throws IOException {
		final Graphviz graphviz = GraphvizUtils.create(null, dotString, "png");
		try (OutputStream os = f.createBufferedOutputStream()) {
			final ProcessState state = graphviz.createFile3(os);
			if (state.differs(ProcessState.TERMINATED_OK())) {
				throw new IllegalStateException("Timeout3 " + state);
			}
		}
	}

	private Path getPath(Collection<Path> paths, int start, int end) {
		for (Path p : paths) {
			if (p.getStart().getUid() == start && p.getEnd().getUid() == end) {
				return p;
			}
		}
		throw new IllegalArgumentException();

	}
}
