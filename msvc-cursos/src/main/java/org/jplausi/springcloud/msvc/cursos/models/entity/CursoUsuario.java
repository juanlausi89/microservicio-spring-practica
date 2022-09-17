package org.jplausi.springcloud.msvc.cursos.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;

@Entity
@Table(name="cursos_usuarios")
public class CursoUsuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="usuario_id",unique = true)
    private Long usuarioId;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Long UsuarioId) {
        this.usuarioId = UsuarioId;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof CursoUsuario)) {
            return false;
        }
        CursoUsuario cursoUsuario = (CursoUsuario) obj;
        return this.usuarioId != null && this.usuarioId.equals(cursoUsuario.usuarioId);
    }
    
}
