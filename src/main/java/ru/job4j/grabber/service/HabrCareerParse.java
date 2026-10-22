package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGE = 5;

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGE; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var dateElement = row.select(".vacancy-card__date time").first();
                    if (titleElement == null || dateElement == null) {
                        return;
                    }
                    var linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String link = String.format("%s%s", SOURCE_LINK,
                            linkElement.attr("href"));
                    String date = dateElement.attr("datetime");
                    var post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(link);
                    post.setDescription(retrieveDescription(link));
                    post.setTime(OffsetDateTime
                            .parse(date)
                            .toInstant()
                            .toEpochMilli());
                    result.add(post);
                });
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        String vacancyDescription = null;
        try {
            var connection = Jsoup.connect(link);
            var document = connection.get();
            var descriptionElement = document.select(".faded-content__container").first();
            if (descriptionElement != null) {
                vacancyDescription = descriptionElement.text();
            }
        } catch (IOException e) {
            LOG.error("When load vacancy description for link: " + link, e);
        }
        return vacancyDescription;
    }
}