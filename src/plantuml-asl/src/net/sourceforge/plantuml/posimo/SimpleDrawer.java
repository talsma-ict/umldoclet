/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

public class SimpleDrawer {

	private final Cluster root;
	private final Collection<Path> paths;

	public SimpleDrawer(Cluster root, Collection<Path> paths) {
		this.root = root;
		this.paths = paths;
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		for (Clusterable cl : root.getContents()) {
			final Block b = (Block) cl;
			final Point2D pos = b.getPosition();
			final Dimension2D dim = b.getSize();
			// drawRectCentered(g2d, pos, dim);
			drawRect(g2d, pos, dim);
		}

		g2d.setColor(Color.GREEN);
		for (Path p : paths) {
			final Label label = p.getLabel();
			final Point2D labelPos = label.getPosition();
			final Dimension2D labelDim = label.getSize();
			// final double x1 = labelPos.getX();
			// final double y1 = labelPos.getY();
			// g2d.draw(new Ellipse2D.Double(x1 - 1, y1 - 1, 3, 3));
			// drawRectCentered(g2d, labelPos, labelDim);
			drawRect(g2d, labelPos, labelDim);
		}

		g2d.setColor(Color.RED);
		for (Path p : paths) {
			p.getDotPath().draw(g2d, 0, 0);
		}

		for (Cluster sub : root.getSubClusters()) {
			new SimpleDrawer(sub, new ArrayList<Path>()).draw(g2d);
		}

	}

	private void drawRectCentered(Graphics2D g2d, final Point2D pos, final Dimension2D dim) {
		final Rectangle2D rect = new Rectangle2D.Double(pos.getX() - dim.getWidth() / 2, pos.getY() - dim.getHeight()
				/ 2, dim.getWidth(), dim.getHeight());
		g2d.draw(rect);
	}

	private void drawRect(Graphics2D g2d, final Point2D pos, final Dimension2D dim) {
		final Rectangle2D rect = new Rectangle2D.Double(pos.getX(), pos.getY(), dim.getWidth(), dim.getHeight());
		g2d.draw(rect);
	}
}
