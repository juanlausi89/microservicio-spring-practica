package org.jplausi.springcloud.msvc.cursos.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jplausi.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.jplausi.springcloud.msvc.cursos.models.Usuario;
import org.jplausi.springcloud.msvc.cursos.models.entity.Curso;
import org.jplausi.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.jplausi.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoServiceImpl implements CursoService{

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) cursoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return cursoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
       Optional<Curso> optional = cursoRepository.findById(id);
       if(optional.isPresent()){
           Curso curso = optional.get();
           if(!curso.getCursoUsuarios().isEmpty()){
               List<Long> ids = curso.getCursoUsuarios().stream().map(cu -> cu.getUsuarioId()).collect(Collectors.toList());
               
               List <Usuario> usuarios = client.obtenerAlumnosPorCurso(ids);
               curso.setUsuarios(usuarios);
           }
            return Optional.of(curso);
       }
       return Optional.empty();
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        cursoRepository.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optional = cursoRepository.findById(cursoId);
        if (optional.isPresent()) {
            Usuario usuarioMscv = client.detalle(usuario.getId());

            Curso curso = optional.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMscv.getId());

            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMscv);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optional = cursoRepository.findById(cursoId);
        if (optional.isPresent()) {
            Usuario usuarioNuevoMscv = client.crear(usuario);

            Curso curso = optional.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioNuevoMscv.getId());

            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioNuevoMscv);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optional = cursoRepository.findById(cursoId);
        if (optional.isPresent()) {
            Usuario usuarioMscv = client.detalle(usuario.getId());

            Curso curso = optional.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMscv.getId());

            curso.removeCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMscv);
        }
        return Optional.empty();
    }

    

    
    
}
