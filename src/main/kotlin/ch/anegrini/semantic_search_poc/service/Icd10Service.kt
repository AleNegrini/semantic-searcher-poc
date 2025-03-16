package ch.anegrini.semantic_search_poc.service

import ch.anegrini.semantic_search_poc.model.Icd10
import ch.anegrini.semantic_search_poc.model.Icd10EmbeddingDocument
import ch.anegrini.semantic_search_poc.repository.Icd10Repository
import co.elastic.clients.elasticsearch._types.KnnSearch
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import org.springframework.ai.ollama.OllamaEmbeddingModel
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Service
class Icd10Service(
    private val ollamaEmbeddingModel: OllamaEmbeddingModel,
    private val icd10Repository: Icd10Repository,
    private val elasticsearchOperations: ElasticsearchOperations
) {

    @OptIn(ExperimentalUuidApi::class)
    fun computeEmbeddings(from: Int, to: Int) {
        val file =
            File("/Users/anegrini/dev/personal/semantic-searcher-poc/src/main/resources/icd10/ICD10GM2024_CSV_S_IT_versionemetadati_codici_20241031.csv")
        val listOfDiseases = file.inputStream().use { readCsv(it) }

        val docToElastic = mutableListOf<Icd10EmbeddingDocument>()
//        icd10Repository.deleteAll()

        listOfDiseases.slice(from..to).map { disease ->
            val embeddings = ollamaEmbeddingModel.embed(disease.description)
            docToElastic.add(
                Icd10EmbeddingDocument(
                    id = Uuid.random().toString(),
                    reviewText = disease.description, reviewVector = embeddings.toList()
                )
            )
        }
        icd10Repository.saveAll(docToElastic)
    }

    private fun readCsv(inputStream: InputStream): List<Icd10> {
        val reader = inputStream.bufferedReader()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val fields = it.split(';', ignoreCase = false, limit = 20)
                val gerarchicalLevel = fields[0]
                val type = fields[1]
                val code = fields[2]
                val chapter = fields[3]
                val groupCode = fields[4]
                val extCode = fields[5]
                val code1 = fields[6]
                val code2 = fields[7]
                val description = fields[8]
                val title1 = fields[9]
                val title2 = fields[10]
                val title3 = fields[11]
                val morbity = fields[12]
                val genderLink = fields[13]
                val errorGenderLink = fields[14]
                val infAgeLimit = fields[15]
                val supAgeLimit = fields[16]
                val errorsAge = fields[17]
                val rareDisease = fields[18][0] // Convert to Char
                val other = fields[19][0] // Convert to Char
                Icd10(
                    gerarchicalLevel,
                    type,
                    code,
                    chapter,
                    groupCode,
                    extCode,
                    code1,
                    code2,
                    description,
                    title1,
                    title2,
                    title3,
                    morbity,
                    genderLink,
                    errorGenderLink,
                    infAgeLimit,
                    supAgeLimit,
                    errorsAge,
                    rareDisease,
                    other
                )
            }.toList()
    }

    fun findNearestNeighbors(vector: FloatArray, k: Int): SearchHits<Icd10EmbeddingDocument> {

        val knnSearch = KnnSearch.Builder()
            .field("vector")
            .queryVector(vector.toList())
            .k(k)
            .numCandidates(100) // Adjust as needed
            .build()


        val query = NativeQuery.builder()
            .withKnnSearches(knnSearch)
            .build()

        return elasticsearchOperations.search(query, Icd10EmbeddingDocument::class.java)
    }

    fun search(searchText: String): List<Icd10EmbeddingDocument?>? {
        val searchTextEmbedding = ollamaEmbeddingModel.embed(searchText)
        val searchTextEmbeddingString = searchTextEmbedding.map { text ->
            text.toString()
        }
        println(
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
          "query_vector": $searchTextEmbeddingString
        }
      }
    }
  }
    """
        )
//        val result = findNearestNeighbors(searchTextEmbedding, 10)
        val result = icd10Repository.findTop10ByEmbedding(searchTextEmbedding);
        return result
    }
}