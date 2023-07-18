package ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO

import ar.edu.unq.eperdemic.modelo.ubicacion.Ubicacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UbicacionJPADAO: JpaRepository<Ubicacion, Long> {
    @Query("SELECT COUNT(u.id) From Ubicacion u")
    fun cantidadDeUbicaciones(): Int

    @Query("""
        SELECT COUNT(DISTINCT u.id)
        FROM Ubicacion u JOIN u.vectores v JOIN v.especies e
        WHERE e.id = :especieId 
    """)
    fun cantidadDeUbicacionesConEspecie(@Param("especieId") especieId: Long): Int

    @Query("""
        SELECT COUNT(v) 
        FROM Ubicacion u 
        JOIN u.vectores v
        WHERE v.ubicacion.nombre = :ubicacionNombre
    """)
    fun cantidadDeVectoresEn(@Param("ubicacionNombre") nombreDeLaUbicacion: String): Int

    @Query("""
        SELECT COUNT(v) 
        FROM Ubicacion u 
        JOIN u.vectores v
        WHERE v.ubicacion.nombre = :ubicacionNombre AND v.especies.size >= 1
    """)
    fun cantidadDeInfectados(@Param("ubicacionNombre") nombreDeLaUbicacion: String): Int

    @Query("SELECT COUNT(u) > 0 FROM Ubicacion u WHERE u.nombre = :nombreUbicacion")
    fun existsByNombreUbicacion(nombreUbicacion: String): Boolean

    @Query("SELECT u FROM Ubicacion u WHERE u.nombre = :nombresUbicacion")
    fun findByNombre(@Param("nombresUbicacion")nombresUbicacion: String): Ubicacion

    fun findAllByNombreIn(nombresDeUbicaciones: List<String>): List<Ubicacion>

    @Query("""
        SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
        FROM Ubicacion u
        JOIN u.vectores v
        WHERE u.nombre = :ubicacionNombre AND v.especies.size >= 1
    """)
    fun hayInfectados(@Param("ubicacionNombre") nombreDeLaUbicacion: String): Boolean

    @Query("""
        SELECT u.nombre
        FROM Ubicacion u
        JOIN u.vectores v
        WHERE v.especies.size >= 1
    """)
    fun nombresDeUbicacionesInfectadas(): List<String>
}
