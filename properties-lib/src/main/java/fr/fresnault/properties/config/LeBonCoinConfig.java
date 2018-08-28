package fr.fresnault.properties.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class LeBonCoinConfig {

	private Map<String, String> headers = new HashMap<>();
	private List<String> localisationPages = new ArrayList<>();

	public List<String> getLocalisationPages() {
		if (localisationPages.isEmpty()) {
			localisationPages.add("https://www.leboncoin.fr/recherche/?category=9&real_estate_type=1,2,3&page=[PAGE]");
			localisationPages.add("https://www.leboncoin.fr/recherche/?category=10&real_estate_type=1,2,3&page=[PAGE]");
		}
		return localisationPages;
	}

	public Map<String, String> getHeaders() {
		if (headers.isEmpty()) {
			headers.put("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			headers.put("Accept-Encoding", "gzip, deflate, br");
			headers.put("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
			headers.put("Connection", "keep-alive");
			headers.put("Host", "www.leboncoin.fr");
			headers.put("Upgrade-Insecure-Requests", "1");
			headers.put("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

		}
		return headers;
	}

	public Integer getNbPages(Document document) {
		return (Integer.valueOf(document.select("meta[name=description]").attr("content").replaceAll("[^0-9]", ""))
				/ 35) + 1;
	}

	public Set<String> getPropertiesLink(Document document) {
		return new HashSet<>(document.select("li[data-qa-id=aditem_container] > a").eachAttr("href")).stream()
				.map(url -> "http://www.leboncoin.fr" + url.substring(0, url.length() - 1)).collect(Collectors.toSet());
	}

}
