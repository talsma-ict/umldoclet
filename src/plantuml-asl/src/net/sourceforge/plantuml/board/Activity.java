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
package net.sourceforge.plantuml.board;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Activity {

	private final BNode node;
	private final ISkinParam skinParam;
	private BNode cursor;

	private final BoardDiagram boardDiagram;

	public Activity(BoardDiagram boardDiagram, String name, ISkinParam skinParam) {
		this.boardDiagram = boardDiagram;
		this.node = new BNode(0, name);
		this.skinParam = skinParam;
		this.cursor = this.node;
	}

	public TextBlock getBox() {
		return new CardBox(Display.create(node.getName()), skinParam);
	}

	public void addRelease(int stage, String label) {
		if (stage <= 0) {
			throw new IllegalArgumentException();
		}
		final BNode newNode = new BNode(stage, label);
		while (true) {
			if (stage > cursor.getStage()) {
				cursor.addChild(newNode);
				cursor = newNode;
				return;
			}
			cursor = cursor.getParent();
		}
	}

	private BArray array;

	private BArray getArray() {
		if (array == null) {
			node.computeX(new AtomicInteger());
			array = new BArray();
			node.initBarray(array);
		}
		return array;
	}

	public double getFullWidth() {
		final BArray array = getArray();
		return (array.getMaxX() + 1) * PostIt.getWidth();
	}

	public int getMaxStage() {
		final BArray array = getArray();
		return array.getMaxY();
	}

	public void drawMe(UGraphic ug) {

		getBox().drawU(ug);

		final BArray array = getArray();

		for (BNode node : array) {
			final double dx = node.getX() * PostIt.getWidth();
			final double dy = node.getStage() * PostIt.getHeight();
			ug.apply(new UTranslate(dx, dy));

			CardBox box = new CardBox(Display.create(node.getName()), skinParam);
			box.drawU(ug.apply(new UTranslate(dx, dy)));

		}

//		for (Entry<Integer, List<PostIt>> ent : postits.entrySet()) {
//			final int line = ent.getKey();
//			final List<PostIt> list = ent.getValue();
//			double dy = boardDiagram.getStageY(ug.getStringBounder(), line);
//			for (PostIt postit : list) {
//				postit.getCard().drawU(ug.apply(UTranslate.dy(dy)));
//				dy += PostIt.getHeight(ug.getStringBounder());
//			}
//
//		}

	}

}
