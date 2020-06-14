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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.ForkStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileHeightFixed;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;

public class FtileFactoryDelegatorCreateParallel extends FtileFactoryDelegator {

	private final double spaceArroundBlackBar = 20;
	private final double xMargin = 14;

	public FtileFactoryDelegatorCreateParallel(FtileFactory factory) {
		super(factory);
	}

	private Ftile allOverlapped(Swimlane swimlane, List<Ftile> all, ForkStyle style, String label) {
		return new FtileForkInnerOverlapped(all);
	}

	@Override
	public Ftile createParallel(Swimlane swimlane, List<Ftile> all, ForkStyle style, String label) {

		final Dimension2D dimSuper = super.createParallel(swimlane, all, style, label).calculateDimension(getStringBounder());
		final double height1 = dimSuper.getHeight() + 2 * spaceArroundBlackBar;

		final List<Ftile> list = new ArrayList<Ftile>();
		for (Ftile tmp : all) {
			list.add(new FtileHeightFixed(FtileUtils.addHorizontalMargin(tmp, xMargin), height1));
		}
		final Ftile inner = super.createParallel(swimlane, list, style, label);

		ParallelFtilesBuilder builder;

		if (style == ForkStyle.SPLIT) {
			builder = new ParallelBuilderSplit2(skinParam(), getStringBounder(), list, inner, swimlane);
		} else if (style == ForkStyle.MERGE) {
			builder = new ParallelBuilderMerge(skinParam(), getStringBounder(), list, inner, swimlane);
		} else if (style == ForkStyle.FORK) {
			builder = new ParallelBuilderFork(skinParam(), getStringBounder(), list, inner, swimlane, label);
		} else {
			throw new IllegalStateException();
		}
		return builder.build();
	}

}
