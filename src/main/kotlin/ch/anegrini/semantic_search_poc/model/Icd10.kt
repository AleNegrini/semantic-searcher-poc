package ch.anegrini.semantic_search_poc.model

data class Icd10 (
    val gerarchicalLevel:String,
    val type: String,
    val code: String,
    val chapter: String,
    val groupCode: String,
    val extCode: String,
    val code1: String,
    val code2: String,
    val description: String,
    val title1: String,
    val title2: String,
    val title3: String,
    val morbity: String,
    val genderLink: String,
    val errorGenderLink: String,
    val infAgeLimit: String,
    val supAgeLimit: String,
    val errorsAge: String,
    val rareDisease: Char,
    val other: Char,
)