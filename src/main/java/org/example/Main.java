package org.example;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;

public class Main {
    static String serverUrl = "http://localhost:9200";
    static String indexName = "users";
    static String fieldSearch = "name";
    static String searchText = "Nguyen";

    public static void main(String[] args) throws IOException {
        RestClient httpClient = RestClient.builder(
                HttpHost.create(serverUrl)
        ).build();

        ElasticsearchTransport transport = new RestClientTransport(
                httpClient,
                new JacksonJsonpMapper()
        );

        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        Query query = MatchQuery.of(m -> m
                .field(fieldSearch)
                .query(searchText)
                .fuzziness("AUTO")
        )._toQuery();

        SearchResponse<Person> searchResponse = esClient.search(s -> s
                        .index(indexName)
                        .query(query).size(10)
                ,
                Person.class
        );

        List<Hit<Person>> hits = searchResponse.hits().hits();
        for (Hit<Person> hit : hits) {
            Person user = hit.source();
            assert user != null;
            System.out.println(user.getNAME());
        }

        httpClient.close();
    }
}