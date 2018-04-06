package jp.banana.planetside2.command;

import java.util.ArrayList;
import java.util.List;


/**
 * http://census.daybreakgames.com/
 *
 */
public class ApiCommandBuilder {
	private static final String PS2URL="http://census.daybreakgames.com/get/";
	private String serviceID;
	private NAMESPACE namespace = null;
	private String collection;
	
	private StringBuilder queryCommand = new StringBuilder();
	private List<String> queryList = new ArrayList<String>();
	
	public enum NAMESPACE {PQ2("sq2/"),PS2("ps2/"),PS2V1("ps2:v1/"),PS2V2("ps2:v2/");
		private final String namespace;
		private NAMESPACE(String ns) {
			this.namespace = ns;
		}
		private String value() {
			return namespace;
		}
	}
	public ApiCommandBuilder(NAMESPACE namespace) {
		this.namespace = namespace;
		this.collection = "";
	}
	public ApiCommandBuilder(NAMESPACE namespace, String collection) {
		this.namespace = namespace;
		this.collection = collection;
	}
	public ApiCommandBuilder(NAMESPACE namespace, String collection, String query) {
		this.namespace = namespace;
		this.collection = collection;
		queryCommand.append(query);
	}
	
	public ApiCommandBuilder setLimit(int limit) {
		queryList.add("c:limit="+limit);
		return this;
	}
	
	/**
	 * åæåÍê›íË
	 * @param lang "en","es","de","fr","it","tr"
	 */
	public ApiCommandBuilder setLang(String lang) {
		queryList.add("c:lang="+lang);
		return this;
	}
	
	public ApiCommandBuilder setShow(String show) {
		queryList.add("c:show="+show);
		return this;
	}
	
	public ApiCommandBuilder setStart(String start) {
		queryList.add("c:start="+start);
		return this;
	}

	public String build() {
		StringBuilder command = new StringBuilder();
		command.append(PS2URL);
		command.append(namespace.value());
		if(serviceID!=null) {
			
		}
		command.append(collection);
		
		for(int i=0;i<queryList.size();i++) {
			String query = queryList.get(i);
			if(i!=0) {
				queryCommand.append("&");
			}
			queryCommand.append(query);
		}

		if(queryCommand.length()!=0){
			command.append("?");
			command.append(queryCommand);
		}
		return command.toString();
	}
}
