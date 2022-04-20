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
package net.sourceforge.plantuml.sequencediagram.graphic;

import net.sourceforge.plantuml.cucadiagram.Display;

public final class Page {

	private final double headerHeight;
	private final double newpage1;
	private final double newpage2;
	private final double tailHeight;
	private final double signatureHeight;
	private final Display title;

	@Override
	public String toString() {
		return "headerHeight=" + headerHeight + " newpage1=" + newpage1 + " newpage2=" + newpage2;
	}

	public Page(double headerHeight, double newpage1, double newpage2, double tailHeight,
			double signatureHeight, Display title) {
		if (headerHeight < 0) {
			throw new IllegalArgumentException();
		}
		if (tailHeight < 0) {
			throw new IllegalArgumentException();
		}
		if (signatureHeight < 0) {
			throw new IllegalArgumentException();
		}
		if (newpage1 > newpage2) {
			throw new IllegalArgumentException();
		}
		this.headerHeight = headerHeight;
		this.newpage1 = newpage1;
		this.newpage2 = newpage2;
		this.tailHeight = tailHeight;
		this.signatureHeight = signatureHeight;
		this.title = title;
	}

	public double getHeight() {
		return headerHeight + getBodyHeight() + tailHeight + signatureHeight;
	}

	public double getHeaderRelativePosition() {
		return 0;
	}

	public double getBodyRelativePosition() {
		return getHeaderRelativePosition() + headerHeight;
	}

	public double getBodyHeight() {
		return newpage2 - newpage1;
	}

	public double getTailRelativePosition() {
		return getBodyRelativePosition() + getBodyHeight();
	}

	public double getSignatureRelativePosition() {
		if (displaySignature() == false) {
			return -1;
		}
		return getTailRelativePosition() + tailHeight;
	}

	public boolean displaySignature() {
		return signatureHeight > 0;
	}

	public double getNewpage1() {
		return newpage1;
	}

	public double getNewpage2() {
		return newpage2;
	}

	public double getHeaderHeight() {
		return headerHeight;
	}

	public final Display getTitle() {
		return title;
	}

}
