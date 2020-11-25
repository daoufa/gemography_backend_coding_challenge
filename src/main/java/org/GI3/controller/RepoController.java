package org.GI3.controller;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
		if (date == null) {
			date = new Date();
		}
		URL url = new URL("https://api.github.com/search/repositories?q=created:%3E" + simpleDateFormat.format(date)
				+ "&sort=stars&order=desc");
		System.out.println();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		String str = "";
		Scanner scanner = new Scanner(url.openStream());

		while (scanner.hasNext()) {
			str += scanner.nextLine();
		}

		scanner.close();

		JSONParser par = new JSONParser();
		JSONObject dataObj = (JSONObject) par.parse(str);

		JSONArray itemObj = (JSONArray) dataObj.get("items");

		Map<String, Integer> languageCounter = new HashMap();
		System.out.println(itemObj.size());
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
}
