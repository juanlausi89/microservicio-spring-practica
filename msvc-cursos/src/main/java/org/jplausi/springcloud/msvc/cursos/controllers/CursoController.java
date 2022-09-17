package org.jplausi.springcloud.msvc.cursos.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.jplausi.springcloud.msvc.cursos.models.Usuario;
import org.jplausi.springcloud.msvc.cursos.models.entity.Curso;
import org.jplausi.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;

@RestController
public class CursoController {
    
    @Autowired
    CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> listar(){
        return ResponseEntity.ok(cursoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Curso> optionalCurso = cursoService.porIdConUsuarios(id);

        if(optionalCurso.isPresent()){
            return ResponseEntity.ok(optionalCurso.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result){
        if(result.hasErrors()){
            return validar(result);
        }
        Curso nuevCurso = cursoService.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevCurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validar(result);
        }
        Optional<Curso> optionalCurso = cursoService.porId(id);
        if(optionalCurso.isPresent()){
            Curso cursoDb = optionalCurso.get();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Curso> optionalCurso = cursoService.porId(id);
        if(optionalCurso.isPresent()){
            cursoService.eliminar(optionalCurso.get().getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> optional;
        try {
            optional = cursoService.asignarUsuario(usuario, cursoId); 
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap("mensaje", "No existe el usuario por id o error en la comunicación: "+
                e.getMessage()));
                
        }
        if(optional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(optional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> optional;
        try {
            optional = cursoService.crearUsuario(usuario, cursoId); 
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap("mensaje", "No se pudo crear el usuario o error en la comunicación: "+
                e.getMessage()));
                
        }
        if(optional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(optional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> optional;
        try {
            optional = cursoService.eliminarUsuario(usuario, cursoId); 
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Collections.singletonMap("mensaje", "No existe el usuario por id o error en la comunicación: "+
                e.getMessage()));
                
        }
        if(optional.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(optional.get());
        }
        return ResponseEntity.notFound().build();
    }

    //Si elimino usuario en el microservicio usuario, se debe eliminar el usuario del curso
    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){
        cursoService.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String,String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(error->{
            errores.put(error.getField(),"El campo "+error.getField()+" "+error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
