package com.doubleA.news;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("News")
@ToString
public class News {
    @Id
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("time_published")
    private String time;

    @SerializedName("authors")
    private List<String> authors;

    @SerializedName("summary")
    private String summary;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("source")
    private String source;

    @SerializedName("category_within_source")
    private String category;

    @SerializedName("source_domain")
    private String sourceDomain;

    @SerializedName("topics")
    private List<TopicSentiment> topics;

    @SerializedName("overall_sentiment_score")
    private Float score;

    @SerializedName("overall_sentiment_label")
    private String label;

    @SerializedName("ticker_sentiment")
    private List<TickerSentiment> tickers;

    @Setter
    @Getter
    private static class TopicSentiment {
        @SerializedName("topic")
        private String topic;

        @SerializedName("relevance_score")
        private String score;
    }

    @Setter
    @Getter
    private static class TickerSentiment {
        @SerializedName("ticker")
        private String ticker;

        @SerializedName("relevance_score")
        private String score;

        @SerializedName("ticker_sentiment_score")
        private String tickerScore;

        @SerializedName("ticker_sentiment_label")
        private String tickerLabel;
    }
}
