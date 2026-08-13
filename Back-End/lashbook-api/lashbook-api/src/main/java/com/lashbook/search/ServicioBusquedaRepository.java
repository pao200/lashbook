package com.lashbook.search;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ServicioBusquedaRepository
    extends ElasticsearchRepository<ServicioBusquedaDocument, String> {

    @Query("""
        {
          "bool": {
            "should": [
              {
                "multi_match": {
                  "query": "?0",
                  "type": "bool_prefix",
                  "fields": [
                    "nombre^4",
                    "nombre._2gram",
                    "nombre._3gram",
                    "descripcion"
                  ]
                }
              },
              {
                "multi_match": {
                  "query": "?0",
                  "type": "best_fields",
                  "fields": [
                    "nombre^4",
                    "descripcion"
                  ],
                  "fuzziness": "AUTO",
                  "prefix_length": 1
                }
              }
            ],
            "minimum_should_match": 1
          }
        }
        """)
    List<ServicioBusquedaDocument> buscarPredictivo(
        String texto
    );
}