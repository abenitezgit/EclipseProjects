package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class mapping {

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		Process proc = new Process();
		
		String vProcID = "001";
		
		proc.setProcID(vProcID);
		
		//Map<String, etl> myetl = new HashMap<String, etl>();
		
		//Map<String, etlMatch> mymatch = new HashMap<String, etlMatch>();
		
		etl myetl = new etl();
		etlMatch mymatch;
		
		List<etlMatch> lstEtlMatch = new ArrayList<>();
		
		
		mymatch = new etlMatch();
		mymatch.setFieldS1("ID");
		mymatch.setFieldS2("ID");
		
		lstEtlMatch.add(mymatch);
		
		mymatch = new etlMatch();
		mymatch.setFieldS1("ID2");
		mymatch.setFieldS2("ID2");
		
		lstEtlMatch.add(mymatch);
		
		myetl.setId("ID001");
		myetl.setLstEtlMatch(lstEtlMatch);
		
		proc.setParam(myetl);
		
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        
        System.out.println(mapper.writeValueAsString(proc));

		
		
		
		

	}

}
