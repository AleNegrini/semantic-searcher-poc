package ch.anegrini.semantic_search_poc.repository

import ch.anegrini.semantic_search_poc.model.Icd10EmbeddingDocument
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository


@Repository
interface Icd10Repository : ElasticsearchRepository<Icd10EmbeddingDocument, String> {

    @Query(
        """
  "size": 10,
  "query": {
    "script_score": {
      "query": {
        "match_all": {}
      },
      "script": {
        "source": "cosineSimilarity(params.query_vector, \"review_vector\") + 1.0",
        "params": {
          "query_vector": ?0
        }
      }
    }
  }
    """
    )
    fun findTop10ByEmbedding(embedding: FloatArray?): List<Icd10EmbeddingDocument?>?
}