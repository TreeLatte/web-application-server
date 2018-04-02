package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;
import webserver.RequestHandler;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private Map<String, String> headers = new HashMap<String,String>();
	private Map<String, String> params = new HashMap<String,String>();
	private String path;
	private String method;
	
	public HttpRequest(InputStream in){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			 String line = br.readLine();
		        if (line == null) {
		            return;
		        }
		        
		        processRequestLine(line);
		        
		        line = br.readLine();
		        while(!line.equals("")) {
		        	log.debug("headers : {}",line);
		        	String[] tokens = line.split(":");
		        	//java trim (공백 제거용.)
		        	headers.put(tokens[0].trim(),tokens[1].trim());
		        	line = br.readLine(); 
		        }
		        if("POST".equals(method)) {
		        	String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
		        	params = HttpRequestUtils.parseQueryString(body);
		        }
		       
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void processRequestLine(String line) {
		// TODO Auto-generated method stub
		log.debug("request line : {}",line);
		String[] tokens=line.split(" ");
		method = tokens[0];
		
		if("POST".equals(method)) {
			path = tokens[1];
			return;
		}
		int index = tokens[1].indexOf("?");
		if(index == -1) {
			//get이고 요청에 파라미터가 없는(사실상 index라든지 그런거)
			path = tokens[1];
			return;
		}else {
			//(get이면서 파라미터 있는거.)
			path = tokens[1].substring(0, index);
			params = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1));
		}
	}


	public String getPath() {
		return path;
	}


	public String getMethod() {
		return method;
	}

	public String getParameter(String parameterName) {
		return params.get(parameterName);
	}
	public String getHeader(String fieldName) {
		return headers.get(fieldName);
	}
}
