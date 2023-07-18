package ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import org.jetbrains.annotations.Nullable
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EspecieDAO : JpaRepository<Especie, Long> {
    @Query("""
        SELECT COUNT(e.id)
        FROM Especie e
        JOIN e.vectores v
        WHERE e.id = :especieId
    """)
    fun cantidadDeInfectados(@Param("especieId") especieId: Long): Int

    @Query("""
        SELECT e
        FROM Especie e
        JOIN e.vectores v
        WHERE v.tipo = :tipoPersona
        GROUP BY e
        ORDER BY COUNT(DISTINCT v.id) DESC
    """)
    @Nullable
    fun especieLider(
        @Param("tipoPersona") tipoPersona: TipoDeVector = TipoDeVector.Persona,
        pageable: PageRequest = PageRequest.of(0,1)
    ): Especie?

    @Query("""
        SELECT e
        FROM Especie e
        JOIN e.vectores v
        WHERE v.tipo = :tipoPersona OR v.tipo = :tipoAnimal
        GROUP BY e
        ORDER BY COUNT(DISTINCT v.id) DESC
    """)
    fun especiesLideres(
        @Param("tipoPersona") tipoPersona: TipoDeVector = TipoDeVector.Persona,
        @Param("tipoAnimal") tipoAnimal: TipoDeVector = TipoDeVector.Animal,
        pageable: PageRequest = PageRequest.of(0,10)

    ): List<Especie>

    @Query("""
        SELECT e
        FROM Especie e
        JOIN e.vectores v
        WHERE v.ubicacion.nombre = :ubicacionNombre
        GROUP BY e
        ORDER BY COUNT(DISTINCT e.id) DESC
    """)
    @Nullable
    fun especieLiderEn(
        @Param("ubicacionNombre") nombreDeLaUbicacion: String,
        pageable: PageRequest = PageRequest.of(0,1)
    ): Especie?
}