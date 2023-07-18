package ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO

import ar.edu.unq.eperdemic.modelo.Patogeno
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PatogenoDAO: JpaRepository<Patogeno, Long>