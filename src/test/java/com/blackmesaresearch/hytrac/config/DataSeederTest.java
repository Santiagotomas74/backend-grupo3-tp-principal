package com.blackmesaresearch.hytrac.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.blackmesaresearch.hytrac.repository.ProvinciaRepository;
import com.blackmesaresearch.hytrac.repository.UsuarioRepository;

@SpringBootTest
@ActiveProfiles("test") 
public class DataSeederTest {

    @Autowired
    private DataSeeder dataSeeder;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Aca solo vamos a verificar por ahora el cargar los Seeder. Verificar la función principal. 
    // Si queremos subir el porcentaje, habria que testear la gestión de errores de datos..es largo.
    @Test
    void testRun_DebePoblarLaBaseDeDatos() throws Exception {
        // Ejecutamos el seeder manualmente
        dataSeeder.run();

        // Verificamos que los datos fueron guardados en la BD en memoria
        assertTrue(provinciaRepository.count() > 0, "Debería haber cargado provincias");
        assertTrue(usuarioRepository.count() > 0, "Debería haber cargado usuarios");
    }
}