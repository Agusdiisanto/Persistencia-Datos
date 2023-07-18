package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.VectorDAO
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.persistencia.dao.FirestoreDAO.UbicacionFirestoreDAOImpl
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VectorServiceImpl : VectorService, EntentyUtils() {
    @Autowired private lateinit var especieDao : EspecieDAO
    @Autowired private lateinit var vectorDAO: VectorDAO
    @Autowired private lateinit var ubicacionDAO: UbicacionJPADAO
    @Autowired private lateinit var ubicacionFireRepository: UbicacionFirestoreDAOImpl


    override fun infectar(vectorId: Long, especieId: Long) {
        val vectorAInfectar = this.findByIdOrThrow(vectorDAO, vectorId)
        val especie = this.findByIdOrThrow(especieDao, especieId)
        especie.infectaA(vectorAInfectar)
        vectorDAO.save(vectorAInfectar)
    }

    override fun enfermedades(vectorId: Long): MutableSet<Especie> {
        val vector = this.findByIdOrThrow(vectorDAO,vectorId)
        return vector.especies.toMutableSet()
    }

    override fun contagiar(vectorInfectado: Vector, vectores: MutableSet<Vector>) {
        vectorInfectado.contagiarATodos(vectores)
        vectorDAO.save(vectorInfectado)
        vectorDAO.saveAll(vectores)
    }

    override fun crearVector(tipo: TipoDeVector, ubicacionId: Long): Vector {
        val ubicacion = this.findByIdOrThrow(ubicacionDAO,ubicacionId)
        val vector = Vector(tipo,ubicacion)
        ubicacionFireRepository.incrementarVectoresEn(ubicacion.nombre)
        return vectorDAO.save(vector)
    }

    override fun recuperarVector(vectorId: Long): Vector {
        return this.findByIdOrThrow(vectorDAO,vectorId)
    }

    override fun recuperarTodos(): MutableSet<Vector> {
        return vectorDAO.findAll().toMutableSet()
    }

    override fun borrarVector(vectorId: Long) {
        vectorDAO.findById(vectorId)
            .orElseThrow { NotFoundException("No existe un vector con el id $vectorId") }
        vectorDAO.deleteById(vectorId)
    }

    override fun clear() {
        vectorDAO.deleteAll()
        especieDao.deleteAll()
        ubicacionDAO.deleteAll()
    }
}