package jp.banana.planetside2.command;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import jp.banana.discordbot.BotConfig;
import jp.banana.planetside2.command.ApiCommandBuilder.NAMESPACE;
import jp.banana.planetside2.entity.Datatype;
import jp.banana.planetside2.entity.Weapon;

import org.junit.Test;

public class ApiCommandBuilderTest {

	@Test
	public void test() {
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V1);
		command.test();
	}
	
	@Test
	public void test2() {
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V1,"vehicle");
		assertEquals("http://census.daybreakgames.com/get/ps2:v1/vehicle", command.build());
	}
	@Test
	public void test3() {
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2,"vehicle");
		command.setLimit(20);
		assertEquals("http://census.daybreakgames.com/get/ps2/vehicle?c:limit=20", command.build());
	}
	
	@Test
	public void test4() {
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2,"vehicle");
		command.setLimit(20).setLang("en");
		assertEquals("http://census.daybreakgames.com/get/ps2/vehicle?c:limit=20&c:lang=en", command.build());
	}
	
	@Test
	public void test5() {
		int id = 5100;
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "map_region", "facility_id="+id);
		assertEquals("http://census.daybreakgames.com/get/ps2:v2/map_region?facility_id=5100", command.build());
	}
	
	@Test
	public void test6() {
		String id = "5428366106639191985";
		ApiCommandBuilder command = new ApiCommandBuilder(ApiCommandBuilder.NAMESPACE.PS2V2, "character", "character_id="+id);
		assertEquals("http://census.daybreakgames.com/get/ps2:v2/character?character_id=5428366106639191985", command.build());
	}
	
	@Test
	public void outputCsv() {
		ApiCommandBuilder.outputCsvDatatype();
	}
	
	@Test
	public void test7(){
		String outfitID = BotConfig.getSingleton().getOutfitID();
		//JVSG
		ApiCommandBuilder command = new ApiCommandBuilder(NAMESPACE.PS2V2, "outfit", "outfit_id="+outfitID+"&c:resolve=member");
		assertEquals("http://census.daybreakgames.com/get/ps2:v2/outfit?outfit_id=37512998641471064&c:resolve=member",command.build());
	}
}
