package ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO

import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VectorDAO : JpaRepository<Vector, Long> {
    @Modifying
    @Query("DELETE FROM Vector v WHERE v.id = :unId")
    override fun deleteById(@Param("unId") vectorId: Long)
}