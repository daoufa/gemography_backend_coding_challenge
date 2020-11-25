package org.GI3.entity;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
	private List<Repo> items;

	public List<Repo> getItems() {
		return items;
	}

	public void setItems(List<Repo> items) {
		this.items = items;
	}

}
