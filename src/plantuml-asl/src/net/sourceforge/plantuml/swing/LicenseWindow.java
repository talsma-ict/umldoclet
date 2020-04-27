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
package net.sourceforge.plantuml.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

import net.sourceforge.plantuml.version.License;
import net.sourceforge.plantuml.version.PSystemVersion;
import net.sourceforge.plantuml.version.Version;

class LicenseWindow extends JFrame {

	/*
	 * - the PlantUML version - the Dot version - the PlantUML authors - the PlantUML license
	 */
	public LicenseWindow() {
		super();
		setIconImage(PSystemVersion.getPlantumlSmallIcon2());

		this.setTitle("Licence PlantUML (" + Version.versionString() + ")");

		getContentPane().add(getNorthLabel(), BorderLayout.NORTH);
		final List<String> list = new ArrayList<String>(License.getCurrent().getTextFull());
		getContentPane().add(getJComponent(list), BorderLayout.CENTER);
		getContentPane().add(getSouthLabel(), BorderLayout.SOUTH);

		setSize(800, 600);
		this.setLocationRelativeTo(this.getParent());
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private JComponent getNorthLabel() {
		final JLabel text = new JLabel("PlantUML (" + Version.versionString() + ")");
		final Font font = text.getFont().deriveFont(Font.BOLD, (float) 20.0);
		text.setFont(font);
		final JPanel ptext = new JPanel();
		ptext.add(text);

		final JLabel icon = new JLabel(new ImageIcon(PSystemVersion.getPlantumlImage()));

		final JPanel result = new JPanel(new BorderLayout());
		result.add(ptext, BorderLayout.CENTER);
		result.add(icon, BorderLayout.EAST);

		return result;
	}

	private JComponent getSouthLabel() {
		final JPanel result = new JPanel();
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});
		result.add(ok);
		return result;
	}

	private JComponent getJComponent(List<String> lines) {
		final StringBuilder sb = new StringBuilder("<html>");
		for (String s : lines) {
			sb.append(s + "</b></i></u>");
			sb.append("<br>");
		}
		sb.append("</html>");
		final JEditorPane text = new JEditorPane("text/html", sb.toString());
		text.setEditable(false);
		CompoundBorder border = new CompoundBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED),
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
		border = new CompoundBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getBackground()), border);

		text.setBorder(border);

		final JScrollPane jScrollPane = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jScrollPane.getVerticalScrollBar().setValue(0);
			}
		});

		return jScrollPane;
	}

	public static void main(String arg[]) {
		new LicenseWindow();
	}

}
