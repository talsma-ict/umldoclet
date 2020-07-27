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
package net.sourceforge.plantuml.postit;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Area implements Elastic {

	private final String title;
	private final char id;

	private Dimension2D minimumDimension;

	private final List<PostIt> postIts = new ArrayList<PostIt>();

	public Area(char id, String title) {
		this.id = id;
		this.title = title;
	}

	public char getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Dimension2D getMinimumDimension() {
		return minimumDimension;
	}

	public void setMinimunDimension(Dimension2D minimumDimension) {
		this.minimumDimension = minimumDimension;
	}

	public Dimension2D getDimension() {
		throw new UnsupportedOperationException();
	}

	public double heightWhenWidthIs(double width, StringBounder stringBounder) {
		final AreaLayoutFixedWidth layout = new AreaLayoutFixedWidth(width);
		final Map<PostIt, Point2D> pos = layout.getPositions(postIts, stringBounder);
		double max = 10;
		for (Map.Entry<PostIt, Point2D> ent : pos.entrySet()) {
			final double y = ent.getKey().getDimension(stringBounder).getHeight() + ent.getValue().getY();
			max = Math.max(max, y);
		}

		return max + 10;
	}

	public double widthWhenHeightIs(double height, StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}

	public void add(PostIt postIt) {
		postIts.add(postIt);
	}

	public void drawU(UGraphic ug, double width) {
		final AreaLayout layout = new AreaLayoutFixedWidth(width);
		final Map<PostIt, Point2D> pos = layout.getPositions(postIts, ug.getStringBounder());
		for (Map.Entry<PostIt, Point2D> ent : pos.entrySet()) {
			final UGraphic ugTranslated = ug.apply(new UTranslate(ent.getValue().getX(), ent.getValue().getY()));
			ent.getKey().drawU(ugTranslated);
		}

	}

}
