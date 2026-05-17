package com.blackmesaresearch.hytrac.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.blackmesaresearch.hytrac.dto.csv.CombustibleCsv;
import com.blackmesaresearch.hytrac.dto.csv.LocalidadCsv;
import com.blackmesaresearch.hytrac.dto.csv.ProvinciaCsv;
import com.blackmesaresearch.hytrac.model.lookup.Permiso;
import com.blackmesaresearch.hytrac.model.lookup.Rol;
import com.blackmesaresearch.hytrac.model.reference.*;
import com.blackmesaresearch.hytrac.repository.*;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProvinciaRepository provinciaRepo;
    private final LocalidadRepository localidadRepo;
    private final CombustibleRepository combustibleRepo;
    private final RolRepository rolRepo;
    private final PermisoRepository permisoRepo;
    private final CsvMapper csvMapper = new CsvMapper();

    public DataSeeder(
            ProvinciaRepository provinciaRepo,
            LocalidadRepository localidadRepo,
            CombustibleRepository combustibleRepo,
            RolRepository rolRepo,
            PermisoRepository permisoRepo) {
        this.provinciaRepo = provinciaRepo;
        this.localidadRepo = localidadRepo;
        this.combustibleRepo = combustibleRepo;
        this.rolRepo = rolRepo;
        this.permisoRepo = permisoRepo;
    }

    // Flujo principal - Define el orden en el que se cargan las tablas
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed if the database is empty (since you nuke the DB often)
        if (provinciaRepo.count() > 0)
            return;

        log.info("Starting CSV Seeding...");

        // 1. Load Provincias
        Map<String, Provincia> provinciaMap = loadProvincias();

        // 2. Load Localidades
        loadLocalidades(provinciaMap);

        // 3. Load Combustibles
        loadCombustibles();

        // 4. Load Roles
        Map<String, Rol> rolMap = loadRoles();

        // 5. Load Permissions
        Map<String, Permiso> permisoMap = loadPermisos();

        // 6. Load Rol-Permission relationships
        loadRolPermisos(rolMap, permisoMap);

        log.info("Seeding complete!");
    }

    private Map<String, Provincia> loadProvincias() throws IOException {
        InputStream is = new ClassPathResource("seed/provincias.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<ProvinciaCsv> it = csvMapper.readerFor(ProvinciaCsv.class)
                .with(schema).readValues(is);

        List<Provincia> saved = new ArrayList<>();
        it.forEachRemaining(row -> {
            Provincia p = new Provincia();
            p.setNombre(row.getNombre());
            saved.add(provinciaRepo.save(p));
        });

        // Create a lookup map: "Buenos Aires" -> Provincia Object (with its new ID)
        return saved.stream().collect(Collectors.toMap(Provincia::getNombre, p -> p));
    }

    private void loadLocalidades(Map<String, Provincia> provinciaMap) throws IOException {
        InputStream is = new ClassPathResource("seed/localidades.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<LocalidadCsv> it = csvMapper.readerFor(LocalidadCsv.class)
                .with(schema).readValues(is);

        it.forEachRemaining(row -> {
            Localidad loc = new Localidad();
            loc.setNombre(row.getNombre());
            loc.setCodigoPostal(row.getCodigo_postal());

            // This is the magic: link by name from our map
            Provincia parent = provinciaMap.get(row.getProvincia_nombre());
            if (parent != null) {
                loc.setProvincia(parent);
                localidadRepo.save(loc);
            }
        });
    }

    private void loadCombustibles() throws IOException {
        InputStream is = new ClassPathResource("seed/combustible.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<CombustibleCsv> it = csvMapper.readerFor(CombustibleCsv.class)
                .with(schema).readValues(is);

        it.forEachRemaining(row -> {
            Combustible c = new Combustible();
            c.setNombre(row.getNombre());
            c.setNumeroOnu(row.getNumero_onu());
            c.setClaseRiesgo(row.getClase_riesgo());
            c.setDensidad(Double.parseDouble(row.getDensidad()));
            c.setTemperaturaReferencia(Double.parseDouble(row.getTemperatura_referencia()));
            combustibleRepo.save(c);
        });
    }

    private Map<String, Rol> loadRoles() throws IOException {
        InputStream is = new ClassPathResource("seed/roles.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<Rol> it = csvMapper.readerFor(Rol.class)
                .with(schema).readValues(is);

        List<Rol> saved = new ArrayList<>();
        it.forEachRemaining(row -> {
            saved.add(rolRepo.save(row));
        });

        return saved.stream().collect(Collectors.toMap(Rol::getNombre, r -> r));
    }

    private Map<String, Permiso> loadPermisos() throws IOException {
        InputStream is = new ClassPathResource("seed/permisos.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<Permiso> it = csvMapper.readerFor(Permiso.class)
                .with(schema).readValues(is);

        List<Permiso> saved = new ArrayList<>();
        it.forEachRemaining(row -> {
            saved.add(permisoRepo.save(row));
        });

        return saved.stream().collect(Collectors.toMap(Permiso::getCodigo, p -> p));
    }

    private void loadRolPermisos(Map<String, Rol> rolMap, Map<String, Permiso> permisoMap) throws IOException {
        InputStream is = new ClassPathResource("seed/rol_permisos.csv").getInputStream();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<Map<String, String>> it = csvMapper.readerFor(Map.class)
                .with(schema).readValues(is);

        while (it.hasNext()) {
            Map<String, String> row = it.next();
            String rolNombre = row.get("rol_nombre");
            String permisoNombre = row.get("permiso_nombre");

            Rol rol = rolMap.get(rolNombre);
            Permiso permiso = permisoMap.get(permisoNombre);

            if (rol != null && permiso != null) {
                // 1. Add the permission to the Set inside the Rol
                if (rol.getPermisos() == null) {
                    rol.setPermisos(new HashSet<>());
                }
                rol.getPermisos().add(permiso);

                // 2. Save the Rol. JPA will automatically update the Rol_Permiso table.
                rolRepo.save(rol);
            } else {
                log.warn("Skipping link: Rol {} or Permiso {} not found", rolNombre, permisoNombre);
            }
        }
    }

}
