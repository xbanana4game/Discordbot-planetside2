package jp.banana.planetside2.command;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import jp.banana.planetside2.entity.Datatype;

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
	 * 言語設定
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
	
	public static List<Datatype> test() {
		String data = getAPIString(PS2URL+NAMESPACE.PS2.value());
		return parseDatatype(data);
	}
	
	public static void outputCsvDatatype() {
		// ファイル出力
		try {
			FileWriter fw = new FileWriter("datatype.csv.txt");
			fw.write(Datatype.csvHead()+"\n");
			List<Datatype> datatype_list = ApiCommandBuilder.test();
			for (Datatype d : datatype_list) {
				String l = Datatype.csvBody(d);
				fw.write(l+"\n");
			}
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static List<Datatype> parseDatatype(String data) {
		List<Datatype> datatype_list = new ArrayList<Datatype>();
		JSONObject json = new JSONObject(data);
		int returned = json.getInt("returned");
		if(returned==0) {
			return null;
		}
		for(int i=0;i<json.getJSONArray("datatype_list").length();i++) {
			try{
				String name = json.getJSONArray("datatype_list").getJSONObject(i).getString("name");
				boolean hidden = json.getJSONArray("datatype_list").getJSONObject(i).getBoolean("hidden");
				Object count_ = json.getJSONArray("datatype_list").getJSONObject(i).get("count");

				String count = null;
				if(count_ instanceof String) {
					count = json.getJSONArray("datatype_list").getJSONObject(i).getString("count");
				} else if(count_ instanceof Integer){
					count = String.valueOf(json.getJSONArray("datatype_list").getJSONObject(i).getInt("count"));
				}
				
				List<String> resolve_list = new ArrayList<String>();
				int resolve_list_len = json.getJSONArray("datatype_list").getJSONObject(i).getJSONArray("resolve_list").length();
				for(int j=0;j<resolve_list_len;j++) {
					resolve_list.add(json.getJSONArray("datatype_list").getJSONObject(i).getJSONArray("resolve_list").getString(j));
				}
				
				Datatype dt = new Datatype(name, hidden, count, resolve_list );
				datatype_list.add(dt);
//				System.out.println(i+":"+json.getJSONArray("datatype_list").getJSONObject(i).toString());
//				System.out.println(dt);
			}
			catch(Exception e) {
				System.err.println(i+":"+json.getJSONArray("datatype_list").getJSONObject(i).toString());
				System.err.println(e.getMessage());
			}

		}
		return datatype_list;
	}
	
	public static String getAPIString(String page_url) {
		URL url = null;
		String line = null;
		try {
			url = new URL(page_url);
			URLConnection conn;
			conn = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			
			if ((line = in.readLine()) == null) {
				return null;
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
}
