package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.MutacionDAO

import ar.edu.unq.eperdemic.modelo.mutacion.Mutacion
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.services.MutacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MutacionServiceImpl: MutacionService, EntentyUtils()  {
    @Autowired private lateinit var mutacionDao : MutacionDAO
    @Autowired private lateinit var especieDao : EspecieDAO

    override fun agregarMutacion(especieId: Long, mutacion: Mutacion): Mutacion {
        val especie = this.findByIdOrThrow(especieDao, especieId)
        especie.agregarMutacion(mutacion)
        return mutacionDao.save(mutacion)
    }

    override fun recuperarMutacion(id: Long): Mutacion {
        return this.findByIdOrThrow(mutacionDao, id)
    }

    override fun recuperarTodos(): List<Mutacion> {
        return mutacionDao.findAll().toMutableList()
    }

    override fun clear() {
        mutacionDao.deleteAll()
    }
}