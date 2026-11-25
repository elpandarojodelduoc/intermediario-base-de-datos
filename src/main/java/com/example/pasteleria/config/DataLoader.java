package com.example.pasteleria.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.pasteleria.models.entities.Categoria;
import com.example.pasteleria.models.entities.Producto;
import com.example.pasteleria.models.entities.Rol;
import com.example.pasteleria.models.entities.Usuario;
import com.example.pasteleria.repositories.CategoriaRepository;
import com.example.pasteleria.repositories.ProductoRepository;
import com.example.pasteleria.repositories.UsuarioRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatosIniciales(CategoriaRepository categoriaRepo, ProductoRepository productoRepo) {
        return args -> {
            if (categoriaRepo.count() > 0 || productoRepo.count() > 0) {
                return;
            }

            Categoria[] c = new Categoria[9]; // se usan las posiciones 1..8

            c[1] = categoriaRepo.save(categoria("Tortas Cuadradas", "Ideales para compartir en celebraciones grandes.", "/img/torta-cuadrada-chocolate.webp"));
            c[2] = categoriaRepo.save(categoria("Tortas Circulares", "Clásicas, suaves y con rellenos tradicionales.", "/img/torta-circular-vainilla.png"));
            c[3] = categoriaRepo.save(categoria("Postres Individuales", "Una porción perfecta para ti.", "/img/mousse-chocolate.webp"));
            c[4] = categoriaRepo.save(categoria("Productos Sin Azúcar", "Dulzura sin azúcar añadida.", "/img/torta-naranja-sin-azucar.webp"));
            c[5] = categoriaRepo.save(categoria("Pastelería Tradicional", "Recetas de siempre, horneadas cada mañana.", "/img/empanada-manzana.png"));
            c[6] = categoriaRepo.save(categoria("Productos Sin Gluten", "Aptos para celíacos, sin perder el sabor.", "/img/brownie-sin-gluten.jpg"));
            c[7] = categoriaRepo.save(categoria("Productos Veganos", "Sin ingredientes de origen animal.", "/img/torta-vegana-chocolate.jpg"));
            c[8] = categoriaRepo.save(categoria("Tortas Especiales", "Para cumpleaños, bodas y ocasiones únicas.", "/img/torta-boda.jpeg"));

            productoRepo.save(producto("Torta Cuadrada de Chocolate", "Bizcocho húmedo de cacao con ganache de chocolate semi amargo.", 45000, "/img/torta-cuadrada-chocolate.webp", 12, 4, 0, true, c[1]));
            productoRepo.save(producto("Torta Cuadrada de Frutas", "Bizcocho de vainilla con crema chantilly y frutas de la estación.", 50000, "/img/torta-cuadrada-frutas.png", 8, 3, 0, false, c[1]));
            productoRepo.save(producto("Torta Circular de Vainilla", "Tres capas de bizcocho de vainilla rellenas con crema pastelera.", 40000, "/img/torta-circular-vainilla.png", 10, 4, 0, true, c[2]));
            productoRepo.save(producto("Torta Circular de Manjar", "Panqueque y manjar artesanal con nueces, receta de la casa.", 42000, "/img/torta-circular-manjar.png", 9, 3, 20, false, c[2]));
            productoRepo.save(producto("Mousse de Chocolate", "Mousse aireado de chocolate belga en vaso individual.", 5000, "/img/mousse-chocolate.webp", 30, 8, 15, true, c[3]));
            productoRepo.save(producto("Tiramisú Clásico", "Café, mascarpone y bizcocho de soletilla, estilo italiano.", 5500, "/img/tiramisu-clasico.jpg", 3, 6, 10, false, c[3]));
            productoRepo.save(producto("Torta Sin Azúcar de Naranja", "Bizcocho cítrico endulzado con stevia, sin azúcar añadida.", 48000, "/img/torta-naranja-sin-azucar.webp", 6, 3, 0, true, c[4]));
            productoRepo.save(producto("Cheesecake Sin Azúcar", "Cheesecake cremoso con base de almendras y endulzante natural.", 47000, "/img/cheesecake-sin-azucar.webp", 5, 3, 0, false, c[4]));
            productoRepo.save(producto("Empanada de Manzana", "Masa hojaldrada rellena de manzana y canela.", 3000, "/img/empanada-manzana.png", 40, 10, 10, false, c[5]));
            productoRepo.save(producto("Tarta de Santiago", "Tarta de almendras de origen gallego, sin harina de trigo.", 6000, "/img/tarta-santiago.jpg", 14, 5, 0, false, c[5]));
            productoRepo.save(producto("Brownie Sin Gluten", "Brownie denso de chocolate preparado con harina de arroz.", 4000, "/img/brownie-sin-gluten.jpg", 25, 8, 0, false, c[6]));
            productoRepo.save(producto("Pan Sin Gluten", "Pan de molde esponjoso para celíacos.", 3500, "/img/pan-sin-gluten.jpg", 18, 6, 0, false, c[6]));
            productoRepo.save(producto("Torta Vegana de Chocolate", "Torta de cacao con bebida vegetal y cobertura de ganache vegano.", 50000, "/img/torta-vegana-chocolate.jpg", 4, 3, 0, false, c[7]));
            productoRepo.save(producto("Galletas Veganas de Avena", "Pack de galletas de avena y pasas, sin huevo ni lácteos.", 4500, "/img/galletas-veganas-avena.jpg", 35, 10, 25, false, c[7]));
            productoRepo.save(producto("Torta Especial de Cumpleaños", "Torta personalizable con mensaje y decoración a elección.", 55000, "/img/torta-cumpleanos.webp", 7, 3, 0, true, c[8]));
            productoRepo.save(producto("Torta Especial de Boda", "Torta de varios pisos con decoración floral para tu gran día.", 60000, "/img/torta-boda.jpeg", 2, 2, 0, false, c[8]));
        };
    }

    @Bean
    CommandLineRunner cargarUsuariosIniciales(UsuarioRepository usuarioRepo, PasswordEncoder encoder,
            @Value("${app.seed.admin-password:admin123}") String claveAdmin) {
        return args -> {
            if (usuarioRepo.count() > 0) {
                return;
            }
            usuarioRepo.save(usuario("Administrador", "Mil Sabores", "admin@milsabores.cl", encoder.encode(claveAdmin),
                    Rol.ADMIN, LocalDate.of(1980, 5, 10), "+56 9 1111 2222", 0, false));
            usuarioRepo.save(usuario("Camila", "Rojas Soto", "camila@duocuc.cl", encoder.encode("camila123"),
                    Rol.CLIENTE, LocalDate.of(2002, 8, 21), "+56 9 3333 4444", 0, true));
            usuarioRepo.save(usuario("Don Hernán", "Pérez Lagos", "hernan@correo.cl", encoder.encode("hernan123"),
                    Rol.CLIENTE, LocalDate.of(1958, 3, 2), "+56 9 5555 6666", 50, false));
        };
    }

    private static Usuario usuario(String nombre, String apellidos, String correo, String claveHash, Rol rol,
            LocalDate nacimiento, String telefono, int descuento, boolean tortaCumple) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        u.setClave(claveHash);
        u.setRol(rol);
        u.setNacimiento(nacimiento);
        u.setTelefono(telefono);
        u.setDescuento(descuento);
        u.setTortaCumple(tortaCumple);
        u.setActivo(true);
        return u;
    }

    private static Categoria categoria(String nombre, String descripcion, String imagen) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setImagen(imagen);
        return c;
    }

    private static Producto producto(String nombre, String descripcion, int precio, String imagen,
            int stock, int stockCritico, int oferta, boolean destacado, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setImagen(imagen);
        p.setStock(stock);
        p.setStockCritico(stockCritico);
        p.setOferta(oferta);
        p.setDestacado(destacado);
        p.setCategoria(categoria);
        return p;
    }
}
