package invsUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import Utils.ItemUtil;

public class InvEvents {
	
	public static List<Inv> invs = new ArrayList<>();
	
	
	public static final Inv example = new Inv("&dДисплейНэйм") {
		@Override public void displItems(Inventory inv) {
			inv.setItem(13, ItemUtil.create(Material.BRICK, 1, "&6Кирпич.", "&fОднострочный лор! &aУра&f!", null, 0));
		}
		@Override public void click(InventoryClickEvent e) {
			
		}
	};/*
	public static final Inv mngshdsjkhdfm = new Inv() {
		@Override public void displItems(Inventory inv) {
			
		}
		@Override public void click(InventoryClickEvent e) {
			
		}
	};/*
	public static final Inv mngshdsjkhdfm = new Inv() {
		@Override public void displItems(Inventory inv) {
			
		}
		@Override public void click(InventoryClickEvent e) {
			
		}
	};/*
	public static final Inv mngshdsjkhdfm = new Inv() {
		@Override public void displItems(Inventory inv) {
			
		}
		@Override public void click(InventoryClickEvent e) {
			
		}
	};
	public static final Inv mngshdsjkhdfm = new Inv() {
		@Override public void displItems(Inventory inv) {
			
		}
		@Override public void click(InventoryClickEvent e) {
			
		}
	};
	public static final Inv mngshdsjkhdfm = new Inv() {
		@Override public void displItems(Inventory inv) {
			
		}
		@Override public void click(InventoryClickEvent e) {
			
		}
	};*/
}