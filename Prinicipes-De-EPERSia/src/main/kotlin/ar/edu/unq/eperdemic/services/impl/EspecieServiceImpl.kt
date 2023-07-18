package ar.edu.unq.eperdemic.services.impl
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.services.EspecieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EspecieServiceImpl : EspecieService, EntentyUtils() {
    @Autowired private lateinit var especieDao : EspecieDAO

    override fun recuperarEspecie(id: Long): Especie {
        return this.findByIdOrThrow(especieDao,id)
    }

    override fun recuperarTodos(): List<Especie> {
        return especieDao.findAll().toMutableList()
    }

    override fun cantidadInfectados(especieId: Long): Int {
        return especieDao.cantidadDeInfectados(especieId)
    }

    override fun clear() {
        especieDao.deleteAll()
    }
}