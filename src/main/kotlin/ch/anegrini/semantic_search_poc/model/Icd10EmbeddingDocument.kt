package ch.anegrini.semantic_search_poc.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "icd10")
data class Icd10EmbeddingDocument(
    @Id
    val id: String,

    @Field(name = "review_text")
    val reviewText: String,
    @Field(name = "review_vector")
    val reviewVector: List<Float>
)
