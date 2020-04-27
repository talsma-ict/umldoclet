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
package net.sourceforge.plantuml.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Graph1 {

	private final Board board;
	private final int widthCell = 40;
	private final int heightCell = 40;

	public Graph1(Board board) {
		this.board = board;
	}

	public BufferedImage createBufferedImage() {
		final BufferedImage im = new BufferedImage(widthCell * 15, heightCell * 15, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = im.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, im.getWidth(), im.getHeight());

		g2d.setColor(Color.BLACK);
		for (ANode n : board.getNodes()) {
			final int x = board.getCol(n) * widthCell;
			final int y = n.getRow() * heightCell;
			g2d.drawString(n.getCode(), x + 5, y + heightCell / 2 - 5);
			g2d.drawOval(x, y, widthCell / 2, heightCell / 2);
		}

		for (ALink link : board.getLinks()) {
			final ANode n1 = link.getNode1();
			final ANode n2 = link.getNode2();
			final int x1 = 10 + board.getCol(n1) * widthCell;
			final int y1 = 10 + n1.getRow() * heightCell;
			final int x2 = 10 + board.getCol(n2) * widthCell;
			final int y2 = 10 + n2.getRow() * heightCell;
			g2d.drawLine(x1, y1, x2, y2);

		}

		return im;

	}

}
