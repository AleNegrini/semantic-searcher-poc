package ch.anegrini.semantic_search_poc.controller

import ch.anegrini.semantic_search_poc.model.Icd10EmbeddingDocument
import ch.anegrini.semantic_search_poc.service.Icd10Service
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.elasticsearch.core.search.Hit
import co.elastic.clients.elasticsearch.core.search.HitsMetadata
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/icd10")
class Icd10Controller(
    private val icd10Service: Icd10Service
) {

    @PostMapping("/ingest")
    fun loadEmbeddings(from: Int, to: Int, dimensions: Int = 768): ResponseEntity<String> {
        try {
            icd10Service.computeEmbeddings(from, to, dimensions)
            println("Embeddings correctly computed")
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            println("An error occurred when computing embeddings " + e.message)
            return ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/search/{searchText}")
    fun search(@PathVariable searchText: String): ResponseEntity<HitsMetadata<Icd10EmbeddingDocument>> {
        val res = icd10Service.search(searchText).hits()
        return ResponseEntity.ok(res)
    }

}