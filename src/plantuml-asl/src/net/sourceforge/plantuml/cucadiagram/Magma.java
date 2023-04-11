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
package net.sourceforge.plantuml.cucadiagram;

import java.util.List;

import net.atmp.CucaDiagram;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.abel.LinkArg;
import net.sourceforge.plantuml.decoration.LinkDecor;
import net.sourceforge.plantuml.decoration.LinkType;

public class Magma {

	private final CucaDiagram diagram;
	private final List<Entity> standalones;
	private final LinkType linkType = new LinkType(LinkDecor.NONE, LinkDecor.NONE).getInvisible();

	public Magma(CucaDiagram system, List<Entity> standalones) {
		this.diagram = system;
		this.standalones = standalones;
	}

	public void putInSquare() {
		final SquareLinker<Entity> linker = new SquareLinker<Entity>() {
			public void topDown(Entity top, Entity down) {
				diagram.addLink(new Link(diagram.getEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(),
						top, down, linkType, LinkArg.noDisplay(2)));
			}

			public void leftRight(Entity left, Entity right) {
				diagram.addLink(new Link(diagram.getEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(),
						left, right, linkType, LinkArg.noDisplay(1)));
			}
		};
		new SquareMaker<Entity>().putInSquare(standalones, linker);
	}

	public Entity getContainer() {
		final Entity parent = standalones.get(0).getParentContainer();
		if (parent == null)
			return null;

		return parent.getParentContainer();
	}

	public boolean isComplete() {
		final Entity parent = getContainer();
		if (parent == null)
			return false;

		return parent.countChildren() == standalones.size();
	}

	private int squareSize() {
		return SquareMaker.computeBranch(standalones.size());
	}

	private Entity getTopLeft() {
		return standalones.get(0);
	}

	private Entity getBottomLeft() {
		int result = SquareMaker.getBottomLeft(standalones.size());
		return standalones.get(result);
	}

	private Entity getTopRight() {
		final int s = squareSize();
		return standalones.get(s - 1);
	}

	@Override
	public String toString() {
		return standalones.get(0).getParentContainer() + " " + standalones.toString() + " " + isComplete();
	}

	public void linkToDown(Magma down) {
		diagram.addLink(new Link(diagram.getEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(),
				this.getBottomLeft(), down.getTopLeft(), linkType, LinkArg.noDisplay(2)));

	}

	public void linkToRight(Magma right) {
		diagram.addLink(new Link(diagram.getEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(),
				this.getTopRight(), right.getTopLeft(), linkType, LinkArg.noDisplay(1)));
	}

}
