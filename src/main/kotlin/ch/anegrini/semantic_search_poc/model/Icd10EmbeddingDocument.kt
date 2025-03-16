package ch.anegrini.semantic_search_poc.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "icd10")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Icd10EmbeddingDocument @JsonCreator constructor (
    @Id
    @JsonProperty("id")
    val id: String,

    @Field(name = "review_text")
    @JsonProperty("review_text")
    val reviewText: String,
    @Field(name = "review_vector")
    @JsonProperty("review_vector")
    val reviewVector: List<Float>
)
