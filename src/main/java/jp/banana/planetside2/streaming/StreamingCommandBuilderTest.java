package jp.banana.planetside2.streaming;

import jp.banana.planetside2.streaming.StreamingCommandBuilder.EVENTNAME;

import org.junit.Test;

public class StreamingCommandBuilderTest {

	@Test
	public void test() {
		StreamingCommandBuilder test = new StreamingCommandBuilder();
		test.addWorlds(1);
		test.addWorlds(2);
		String command = test.build();
		System.out.println(command);
	}
	
	@Test 
	public void facilityControl() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder().addEventNames(EVENTNAME.FacilityControl);
		sc = sc.addWorlds(1);
		String command = sc.build();
		System.out.println(command);
	}
	
	@Test 
	public void help() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder().help();
		String command = sc.build();
		System.out.println(command);
	}
	
	@Test 
	public void clear() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder().clearCommand();
		String command = sc.build();
		System.out.println(command);
	}
	
	@Test
	public void vehicleDestroy() {
		StreamingCommandBuilder sc = new StreamingCommandBuilder();
		sc = sc.addEventNames(EVENTNAME.VehicleDestroy);
		sc = sc.addCharacters("all");
		sc = sc.addWorlds(1);
		String command = sc.build();
		System.out.println(command);
	}
}
