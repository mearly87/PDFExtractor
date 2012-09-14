package com.odc.pdfextractor.model;

import java.awt.Color;
import java.awt.Graphics;

import com.odc.pdfextractor.enumeration.HeaderType;

public class Header extends StringLocation {

	private final HeaderType type;
	
	public Header(HeaderType type, StringLocation header) {
		super(header.getLocations());
		this.type = type;
	}

	public HeaderType getType() {
		return type;
	}

	public void draw(Graphics g, int xOffSet, int yOffSet, double xScale, double yScale) {
		Color temp = g.getColor();
		g.setColor(type.getColor());
		super.draw(g, xOffSet, yOffSet, xScale, yScale);
		g.setColor(temp);
	}
}
