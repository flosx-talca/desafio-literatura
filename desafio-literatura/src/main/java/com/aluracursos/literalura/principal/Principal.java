package com.aluracursos.literalura.principal;
import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoApi;
import com.aluracursos.literalura.service.ConvierteDatos;

import javax.swing.text.Element;
import java.util.*;
import java.util.jar.JarOutputStream;

public class Principal {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private DatosLibro datosLibro;
    private Libro libro;
    private DatosAutor datosAutor;
    private Autor autor;
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private final ConvierteDatos conversor = new ConvierteDatos();
    private Resultado dataResultado;


    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {

            this.libroRepository =libroRepository;
            this.autorRepository = autorRepository;
    }

    public void inicioApp(){
        String mensaje1 = "\nIngresa la opción: ";
        String mensaje2 = "Debe ingresar un dato valido 1 al 5:";
        int opcionUsuario;
        do{
            mostrarMenu();
            opcionUsuario = solicitaOpcionTeclado(mensaje1, mensaje2);
            switch (opcionUsuario){

                case 1:
                    BuscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                default:
                    break;
            }

        }while (opcionUsuario != 0);
   }

   public void BuscarLibroPorTitulo(){
       String libroPorTeclado = solicitaLibroTeclado();
       //System.out.println(libroPorTeclado);
       var json = consumoApi.obtenerDatos(URL_BASE+libroPorTeclado.replace(" ", "%20"));
       //System.out.println(URL_BASE);
       //System.out.println("Prueba de JSON: " + json);
       dataResultado = conversor.obtenerDatos(json, Resultado.class);
       //String libroPorTeclado = "Great Expectation";
       Optional<DatosLibro> dLibro = dataResultado.libroresultado().stream()
               .filter(d -> d.titulo().toUpperCase().contains(libroPorTeclado.toUpperCase()))
               //.filter(d -> d.titulo().equalsIgnoreCase(libroPorTeclado))
               .findFirst();

       if(dLibro.isPresent()) {
           datosLibro = dLibro.get();
           Optional <Libro> libroBuscadoBD = Optional.ofNullable(libroRepository.findByTitulo(datosLibro.titulo()));

           if(libroBuscadoBD.isEmpty()){ //VACIO BD
               //libro = dlibro.get();
               libro = new Libro(datosLibro);
               //System.out.println(libro.getTitulo()+ libro.getAutor() +" MOSTRADOS");
               Optional<DatosAutor> dAutor = datosLibro.autor().stream().findFirst();

               if (dAutor.isPresent()) {
                   datosAutor = dAutor.get();
                   Optional<Autor> autorBuscadoBD= Optional.ofNullable(autorRepository.findByNombre(datosAutor.nombre()));

                   if(autorBuscadoBD.isEmpty()){ //VACIO BD
                       autor = new Autor(datosAutor);
                       autorRepository.save(autor);
                       libro.setAutor(autor);
                       libroRepository.save(libro);
                   }
                   else{
                       System.out.println("El Autor existe en la Base de Datos");
                       autor = autorBuscadoBD.get();
                       libro.setAutor(autor);
                       libroRepository.save(libro);
                   }
                 //  System.out.println("Autor existe " + dAutor);
                   // List<Autor> listaAutor = Arrays.asList(autor);

               } else {
                   System.out.println("Autor no existe en la API");
               }
           }
           else{
               System.out.println("Existe Libro en la Base de datos Local");
           }

       } else {
           System.out.println("Libro NO encontrado en la API");
       }
   }

   public void mostrarMenu(){
        String menu = """
                ------------------------------
                    MANTENEDOR API LIBROS
                (1) - Buscar libro por titulo
                (2) - Listar Libros registrados
                (3) - Listar Autores registrados
                (4) - Listar Autores vivos determinados año
                (5) - Listar Libro por idioma
                (0) - Salir
                -------------------------------""";
       System.out.println(menu);
   }

    public Integer solicitaOpcionTeclado(String mensaje1, String mensaje2) {
        int op=999;

        try {
            System.out.print(mensaje1);
            op = teclado.nextInt();

        } catch (Exception e) {
             System.out.println(mensaje2 + e);

        }
        teclado.nextLine();
        return op;
    }

    public String solicitaLibroTeclado() {
        String solicitaLibro="";

        while (solicitaLibro.isEmpty()) {
            try {
                System.out.print("\nIngrese nombre del libro: ");
                solicitaLibro = teclado.nextLine();

                if (solicitaLibro.isEmpty()) {
                    throw new IllegalArgumentException("La entrada no puede estar vacía, ingrese nuevamente");
                }
            } catch (Exception e) {
                System.out.println("Debe ingresar un dato valido 1 al 5: " + e);
            }
        }
        return solicitaLibro;
    }

    public void listarLibrosRegistrados(){

        List<Libro> librosRegistrados = libroRepository.findAll();
        System.out.println("Total libros en la Base de Datos: "+ librosRegistrados.size());
        librosRegistrados.forEach(System.out::println);

    }

    public void listarAutoresRegistrados(){

        List<Autor> autoresRegistrados = autorRepository.findAll();
        System.out.println("Total autores encontrados: "+autoresRegistrados.size());
        autoresRegistrados.forEach(System.out::println);

    }

    public void listarAutoresVivos(){
        String mensaje1 = "\nIngrese año, para ver si autor estaba vivo: ";
        String mensaje2 = "Año inválido";

       // System.out.println("Ingrese año, para ver si autor estaba vivo");
        int anio = solicitaOpcionTeclado(mensaje1, mensaje2);
        //anio =  Integer.parseInt(teclado.next());
        List<Autor> autoresVivos  = autorRepository.buscaAutorVivoanio(anio);
        if (autoresVivos.isEmpty()){
            System.out.println("No hay autores vivos en año especificado\n");
        }
        else{
            System.out.println("Total autores encontrados: "+autoresVivos.size());
            autoresVivos.forEach(System.out::println);
        }

    }
    public void listarLibrosPorIdioma(){
        String idioma = "*";
        String mensaje1 = """
                    ------------------------------
                    IDIOMAS LIBROS (ingrese 1 al 4)
                    (1) -> es - Español
                    (2) -> en - Inglés
                    (3) -> fr - Francés
                    (4) -> pt - Portugués
                    """;
        String mensaje2 = "Opcion de idioma incorrecta\n";
        int opcion = solicitaOpcionTeclado(mensaje1,mensaje2);
        switch (opcion){
            case 1:
                idioma = "es";
                break;
            case 2:
                idioma = "en";
                break;
            case 3:
                idioma = "fr";
                break;
            case 4:
                idioma = "pt";
                break;
            default:
                System.out.println("Opcion no encontrada");
                break;

        }
       // Libro idioma2 = libro;
        List<Libro> libroIdioma = libroRepository.buscaLibroPorIdioma(idioma);
        if(libroIdioma.isEmpty()){
            System.out.println("libros no encontrados");
        }
        else{
            System.out.println("Total Libros encontrados: "+ libroIdioma.size());
            libroIdioma.forEach(System.out::println);
        }


    }




}
