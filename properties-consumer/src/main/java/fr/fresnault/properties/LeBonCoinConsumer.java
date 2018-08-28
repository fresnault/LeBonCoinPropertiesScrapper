package fr.fresnault.properties;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.fresnault.properties.config.LeBonCoinConfig;
import fr.fresnault.properties.config.RabbitMQConfiguration;
import fr.fresnault.properties.domain.City;
import fr.fresnault.properties.domain.Property;
import fr.fresnault.properties.domain.PropertyPhoto;
import fr.fresnault.properties.domain.enumeration.Source;
import fr.fresnault.properties.domain.enumeration.Transaction;
import fr.fresnault.properties.domain.enumeration.Type;
import fr.fresnault.properties.repository.CityRepository;
import fr.fresnault.properties.repository.PropertyRepository;

@Component
public class LeBonCoinConsumer {

	private final Logger log = LoggerFactory.getLogger(LeBonCoinConsumer.class);

	private final LeBonCoinConfig leBonCoinConfig;

	private final CityRepository cityRepository;

	private final PropertyRepository propertyRepository;

	private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		}
	};

	public LeBonCoinConsumer(CityRepository cityRepository, PropertyRepository propertyRepository,
			LeBonCoinConfig leBonCoinConfig) {
		this.cityRepository = cityRepository;
		this.propertyRepository = propertyRepository;
		this.leBonCoinConfig = leBonCoinConfig;
	}

	@Scheduled(cron = "0 0 */6 * * *")
	public void status() {
		log.info("Receiver started");
	}

	@Async
	@RabbitListener(queues = RabbitMQConfiguration.QUEUE_NAME)
	public void receiveMessage(String refId) throws IOException, JSONException, ParseException, InterruptedException {
		log.info("Traitement refId '{}'", refId);

		Property property = propertyRepository.findByRefSourceAndRefId(Source.LEBONCOIN, refId);

		Boolean connected = false;
		int nbTry = 0;
		Connection connexion = null;

		while (!connected && nbTry++ < 5) {
			try {
				connexion = Jsoup.connect(property.getUrl()).headers(leBonCoinConfig.getHeaders());
				connected = true;
			} catch (Exception e) {
				log.error("Connect failed, retry " + nbTry + "...");
				Thread.sleep(1000);
			}
		}

		Document document = connexion.get();
		String json = document.getElementsByTag("script").stream().filter(e -> e.data().startsWith("window.FLUX_STATE"))
				.findFirst().get().data();

		JSONObject jsonObj = new JSONObject(json.substring(20));
		JSONObject adview = jsonObj.getJSONObject("adview");
		Map<String, String> attributes = getAttributes(adview.getJSONArray("attributes"));

		String name = getName(adview);
		Transaction transaction = getTransaction(adview);
		Type type = getType(attributes);
		String description = getDescription(adview);
		Instant createdDate = getCreatedDate(adview);
		BigDecimal price = getPrice(adview);
		Integer roomCount = getRoomCount(attributes);
		BigDecimal area = getArea(attributes);
		// Set<PropertyFeature> features = getFeatures();
		Set<PropertyPhoto> photos = getPhotos(adview);
		City city = getCity(adview);

		property.name(name).transaction(transaction).type(type).description(description).createdDate(createdDate)
				.price(price).roomCount(roomCount).photos(photos).city(city);

		if (type == Type.LAND) {
			property.surfaceArea(area);
		} else {
			property.livingArea(area);
		}

		propertyRepository.save(property);
	}

	private String getStringFromJSON(JSONObject json, String key) {
		return json.has(key) ? json.getString(key) : "";
	}

	private Map<String, String> getAttributes(JSONArray jsonArray) {
		Map<String, String> attributes = new HashMap<>();

		for (int index = 0; index < jsonArray.length(); index++) {
			JSONObject jsonObject = jsonArray.getJSONObject(index);
			attributes.put(jsonObject.getString("key"), jsonObject.getString("value"));
		}

		return attributes;
	}

	private String getName(JSONObject adview) {
		return getStringFromJSON(adview, "subject");
	}

	private Transaction getTransaction(JSONObject adview) {
		String typeTransaction = getStringFromJSON(adview, "category_name");
		if ("Locations".equals(typeTransaction)) {
			return Transaction.RENT;
		} else {
			return Transaction.SELL;
		}
	}

	private Type getType(Map<String, String> attributes) {
		switch (attributes.get("real_estate_type")) {
		case "1":
			return Type.HOUSE;
		case "2":
			return Type.FLAT;
		case "3":
			return Type.LAND;
		case "4":
			return Type.PARKING;
		case "5":
			return Type.OTHER;
		}
		return Type.OTHER;
	}

	public String getDescription(JSONObject adview) {
		return getStringFromJSON(adview, "body");
	}

	public Instant getCreatedDate(JSONObject adview) {
		String createdDate = getStringFromJSON(adview, "first_publication_date");
		try {
			return df.get().parse(createdDate).toInstant();
		} catch (Exception e) {
			return new Date().toInstant();
		}
	}

	private BigDecimal getPrice(JSONObject adview) {
		if (adview.has("price")) {
			JSONArray prices = adview.getJSONArray("price");
			if (prices.length() > 0) {
				return new BigDecimal(prices.getInt(0));
			}
		}
		return null;
	}

	private Integer getRoomCount(Map<String, String> attributes) {
		if (attributes.containsKey("rooms")) {
			String nbRooms = attributes.get("rooms");
			if (nbRooms != null && !nbRooms.isEmpty()) {
				return Integer.getInteger(nbRooms);
			}
		}
		return null;
	}

	public BigDecimal getArea(Map<String, String> attributes) throws JSONException {
		if (attributes.containsKey("square")) {
			String nbRooms = attributes.get("square");
			if (nbRooms != null && !nbRooms.isEmpty()) {
				return new BigDecimal(nbRooms);
			}
		}
		return null;
	}

	/*
	 * Type Type = getType(doc);
	 * 
	 * if (Type == Type.FLAT || Type == Type.HOUSE) { return getSurface(doc); }
	 * 
	 * return null; }
	 * 
	 * public BigDecimal getSurfaceArea(Document doc) { Type Type =
	 * getType(doc);
	 * 
	 * if (Type == Type.LAND) { return getSurface(doc); }
	 * 
	 * return null; }
	 */

	public Set<PropertyPhoto> getPhotos(JSONObject adview) throws JSONException {
		Set<PropertyPhoto> photos = new HashSet<>();

		if (adview.has("images")) {
			JSONObject jsonPhotos = adview.getJSONObject("images");

			for (int index = 0; index < jsonPhotos.getInt("nb_images"); index++) {
				photos.add(new PropertyPhoto().photoSeq(index)
						.photoThumbUrl(jsonPhotos.getJSONArray("urls_thumb").getString(index))
						.photoUrl(jsonPhotos.getJSONArray("urls_large").getString(index)));
			}
		}

		return photos;
	}

	public City getCity(JSONObject adview) throws JSONException {
		City city = null;

		if (adview.has("location")) {
			JSONObject location = adview.getJSONObject("location");
			city = cityRepository.findByNameAndZipCode(getStringFromJSON(location, "city"),
					getStringFromJSON(location, "zipcode"));
		}

		return city;
	}

}
