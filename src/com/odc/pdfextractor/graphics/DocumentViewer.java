package com.odc.pdfextractor.graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.odc.pdfextractor.model.DataTable;
import com.odc.pdfextractor.model.DocumentLocation;
import com.odc.pdfextractor.model.Header;
import com.odc.pdfextractor.model.StringLocation;

public class DocumentViewer extends JPanel {
	
	StringLocationViewer[] pages;
	int currPage = 0;
	

	public DocumentViewer(DocumentLocation loc, Dimension dimension, List<DataTable> headerList) {
		List<StringLocation> locations = loc.getLocations();
		setSize(dimension.width,(int) Math.round(dimension.getHeight()));
		pages = new StringLocationViewer[locations.size()];
		Map<Integer, List<DataTable>> headersByPage = getHeaderByPageMap(loc.getLocations().size());
		for (DataTable header : headerList) {
			headersByPage.get(header.getPage()).add(header);
		}
		for (int i = 0; i < locations.size(); i++) {
			pages[i] = new StringLocationViewer(locations.get(i), dimension, headersByPage.get(i));
		}
		setPage(0);

	}




	private Map<Integer, List<DataTable>> getHeaderByPageMap(int pages) {
		Map<Integer, List<DataTable>> headersByPage = new HashMap<Integer, List<DataTable>>();
		for(int i = 0; i < pages; i++) {
			headersByPage.put(i, new ArrayList<DataTable>());
		}
		return headersByPage;
	}


	
	
	protected void redrawCanvas(Dimension dimension) {
		Dimension d = this.getSize();
		pages[currPage].setSize(d.width, (int) Math.round(d.height) - 25);
		pages[currPage].repaint();
	}

	protected void decrementPage() {
		setPage(currPage - 1);	
	}

	public void setPage(int page) {
		if (0 <= page && page < pages.length) {
			remove(pages[currPage]);
			currPage = page;
			redrawCanvas(this.getSize());
			add(pages[page]);	
			
		}
	}
	
	public void incrementPage() {
		setPage(currPage + 1);
	}




	public void selectHeaders() {
		pages[currPage].selectHeaders();
	}
	
	
}
