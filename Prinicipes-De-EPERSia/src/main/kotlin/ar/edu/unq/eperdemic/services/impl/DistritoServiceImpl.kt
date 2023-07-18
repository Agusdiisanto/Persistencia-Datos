package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.persistencia.dao.RelacionalDAO.UbicacionJPADAO
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.MongoDbDAO.UbicacionMongoDAO
import ar.edu.unq.eperdemic.modelo.ubicacion.Distrito
import ar.edu.unq.eperdemic.modelo.utils.EntentyUtils
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.exceptions.DistritoSuperpuesto
import ar.edu.unq.eperdemic.services.exceptions.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DistritoServiceImpl : DistritoService, EntentyUtils() {
    @Autowired private lateinit var dao: DistritoDAO
    @Autowired private lateinit var ubicacionMongoDAO: UbicacionMongoDAO
    @Autowired private lateinit var ubicacionJPADAO: UbicacionJPADAO

    override fun crear(distrito: Distrito): Distrito {
        if(dao.existeDistritoEnArea(distrito.area)){
            throw DistritoSuperpuesto("El area del distrito ingresado se superpone a la de uno existente")
        }
        return dao.save(distrito)
    }

    override fun recuperar(nombre: String): Distrito {
        return dao.findByNombre(nombre)
    }

    override fun recuperarTodos(): List<Distrito> {
        return dao.findAll()
    }

    override fun distritoMasEnfermo(): Distrito {
        val ubicacionesInfectadas = ubicacionJPADAO.nombresDeUbicacionesInfectadas()
        val distritoID = ubicacionMongoDAO.distritoMasEnfermo(ubicacionesInfectadas)?:throw NotFoundException("No se hallaron distritos con ubicaciones infectadas.")

        return dao.findByIdOrNull(distritoID)!!
    }
}