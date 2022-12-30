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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.ForkStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;

public final class FtileFactoryDelegatorCreateParallel extends FtileFactoryDelegator {

	public FtileFactoryDelegatorCreateParallel(FtileFactory factory) {
		super(factory);
	}

	@Override
	public Ftile createParallel(List<Ftile> all, ForkStyle style, String label, Swimlane in, Swimlane out) {

		AbstractParallelFtilesBuilder builder;
		if (style == ForkStyle.SPLIT)
			builder = new ParallelBuilderSplit(skinParam(), getStringBounder(), all);
		else if (style == ForkStyle.MERGE)
			builder = new ParallelBuilderMerge(skinParam(), getStringBounder(), all);
		else if (style == ForkStyle.FORK)
			builder = new ParallelBuilderFork(skinParam(), getStringBounder(), label, in, out, all);
		else
			throw new IllegalStateException();

		final Ftile inner = super.createParallel(builder.list99, style, label, in, out);
		return builder.build(inner);
	}

}
