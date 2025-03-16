package ch.anegrini.semantic_search_poc.repository

import ch.anegrini.semantic_search_poc.model.Icd10EmbeddingDocument
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository


@Repository
interface Icd10Repository : ElasticsearchRepository<Icd10EmbeddingDocument, String> {

    @Query(
        """
{
    "knn": {
    "field": "review_vector",
    "query_vector": [ 0.039067574, -0.014321604, -0.14575335, 0.018615553, 0.017406486, -0.033041764, -0.015447659, -0.032389347, -0.022840725, 0.062614955 ],
    "k": 5,
    "num_candidates": 10
  }
}
"""
    )
    fun findByVector(queryVector: FloatArray): SearchHits<Icd10EmbeddingDocument>
}