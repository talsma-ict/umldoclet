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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;

public class Cluster implements Clusterable {

	private static int CPT = 1;

	private final Cluster parent;
	private final Collection<Block> blocs = new ArrayList<>();
	private final Collection<Cluster> children = new ArrayList<>();
	private final int uid = CPT++;
	private double x;
	private double y;
	private double width;
	private double height;
	
	private final double titleWidth;
	private final double titleHeight;

//	public Cluster(Cluster parent) {
//		this(parent, 100, 20);
//	}
//	
	public Cluster(Cluster parent, double titleWidth, double titleHeight) {
		this.parent = parent;
		this.titleWidth = titleWidth;
		this.titleHeight = titleHeight;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	public Collection<Cluster> getSubClusters() {
		return Collections.unmodifiableCollection(children);
	}

	public Collection<Block> getRecursiveContents() {
		final Collection<Block> result = new ArrayList<>();
		addContentRecurse(result);
		return Collections.unmodifiableCollection(result);
	}

	private void addContentRecurse(Collection<Block> result) {
		result.addAll(blocs);
		for (Cluster c : children) {
			c.addContentRecurse(result);
		}

	}

	public int getUid() {
		return uid;
	}

	public void addBloc(Block b) {
		this.blocs.add(b);
	}

	public Cluster getParent() {
		return parent;
	}

	public Collection<Block> getContents() {
		return Collections.unmodifiableCollection(blocs);
	}

	public Block getBlock(int uid) {
		for (Block b : blocs) {
			if (b.getUid() == uid) {
				return b;
			}
		}
		for (Cluster sub : children) {
			final Block result = sub.getBlock(uid);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	public XPoint2D getPosition() {
		return new XPoint2D(x, y);
	}

	public XDimension2D getSize() {
		return new XDimension2D(width, height);
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final void setY(double y) {
		this.y = y;
	}

	public final void setWidth(double width) {
		this.width = width;
	}

	public final void setHeight(double height) {
		this.height = height;
	}

	public final double getTitleWidth() {
		return titleWidth;
	}

	public final double getTitleHeight() {
		return titleHeight;
	}

	public void moveSvek(double deltaX, double deltaY) {
		throw new UnsupportedOperationException();
	}


}
