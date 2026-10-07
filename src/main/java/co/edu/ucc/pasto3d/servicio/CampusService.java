package co.edu.ucc.pasto3d.servicio;

import co.edu.ucc.pasto3d.dto.Dtos.*;
import co.edu.ucc.pasto3d.ia.ContextoCampus;
import co.edu.ucc.pasto3d.modelo.*;
import co.edu.ucc.pasto3d.repositorio.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Capa de servicio: convierte entidades en DTOs y reúne la información del campus. */
@Service
@Transactional(readOnly = true)
public class CampusService {

    private final EdificioRepositorio edificios;
    private final LugarRepositorio lugares;
    private final ProgramaRepositorio programas;
    private final PreguntaFrecuenteRepositorio preguntas;

    public CampusService(EdificioRepositorio edificios, LugarRepositorio lugares,
                         ProgramaRepositorio programas, PreguntaFrecuenteRepositorio preguntas) {
        this.edificios = edificios;
        this.lugares = lugares;
        this.programas = programas;
        this.preguntas = preguntas;
    }

    public List<EdificioDTO> edificios() {
        return edificios.findAll(Sort.by("id")).stream().map(CampusService::aDto).toList();
    }

    public List<LugarDTO> lugares() {
        return lugares.findAll(Sort.by("id")).stream().map(CampusService::aDto).toList();
    }

    public List<LugarDTO> lugaresDeEdificio(Integer edificioId) {
        return lugares.findByEdificioIdOrderByPisoAsc(edificioId).stream().map(CampusService::aDto).toList();
    }

    public List<ProgramaDTO> programas() {
        return programas.findAll(Sort.by("nombre")).stream().map(CampusService::aDto).toList();
    }

    public List<PreguntaFrecuenteDTO> preguntasFrecuentes() {
        return preguntas.findAll(Sort.by("id")).stream()
                .map(f -> new PreguntaFrecuenteDTO(f.getId(), f.getCategoria(), f.getPregunta(), f.getRespuesta(),
                        f.getPalabrasClave()))
                .toList();
    }

    public ContextoCampus contexto() {
        return new ContextoCampus(edificios(), lugares(), programas(), preguntasFrecuentes());
    }

    static EdificioDTO aDto(Edificio e) {
        return new EdificioDTO(e.getId(), e.getCodigo(), e.getNombre(), e.getDescripcion(),
                e.getPosX(), e.getPosZ(), e.getAncho(), e.getProfundidad(), e.getPisos(), e.getColor(), e.getTipo());
    }

    static LugarDTO aDto(Lugar l) {
        return new LugarDTO(l.getId(), l.getNombre(), l.getTipo(), l.getEdificio().getId(), l.getEdificio().getNombre(),
                l.getPiso(), l.getDescripcion(), l.getHorario(), l.getTelefono(), l.getCorreo());
    }

    static ProgramaDTO aDto(Programa p) {
        Edificio e = p.getEdificio();
        return new ProgramaDTO(p.getId(), p.getNombre(), p.getFacultad(), p.getNivel(), p.getModalidad(),
                p.getDuracionSemestres(), p.getTitulo(), p.getDescripcion(),
                e == null ? null : e.getId(), e == null ? null : e.getNombre());
    }
}
