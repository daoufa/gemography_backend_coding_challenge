package org.GI3.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepoController {

	@RequestMapping("/repos/{date}")
	public Map getItem(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-mm-dd") Date date) throws Exception {

		// get date from url and format it as yyyy-mm-dd
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
		if (date == null) {
			date = new Date();
		}

		// create connection and save the data as String
		URL url = new URL("https://api.github.com/search/repositories?q=created:%3E" + simpleDateFormat.format(date)
				+ "&sort=stars&order=desc");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		String str = "";
		Scanner scanner = new Scanner(url.openStream());

		while (scanner.hasNext()) {
			str += scanner.nextLine();
		}

		scanner.close();

		// parse data from String to JSONObject to get items object wich contains
		// language used in the repo
		JSONParser par = new JSONParser();
		JSONObject dataObj = (JSONObject) par.parse(str);

		JSONArray itemObj = (JSONArray) dataObj.get("items");

		// create map that map each language with the number of times used in the total
		// 100 repo
		Map<String, Integer> languageCounter = new HashMap();

		for (int i = 0; i < itemObj.size(); i++) {
			JSONObject repo = (JSONObject) itemObj.get(i);
			String language = "";
			if (repo.get("language") != null) {
				language = repo.get("language").toString();
			} else
				continue;

			if (languageCounter.containsKey(language)) {

				int counter = languageCounter.get(language) + 1;
				languageCounter.replace(language, counter);

			} else {
				languageCounter.put(language, 1);
			}
		}

		// get the most popular language
		int max = 0;
		String mostPopularLanguage = "";
		for (Map.Entry<String, Integer> m : languageCounter.entrySet()) {
			if (max < (int) m.getValue()) {
				max = (int) m.getValue();
				mostPopularLanguage = (String) m.getKey();
			}
		}

		return languageCounter;
	}

	@RequestMapping("/repos")
	void handleFoo(HttpServletResponse response) throws IOException {
//		DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		System.out.println(java.time.LocalDate.now());
		response.sendRedirect("/repos/" + java.time.LocalDate.now());
	}

}
