package com.a7a7.modeule.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class TestController {
	@RequestMapping(value = "/xdm/test/TestXdmTest")
	public String publicCorona1JsonNodeList(Model model) throws Exception {
		
//		String apiUrl = "http://apis.data.go.kr/1471000/CovidDagnsRgntProdExprtStusService/getCovidDagnsRgntProdExprtStusInq?serviceKey=gSyQXQjGmxFL7i8GtzkF2v760PTeLTvN0RexWjyq9sn5gQL43K1q145MhKDSrh8V0ZiBaarDbiJwxC2fM%2BmvyQ%3D%3D&numOfRows=3&pageNo=1&type=json";
		String apiUrl = "http://apis.data.go.kr/1471000/CovidDagnsRgntProdExprtStusService/getMmCovidDagnsRgntExprtStusInq?serviceKey=gSyQXQjGmxFL7i8GtzkF2v760PTeLTvN0RexWjyq9sn5gQL43K1q145MhKDSrh8V0ZiBaarDbiJwxC2fM%2BmvyQ%3D%3D&numOfRows=3&pageNo=1&type=json";
		
		URL url = new URL(apiUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod("GET");
		
		BufferedReader bufferedReader;
		if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() <= 300) {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
		} else {
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println("line: " + line);
			stringBuilder.append(line);
		}

		bufferedReader.close();
		httpURLConnection.disconnect();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readTree(stringBuilder.toString());
		
//		System.out.println("node.get(\"header\").get(\"resultCode\").asText(): " + node.get("header").get("resultCode").asText());
//		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("header").get("resultMsg").asText());
//		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(0).get("KIT_PROD_QTY").asText());
		
		System.out.println("node.get(\"header\").get(\"resultCode\").asText(): " + node.get("header").get("resultCode").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("header").get("resultMsg").asText());
		System.out.println("node.get(\"header\").get(\"resultMsg\").asText(): " + node.get("body").get("items").get(0).get("KIT_EXPRT_QTY").asText());
		
		model.addAttribute("node", node);
		
//		model.addAttribute(node);
		
		return "xdm/test/TestXdmTest";
	}
}
