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
package net.sourceforge.plantuml.evalex;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Snipset1 {

	public static void main(String[] args) {
		Number result = null;

		Expression expression = new Expression("1+1/3");
		result = expression.eval();
		expression.setPrecision(2);
		result = expression.eval();

		result = new Expression("(3.4 + -4.1)/2").eval();

		result = new Expression("SQRT(a^2 + b^2)").with("a", "2.4").and("b", "9.253").eval();

		BigDecimal a = new BigDecimal("2.4");
		BigDecimal b = new BigDecimal("9.235");
		result = new Expression("SQRT(a^2 + b^2)").with("a", a).and("b", b).eval();

		result = new Expression("2.4/PI").setPrecision(128).setRoundingMode(RoundingMode.UP).eval();

		result = new Expression("random() > 0.5").eval();

		result = new Expression("not(x<7 || sqrt(max(x,9,3,min(4,3))) <= 3)").with("x", "22.9").eval();
		System.err.println("foo1=" + result);

		result = new Expression("log10(100)").eval();
	}
}
