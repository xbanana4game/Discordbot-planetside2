package jp.banana.planetside2.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class Planetside2APITest {

	@Test
	public void getOutfitName() {
		try {
			String name = Planetside2API.getOutfitName("37512998641471064");
			assertEquals(name, "Japan VS Guardians");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getFacilityName() {
		try {
			String name = Planetside2API.getFacilityName("5100");
			assertEquals(name, "Indar Excavation Site");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			String name = Planetside2API.getFacilityName("7000");
			assertEquals(name, "Tawrich Tech Plant");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getCharacterName() {
		try {
			String name = Planetside2API.getCharacterName("5428366106639191985");
			assertEquals(name, "hhuftyfyt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getVehicleName() {
		String name = Planetside2API.getSingleton().getVehicleName(1);
		assertEquals(name, "Flash");
	}
	
	@Test
	public void isOutfitMember() {
		Planetside2API.getSingleton().getOutfitMember();
		boolean outfit = Planetside2API.getSingleton().isOutfitMember("5428366106639191985");
		assertEquals(outfit, true);
	}
	
	@Test
	public void getWeaponName() {
//		Planetside2API.getSingleton().getWeaponInfo();
		Planetside2API.getSingleton().outputCsvWeaponInfo();
		String name = Planetside2API.getSingleton().getWeaponName(705);
		assertEquals(name, "Spear Anti-Vehicle Phalanx Turret");
	}
}
