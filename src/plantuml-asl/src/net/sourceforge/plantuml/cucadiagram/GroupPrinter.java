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
package net.sourceforge.plantuml.cucadiagram;

import java.io.IOException;
import java.io.PrintWriter;

import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.security.SFile;

public class GroupPrinter {

	private final PrintWriter pw;

	private GroupPrinter(PrintWriter pw) {
		this.pw = pw;
	}

	private void printGroup(IGroup group) {
		pw.println("<table border=1 cellpadding=8 cellspacing=0>");
		pw.println("<tr>");
		pw.println("<td bgcolor=#DDDDDD>");
		pw.println(group.getCodeGetName());
		pw.println("<tr>");
		pw.println("<td>");
		if (group.getLeafsDirect().size() == 0) {
			pw.println("<i>No direct leaf</i>");
		} else {
			for (ILeaf leaf : group.getLeafsDirect()) {
				pw.println("<ul>");
				printLeaf(leaf);
				pw.println("</ul>");
			}
		}
		pw.println("</td>");
		pw.println("</tr>");
		if (group.getChildren().size() > 0) {
			pw.println("<tr>");
			pw.println("<td>");
			for (IGroup g : group.getChildren()) {
				pw.println("<br>");
				printGroup(g);
				pw.println("<br>");
			}
			pw.println("</td>");
			pw.println("</tr>");
		}
		pw.println("</table>");
	}

	private void printLeaf(ILeaf leaf) {
		pw.println("<li>" + leaf.getCodeGetName());
	}

	public static void print(SFile f, IGroup rootGroup) {
		try (PrintWriter pw = f.createPrintWriter()) {
			pw.println("<html>");
			new GroupPrinter(pw).printGroup(rootGroup);
			pw.println("</html>");
		} catch (IOException e) {
			Logme.error(e);
		}
	}

}
