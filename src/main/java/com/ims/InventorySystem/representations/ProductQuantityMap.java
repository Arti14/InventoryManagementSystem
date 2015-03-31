package com.ims.InventorySystem.representations;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ProductQuantityMap {
	
	@JsonProperty
	private Map<Long, Integer> map;
	
	@JsonIgnore
	public Map<Long, Integer> getMap() {
		return map;
	}
}
