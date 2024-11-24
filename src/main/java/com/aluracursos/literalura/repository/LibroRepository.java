package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    Libro findByTitulo(String titulo);

    Libro findByIdioma(String idioma);

    @Query("SELECT l FROM Libro l WHERE l.idioma = :idioma")
    List<Libro> buscaLibroPorIdioma(String idioma);
}
