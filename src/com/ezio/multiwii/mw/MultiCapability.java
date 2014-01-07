package com.ezio.multiwii.mw;

public class MultiCapability {
	public boolean RXBind = false;
	public boolean Motors = false;
	public boolean Flaps = false;

	public boolean ByMis = false; // Firmware by Mis, wyswietla "by Miœ" na
									// pierwszej stronie oraz zmnieia MinCommand
									// na FS RTH altitude w Misc

	public boolean Nav = false;

	public int getNavVersion(int MWCapability) {
		return (MWCapability >> 28);

	}
}
