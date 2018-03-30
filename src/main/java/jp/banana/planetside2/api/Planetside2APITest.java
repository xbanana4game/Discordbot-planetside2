package jp.banana.planetside2.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class Planetside2APITest {

	@Test
	public void test() {
		try {
			String name = Planetside2API.getOutfitName("37512998641471064");
			assertEquals(name, "Japan VS Guardians");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() {
		try {
			String name = Planetside2API.getFacilityName("5100");
			assertEquals(name, "Indar Excavation Site");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_getFacilityName() {
		try {
			String name = Planetside2API.getFacilityName("7000");
			assertEquals(name, "Tawrich Tech Plant");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test3() {
		try {
			String name = Planetside2API.getCharacterName("5428366106639191985");
			assertEquals(name, "hhuftyfyt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test4() {
		Planetside2API.getSingleton().getFacilityData();
	}
	
	@Test
	public void test5() {
		String name = Planetside2API.getSingleton().getVehicleName(1);
		assertEquals(name, "Flash");
	}
	
	@Test
	public void test6() {
		Planetside2API.getSingleton().getJVSGMember();
		boolean jvsg = Planetside2API.getSingleton().isJVSGMember("5428366106639191985");
		assertEquals(jvsg, true);
	}
	
	@Test
	public void test7() {
//		Planetside2API.getSingleton().getWeaponInfo();
		Planetside2API.getSingleton().outputCsvWeaponInfo();
		String name = Planetside2API.getSingleton().getWeaponName(705);
		assertEquals(name, "Spear Anti-Vehicle Phalanx Turret");
	}
}
