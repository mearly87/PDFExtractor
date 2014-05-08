package com.odc.pdfextractor.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import com.odc.pdfextractor.enumeration.HeaderType;
import com.odc.pdfextractor.model.DataTable;
import com.odc.pdfextractor.model.Header;
import com.odc.pdfextractor.model.StringLocation;

public class StringLocationViewer extends Canvas {
	
	StringLocation doc;	
	
	List<DataTable> data;
	StringLocation selectedString;
	DataTable selectedData;
	
	Point start;
	Point end;

	private Rectangle[] crossHairs;
	private int newLeft = -1;
	private int newRight = -1;
	private int newTop = -1;
	private int newBottom = -1;
	private int selectedCrossHair = -1;
	
	private void setStart(Point mousePosition) {
		this.start = mousePosition;
	}
	

	private void setEnd(Point mousePosition) {
		this.end = mousePosition;
	}
	
	public StringLocationViewer(final StringLocation doc, Dimension d, List<DataTable> headers) {
		this.doc = doc;
		this.data = headers;
		setSize(d.width, d.height);
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e){
				if (selectedCrossHair != -1) {
					if (selectedCrossHair == 0) {
						newBottom = selectedData.getBottom();
						newRight = selectedData.getRight();
					} else if (selectedCrossHair == 1) {
						newBottom = selectedData.getBottom();
						newLeft = selectedData.getLeft();
					} else if (selectedCrossHair == 3) {
						newTop = selectedData.getTop();
						newLeft = selectedData.getLeft();
					} else if (selectedCrossHair == 2) {
						newTop = selectedData.getTop();
						newRight = selectedData.getRight();
					}
					return;
				}
				
				
				setStart(StringLocationViewer.this.getMousePosition());
				selectedString = null;
				Dimension d = StringLocationViewer.this.getSize();
				double xScale = d.getWidth()  / (doc.getRight() + doc.getLeft());
				double yScale = d.getHeight() / (doc.getBottom() + doc.getTop());
				selectedData = null;
				for (DataTable table : data) {
					if (table!= null && table.containsPoint(start, xScale, yScale)) {
						selectedData = table;
					}
				}
				StringLocationViewer.this.update(StringLocationViewer.this.getGraphics());
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				Dimension d = StringLocationViewer.this.getSize();
				int x = -1;
				int y = -1;
				int width = -1;
				int height = -1;
				if (end != null) {
					x = Math.min(end.x, start.x);
					y = Math.min(end.y, start.y);
					width = Math.abs(end.x - start.x);
					height = Math.abs(end.y - start.y);
				}

				double xScale = d.getWidth()  / (doc.getRight() + doc.getLeft());
				double yScale = d.getHeight() / (doc.getBottom() + doc.getTop());
				Point mousePos = StringLocationViewer.this.getMousePosition();
				if (selectedCrossHair != -1) {
					if (newLeft == -1) {
						x = mousePos.x;
						width = (int) ((newRight * xScale) - x);
						if (width < 0) {
							x += width;
							width = -width;
						}
					} else {
						x = (int) (newLeft * xScale);
						width = mousePos.x - x;
						if (width < 0) {
							x += width;
							width = -width;
						}
					}
					if (newTop == -1) {
						y = mousePos.y;
						height = (int) (newBottom * yScale) - y;
						if (height < 0) {
							y += height;
							height = -height;
						}
					} else {
						y = (int) (newTop * yScale);
						height = mousePos.y - y;
						if (height < 0) {
							y += height;
							height = -height;
						}
					}
				}
				StringLocation selectedLoc = doc.getLocation((int) (x /xScale), (int) (y / yScale), (int) ((x + width) / xScale), (int) ((y + height) / yScale));
				selectedString = new Header(HeaderType.UNKOWN, selectedLoc);
				Graphics g  = StringLocationViewer.this.getGraphics();
				Font font = new Font("Dialog", Font.PLAIN, 9);
				g.setFont(font);
				g.setColor(Color.red);
				StringLocationViewer.this.update(StringLocationViewer.this.getGraphics());
				selectedLoc.draw(g, 0, 0, xScale, yScale);
				start = null;
				end = null;
				newLeft = -1;
				newRight = -1;
				newTop = -1;
				newBottom = -1;
				selectedCrossHair = -1;
				
			}
			
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			

