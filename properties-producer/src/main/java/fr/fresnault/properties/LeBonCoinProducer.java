package fr.fresnault.properties;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.fresnault.properties.config.LeBonCoinConfig;
import fr.fresnault.properties.config.RabbitMQConfiguration;
import fr.fresnault.properties.domain.Property;
import fr.fresnault.properties.domain.enumeration.Source;
import fr.fresnault.properties.repository.PropertyRepository;

@Component
public class LeBonCoinProducer {

	private final Logger log = LoggerFactory.getLogger(LeBonCoinProducer.class);

	private final LeBonCoinConfig leBonCoinConfig;

	private final RabbitTemplate rabbitTemplate;

	private final PropertyRepository propertyRepository;

	public LeBonCoinProducer(RabbitTemplate rabbitTemplate, PropertyRepository propertyRepository,
			LeBonCoinConfig leBonCoinConfig) {
		this.rabbitTemplate = rabbitTemplate;
		this.propertyRepository = propertyRepository;
		this.leBonCoinConfig = leBonCoinConfig;
	}

	@Scheduled(fixedDelay = 600000) // 10 minutes
	public void run() {
		try {
			for (String localisationPages : leBonCoinConfig.getLocalisationPages()) {
				Deque<Boolean> deque = new ArrayDeque<>();
				String firstPageLink = replacePageInUrl(localisationPages, 1);

				Document docFirstPage = Jsoup.connect(firstPageLink).headers(leBonCoinConfig.getHeaders())
						.followRedirects(true).get();

				Integer nbPages = leBonCoinConfig.getNbPages(docFirstPage);
				log.info("{} pages dans {}", nbPages, firstPageLink);

				IntStream.range(1, nbPages).allMatch(pageIndex -> {
					if (deque.size() > 300) {
						if (!deque.isEmpty() && deque.stream().allMatch(e -> Boolean.TRUE == e)) {
							log.info("Les biens immobiliers ont déjà été traités");
							return false;
						}
						deque.clear();
					}

					try {
						String currentPageLink = replacePageInUrl(localisationPages, pageIndex);

						Document document = Jsoup.connect(currentPageLink).headers(leBonCoinConfig.getHeaders())
								.followRedirects(true).get();

						// Récupération des liens des biens immobiliers présents
						// dans le document
						Set<String> propertiesLink = leBonCoinConfig.getPropertiesLink(document);
						log.info("{} liens dans {}", propertiesLink.size(), currentPageLink);

						// On envoie l'ensemble des liens dans RabbitMQ
						for (String link : propertiesLink) {
							int beginIndex = link.lastIndexOf('/');
							int endIndex = link.lastIndexOf('.');
							String refId = link.substring(beginIndex + 1, endIndex);
							if (propertyRepository.existsByRefSourceAndRefId(Source.LEBONCOIN, refId)) {
								log.info("La référence {} existe déjà dans la base", refId);
								deque.push(true);
							} else {
								log.info("La référence {} n'existe pas dans la base", refId);
								deque.push(false);
								propertyRepository.save(new Property(Source.LEBONCOIN, refId, link));
								rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME,
										RabbitMQConfiguration.QUEUE_NAME, refId);
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				});
			}
		} catch (Exception e) {
			log.error("Une exception est survenue lors du traitement leboncoin", e);
		}

	}

	private String replacePageInUrl(String localisationPages, int currentPage) {
		return localisationPages.replace("[PAGE]", String.valueOf(currentPage));
	}

}