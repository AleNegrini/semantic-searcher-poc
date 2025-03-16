package ch.anegrini.semantic_search_poc.controller

import ch.anegrini.semantic_search_poc.service.Icd10Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/icd10")
class Icd10Controller(
    private val icd10Service: Icd10Service
) {

    @PostMapping("/ingest")
    fun loadEmbeddings(from: Int, to: Int): ResponseEntity<String> {
        try {
            icd10Service.computeEmbeddings(from, to)
            println("Embeddings correctly computed")
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            println("An error occurred when computing embeddings " + e.message)
            return ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/search/{searchText}")
    fun search(@PathVariable searchText: String): ResponseEntity<String> {
        val res = icd10Service.search(searchText)
        return ResponseEntity.ok().build()
    }

}