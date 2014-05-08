package com.odc.pdfextractor.graphics;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.BoxLayout;

import com.odc.pdfextractor.model.DataTable;
import com.odc.pdfextractor.model.DocumentLocation;

public class PdfExtractorViewer extends Frame {

	private final DocumentViewer display;
	
	public PdfExtractorViewer(DocumentLocation loc, List<DataTable> dataTable) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension d = toolkit.getScreenSize();
		setSize((int) Math.round(d.getWidth() / 2),(int) Math.round(d.getHeight() * .8));
		display = new DocumentViewer(loc, this.getSize(), dataTable);
		setSize((int) Math.round(d.getWidth() / 2),(int) Math.round(d.getHeight() * .9));
	}
	
	public static void display(DocumentLocation loc, List<DataTable> data) {
		final PdfExtractorViewer app = new PdfExtractorViewer(loc, data);
		Panel buttons = new Panel();
		final DocumentViewer F1 = app.display;
		F1.setVisible(true);
		app.addComponentListener(new ComponentAdapter() {
			@Override
		    public void componentResized(ComponentEvent e)
		    {
		        F1.redrawCanvas(app.getSize());
		    }
		});
		
		
		// F1.setResizable(false);
		Button prev = new Button("PREV");
		prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				F1.decrementPage();
			}
			
		});
		Button next = new Button("NEXT");
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				F1.incrementPage();
			}
			
		});
		Button selectHeaders = new Button("Select Headers");
		selectHeaders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				F1.selectHeaders();
			}
			
		});
		BoxLayout layout = new BoxLayout(app, BoxLayout.Y_AXIS);
		app.setLayout(layout);
		app.add(F1);
		buttons.add(prev);
		buttons.add(next);
		buttons.add(selectHeaders);
		System.out.println(buttons.getSize().getHeight());
		app.add(buttons);

		app.setVisible(true);
	}
}