			@Override
			public void mouseDragged(MouseEvent e){
				Dimension d = StringLocationViewer.this.getSize();
				if (selectedCrossHair != -1) {
					double xScale = d.getWidth()  / (doc.getRight() + doc.getLeft());
					double yScale = d.getHeight() / (doc.getBottom() + doc.getTop());
					Point mousePos = StringLocationViewer.this.getMousePosition();
					int left = 0;
					int top = 0;
					int height = 0;
					int width = 0;
					if (newLeft == -1) {
						left = mousePos.x;
						width = (int) ((newRight * xScale) - left);
						if (width < 0) {
							left += width;
							width = -width;
						}
					} else {
						left = (int) (newLeft * xScale);
						width = mousePos.x - left;
						if (width < 0) {
							left += width;
							width = -width;
						}
					}
					if (newTop == -1) {
						top = mousePos.y;
						height = (int) (newBottom * yScale) - top;
						if (height < 0) {
							top += height;
							height = -height;
						}
					} else {
						top = (int) (newTop * yScale);
						height = mousePos.y - top;
						if (height < 0) {
							top += height;
							height = -height;
						}
					}
					StringLocationViewer.this.update(StringLocationViewer.this.getGraphics());
					StringLocationViewer.this.getGraphics().drawRect(left,top,width,height);
					return;
				}
				setEnd(StringLocationViewer.this.getMousePosition());
				if (end == null) {
					return;
				}
				int x = Math.min(end.x, start.x);
				int y = Math.min(end.y, start.y);
				int width = Math.abs(end.x - start.x);
				int height = Math.abs(end.y - start.y);
				StringLocationViewer.this.update(StringLocationViewer.this.getGraphics());
				StringLocationViewer.this.getGraphics().drawRect(x, y, width, height);
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if (crossHairs == null) {
					StringLocationViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}
				selectedCrossHair = -1;
				int i = 0;
				for (Rectangle r : crossHairs) {
					if (r.contains(StringLocationViewer.this.getMousePosition())) {
						StringLocationViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						selectedCrossHair = i;
					}
					i++;
				}
				if (selectedCrossHair == -1) {
					StringLocationViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}

			

		});
	}
	
	public void paint(Graphics g) {
		Dimension d = this.getSize();
		Image offscreen = createImage(d.width, d.height);
		Graphics offgc = offscreen.getGraphics();
		
		Font font = new Font("Dialog", Font.PLAIN, 9);
		offgc.setColor(Color.white);
		offgc.fillRect(0, 0, d.width, d.height);
		offgc.setColor(Color.black);
		
		offgc.setFont(font);
		double xScale = d.getWidth()  / (doc.getRight() + doc.getLeft());
		double yScale = d.getHeight() / (doc.getBottom() + doc.getTop());
		doc.draw(offgc, 0, 0, xScale, yScale);
		offgc.setColor(Color.blue);
		for (DataTable dataTable: data) {
			dataTable.draw(offgc, 0, 0, xScale, yScale);
		}
		crossHairs = null;
		if (selectedData!= null) {
			crossHairs = selectedData.draw(offgc, 0, 0, xScale, yScale, true);
		}
		g.drawImage(offscreen, 0, 0, this);
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public StringLocation getSelected() {
		return selectedString;
	}


	public StringLocation selectHeaders() {
		// if(selected != null) data.add(new DataTable(new Header(ColumnTypeselected, new ArrayList<StringLocation>());
		this.update(this.getGraphics());
		return selectedString;
	}

}
