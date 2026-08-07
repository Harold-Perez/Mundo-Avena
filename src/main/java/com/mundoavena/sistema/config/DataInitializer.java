package com.mundoavena.sistema.config;

import com.mundoavena.sistema.model.Bodega;
import com.mundoavena.sistema.model.Producto;
import com.mundoavena.sistema.repository.ProductoRepository;
import com.mundoavena.sistema.model.Rol;
import com.mundoavena.sistema.model.Usuario;
import com.mundoavena.sistema.repository.BodegaRepository;
import com.mundoavena.sistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Administrador Sistema");
            admin.setRol(Rol.ADMINISTRADOR);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario admin creado correctamente");
        }

        sembrarBodega("grano-groat", "Silo Groat", "🌾", true, "plana", 600_000L);
        sembrarBodega("grano-avena", "Silo Avena con Cáscara", "🌾", true, "plana", 600_000L);
        sembrarBodega("descascarado", "Descascarado", "🌰", true, "plana", null);
        sembrarBodega("cascara", "Cáscara", "🍂", true, "sin-lotes", null);
        sembrarBodega("bodega-b", "Bodega B", "🐄", true, "ubicacion", null);
        sembrarBodega("harina-otw", "Harina OTW", "🥣", true, "plana", null);
        sembrarBodega("producto-terminado", "Producto Terminado", "📦", true, "categoria", null);
        sembrarBodega("materia-prima", "Materia Prima", "🏷️", true, "categoria", null);
    }

    private void sembrarBodega(String slug, String nombre, String icono, boolean manejaLotes, String tipoAgrupacion, Long capacidadKg) {
        if (bodegaRepository.findBySlug(slug).isEmpty()) {
            Bodega bodega = new Bodega();
            bodega.setSlug(slug);
            bodega.setNombre(nombre);
            bodega.setIcono(icono);
            bodega.setManejaLotes(manejaLotes);
            bodega.setTipoAgrupacion(tipoAgrupacion);
            bodega.setCapacidadKg(capacidadKg);
            bodegaRepository.save(bodega);
            System.out.println("✅ Bodega creada: " + nombre);
        }


        sembrarProductos();
    }

    private void sembrarProductos() {
        sembrarProducto("grano-groat", "Grano Groat", null, "kg");
        sembrarProducto("grano-avena", "Grano Avena con Cáscara", null, "kg");

        sembrarProducto("descascarado", "Puntilla", null, "sacos");
        sembrarProducto("descascarado", "Avenilla", null, "sacos");
        sembrarProducto("descascarado", "Fibra", null, "sacos");
        sembrarProducto("descascarado", "Maxis Sacos", null, "sacos");

        sembrarProducto("cascara", "Afrecho", null, "sacos");

        sembrarProducto("bodega-b", "Hojuela Mosh Consumo Animal", null, "sacos");

        sembrarProducto("harina-otw", "Harina OTW", null, "sacos");

        sembrarProducto("producto-terminado", "Avena Mosh 600g", "Avena Mosh", "sacos");
        sembrarProducto("producto-terminado", "Avena Mosh 900g", "Avena Mosh", "sacos");
        sembrarProducto("producto-terminado", "Avena Mosh 1200g", "Avena Mosh", "sacos");
        sembrarProducto("producto-terminado", "Bobina 600 gramos", "Bobina Mosh Quaquer", "unidades");
        sembrarProducto("producto-terminado", "Bobina 900 gramos", "Bobina Mosh Quaquer", "unidades");
        sembrarProducto("producto-terminado", "Bobina Avena Frescos 600g", "Bobina Avena para Frescos", "unidades");
        sembrarProducto("producto-terminado", "Rico Mosh", "Rico Mosh", "unidades");

        // ---- Materia Prima (Material de Empaque) ----
        sembrarProducto("materia-prima", "Bobina Avena Estrella 360 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Estrella 1 Kilo", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Mosh Empaque Antiguo 300 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Fortificada 454 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Crema de Avena 100 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Integral 300 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Integral 40 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobinas Transparentes PMA", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Mosh Roja 300 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Roja 40 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Perlada 165 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Steel Cut 40 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Mosh Empaque Antiguo Naranja 150 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Fortificada 300 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Fortificada 150 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina Avena Fortificada 40 Gramos", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobinas Transparentes No Funcional", "Bobinas", "kg");
        sembrarProducto("materia-prima", "Bobina para Pruebas Transparente", "Bobinas", "kg");

        sembrarProducto("materia-prima", "Sacos Blancos PMA", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos Celestes Fondo Plano", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos Azul Mix", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos Estrella", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos de Segunda de Afrecho", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos Afrecho Blancos", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos Maxi", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Sacos Blancos Laminados", "Sacos", "unidades");
        sembrarProducto("materia-prima", "Saco Blanco Laminado 50 Libras", "Sacos", "unidades");

        sembrarProducto("materia-prima", "Bolsas RTD", "Bolsas", "unidades");
        sembrarProducto("materia-prima", "Bolsas para Arroz", "Bolsas", "unidades");

        sembrarProducto("materia-prima", "Carbonato", "Vitamina", "unidades");
        sembrarProducto("materia-prima", "Vitamina", "Vitamina", "unidades");

        sembrarProducto("materia-prima", "Bobina Quaker", "Bobina Quaker 600g", "kg");
        sembrarProducto("materia-prima", "Bobina Mosh Quaker", "Bobina Quaker 600g", "kg");
        sembrarProducto("materia-prima", "Bobina para Frescos 600 Gramos", "Bobina Quaker 600g", "kg");

        sembrarProducto("materia-prima", "Bobina Nutremas 1200 Gramos", "Bobina Quaker 1200g", "kg");

        sembrarProducto("materia-prima", "Bobina Nutremas 900 Gramos", "Bobina Quaker 900g", "kg");

        sembrarProducto("materia-prima", "Bobina Rico Mosh 360 Gramos", "Bobina Rico Mosh 360g", "kg");
        sembrarProducto("materia-prima", "Bobina Nutremas 360 Gramos", "Bobina Rico Mosh 360g", "kg");
    }

    private void sembrarProducto(String slugBodega, String nombre, String categoria, String unidad) {
        Bodega bodega = bodegaRepository.findBySlug(slugBodega).orElse(null);
        if (bodega == null) return;

        boolean yaExiste = productoRepository.findByBodegaAndActivoTrue(bodega).stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
        if (yaExiste) return;

        Producto producto = new Producto();
        producto.setBodega(bodega);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setUnidadMedida(unidad);
        producto.setActivo(true);
        productoRepository.save(producto);
        System.out.println("✅ Producto creado: " + nombre + " (" + bodega.getNombre() + ")");
    }
    }
