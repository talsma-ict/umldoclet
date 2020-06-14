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

import java.util.Collection;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.PositionedNote;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.sequencediagram.NoteType;

public class FtileFactoryDelegatorAddNote extends FtileFactoryDelegator {

	public FtileFactoryDelegatorAddNote(FtileFactory factory) {
		super(factory);
	}

	@Override
	public Ftile addNote(Ftile ftile, Swimlane swimlane, Collection<PositionedNote> notes) {
		if (notes.size() == 0) {
			throw new IllegalArgumentException();
		}
		// if (notes.size() > 1) {
		// throw new IllegalArgumentException();
		// }
		ISkinParam skinParam = skinParam();
		if (ftile == null) {
			final PositionedNote note = notes.iterator().next();
			if (note.getColors() != null) {
				skinParam = note.getColors().mute(skinParam);
			}
			return new FtileNoteAlone(skinParam.shadowing(null), note.getDisplay(), skinParam,
					note.getType() == NoteType.NOTE, swimlane);
		}
		return FtileWithNoteOpale.create(ftile, notes, skinParam, true);
	}
}
