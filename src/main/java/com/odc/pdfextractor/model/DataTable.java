package com.odc.pdfextractor.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataTable extends StringLocation implements Map<Header, StringLocation> {
	
	private final Map<Header, StringLocation> headersToData;
	
	
	public DataTable(Map<Header, StringLocation> headersToData) {
		super(union(headersToData.keySet(),headersToData.values()));
		this.headersToData = headersToData;
	}


	public static List<StringLocation> union(Collection<? extends StringLocation> list1, Collection<? extends StringLocation> list2) {
		List<StringLocation> result = new ArrayList<StringLocation>(list1);
		result.addAll(list2);
		return result;
	}

	@Override
	public boolean isEmpty() {
		return headersToData.isEmpty();
	}


	@Override
	public boolean containsKey(Object key) {
		return headersToData.containsKey(key);
	}


	@Override
	public boolean containsValue(Object value) {
		return headersToData.containsValue(value);
	}


	@Override
	public StringLocation get(Object key) {
		return headersToData.get(key);
	}


	@Override
	public StringLocation put(Header key, StringLocation value) {
		return headersToData.put(key, value);
	}


	@Override
	public StringLocation remove(Object key) {
		return headersToData.remove(key);
	}


	@Override
	public void putAll(Map m) {
		headersToData.putAll(m);
	}


	@Override
	public void clear() {
		headersToData.clear();
	}


	@Override
	public Set<Header> keySet() {
		return headersToData.keySet();
	}


	@Override
	public Collection<StringLocation> values() {
		return headersToData.values();
	}


	@Override
	public Set entrySet() {
		return headersToData.entrySet();
	}
	
	@Override
	public void draw(Graphics g, int xOffSet, int yOffSet, double xScale, double yScale) {
		draw(g, xOffSet, yOffSet,xScale, yScale, false);
	}
	
	public Rectangle[] draw(Graphics g, int xOffSet, int yOffSet, double xScale, double yScale, boolean isSelected) {
		Color temp = g.getColor();
		int left = (int)(this.getLeft() * xScale);
		int top = (int) (this.getTop() * yScale);
		int width = (int) ((this.getRight() - this.getLeft()) * xScale);
		int height = (int) ((this.getBottom() - this.getTop()) * yScale);
		Rectangle[] crossHairs = null;
		if (isSelected) {
			g.setColor(Color.red);
			crossHairs = new Rectangle[4];
			g.drawRect(left-2, top-2, 4, 4);
			g.drawRect(left + width - 2, top - 2, 4, 4);
			g.drawRect(left -2, top + height - 2, 4, 4);
			g.drawRect(left + width - 2, top + height - 2, 4, 4);
			crossHairs[0]= new Rectangle(left-2, top-2, 4, 4);
			crossHairs[1]= new Rectangle(left + width - 2, top - 2, 4, 4);
			crossHairs[2]= new Rectangle(left -2, top + height - 2, 4, 4);
			crossHairs[3]= new Rectangle(left + width - 2, top + height - 2, 4, 4);
			g.drawRect(left, top, width, height);			
		}
		g.drawRect((int) (this.getLeft() * xScale), (int) (this.getTop() * yScale), (int) ((this.getRight() - this.getLeft()) * xScale), (int) ((this.getBottom() - this.getTop()) * yScale));
		for (Header header : headersToData.keySet()) {
			g.setColor(header.getType().getColor());
			header.draw(g, xOffSet, yOffSet, xScale, yScale);
			headersToData.get(header).draw(g, xOffSet, yOffSet, xScale, yScale);
		}
		g.setColor(temp);
		return crossHairs;
	}


	public boolean containsPoint(Point mousePosition, double xScale, double yScale) {
		int x = (int) (mousePosition.x / xScale);
		int y = (int) (mousePosition.y / yScale);		
		if (this.getLeft() < x && x < this.getRight() && this.getTop() < y && y < this.getBottom()) {
			return true;
		}
		return false;
	}
}
